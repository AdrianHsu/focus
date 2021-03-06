package com.dots.focus.service;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.dots.focus.config.LimitType;
import com.dots.focus.model.AppInfo;
import com.dots.focus.model.DayBlock;
import com.dots.focus.model.HourBlock;
import com.dots.focus.ui.FocusModeActivity;
import com.dots.focus.ui.FocusModeView;
import com.dots.focus.util.FetchAppUtil;
import com.dots.focus.util.KickUtil;
import com.dots.focus.util.SettingsUtil;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TrackAccessibilityService extends AccessibilityService {
    public static TrackAccessibilityService service = null;
    public static boolean permissionOn = false;

    public static String previousPackageName = "";
    public static long startTime = 0;
    public static long startHour = 0;
    public static final String TAG = "TrackService";
    public static List<String> ignore = new ArrayList<>();
    private static int appIndex = -1;
    public static int[] appsUsage = {0, 0, 0, 0, 0, 0};
    private static long blockTime = 0;

    public static boolean inLockMode = false;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("HourReceiver_broadcast_an_hour")) {
                Log.d(TAG, "Get HourReceiver's broadcast...");
                final long time = intent.getExtras().getLong("time");
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        checkWindowState(previousPackageName, time);
                    }
                };
                thread.start();

            } else if (action.equals("check permission")) {
                Log.d(TAG, "get check permission");
                checkPermission();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate...");
        service = this;

        IntentFilter filter = new IntentFilter();
        filter.addAction("HourReceiver_broadcast_an_hour");
        filter.addAction("check permission");

        registerReceiver(receiver, filter);

        blockTime = System.currentTimeMillis();
        Timer timer = new Timer();
//        timer.schedule(new CheckLimitTask(), 0, 300000);
        timer.schedule(new CheckLimitTask(), 0, 15000); // 15 sec
    }


    class CheckLimitTask extends TimerTask {
        public void run() {
            checkLimit();
        }
    }


    public static void checkLimit() {
        if (ParseUser.getCurrentUser() == null) return;
        int count = 0;
        blockTime = System.currentTimeMillis();
        if (appIndex >= 0) {
            appsUsage[5] += (int) ((blockTime - startTime) / 1000);
        }

        for (int i = 0; i < 6; ++i)
            count += appsUsage[i];
//        if (count >= 1200) // 20 mins
        if (count >= 10) {
            for (int i = 0; i < 6; ++i)
                appsUsage[i] = 0;
            KickUtil.sendKickRequest(LimitType.HOUR_LIMIT.getValue(), count, blockTime,
                    SettingsUtil.getString("kickRequest"));
        }

        List<Integer> appLength = TrackAccessibilityUtil.getCurrentDay(blockTime).getAppLength();
        count = 0;
        for (int i = 0, size = appLength.size(); i < size; ++i)
            count += appLength.get(i);

        if (count >= 7200) // 2 hours
            KickUtil.sendKickRequest(LimitType.DAY_LIMIT.getValue(), count, blockTime,
                    SettingsUtil.getString("kickRequest"));

//        for (int i = 0; i < 5; ++i)
//            appsUsage[i] = appsUsage[i + 1];
        System.arraycopy(appsUsage, 1, appsUsage, 0, 5);
        appsUsage[5] = 0;
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        unregisterReceiver(receiver);
        service = null;
        super.onDestroy();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                ParseUser.getCurrentUser() != null) {
            Log.v(TAG, "***** onAccessibilityEvent");
            final String tempPackageName = event.getPackageName().toString();
            Log.d(TAG, "getPackageName: " + tempPackageName);

            if (inLockMode && !tempPackageName.equals(FocusModeView.callApp)
                    && !tempPackageName.equals(FocusModeView.messageApp)) {
                inLockMode = false;

                Intent intent = new Intent(this, FocusModeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(FocusModeService.LOCK_ACTION);
                startActivity(intent);

//                Intent intent = new Intent();
//                intent.setAction("resume lock mode");
//                sendBroadcast(intent);
            }

            Thread thread = new Thread() {
                @Override
                public void run() {
                    checkWindowState(tempPackageName, System.currentTimeMillis());
                }
            };
            thread.start();
        }
    }

    public void checkWindowState(String tempPackageName, long now) {
        if (startTime == 0 || previousPackageName.contentEquals("") || appIndex < 0 ||
                ParseUser.getCurrentUser() == null) {
            startTime = now;
            startHour = TrackAccessibilityUtil.anHour * (now / TrackAccessibilityUtil.anHour);
            previousPackageName = tempPackageName;
            checkIndex(tempPackageName);
            return;
        }

        while (now > startHour + TrackAccessibilityUtil.anHour) {
            storeInDatabase(startHour + TrackAccessibilityUtil.anHour);
            startTime = startHour = startHour + TrackAccessibilityUtil.anHour;
        }
        storeInDatabase(now);

        startTime = now;
        previousPackageName = tempPackageName;

        if (!checkIndex(tempPackageName)) {
            startTime = now;
        }
    }

    // helper functions
    private void storeInDatabase(long now) {
        int duration = (int) ((now - startTime) / 1000), blockDuration = duration;
        if (blockTime > startTime) blockDuration = (int) ((now - blockTime) / 1000);
        appsUsage[5] += blockDuration;

        if (appIndex < 0) return;
        int endIndex = TrackAccessibilityUtil.getCurrentHour(startTime).getInt("endIndex"),
                AppIndex = ParseUser.getCurrentUser().getInt("AppIndex");
        if (endIndex != AppIndex)
            Log.d("TAG", "Different index, endIndex: " + endIndex + ", AppIndex: " + AppIndex);

        ParseUser user = ParseUser.getCurrentUser();

        storeAppUsage(now, AppIndex);

        HourBlock hour = TrackAccessibilityUtil.getCurrentHour(startTime);
        DayBlock day = TrackAccessibilityUtil.getCurrentDay(startTime);

        clickCount(appIndex, day);

        if (endIndex > AppIndex) AppIndex = endIndex;
        ++AppIndex;
        hour.put("endIndex", AppIndex);
        if (AppIndex > user.getInt("AppIndex"))
            user.put("AppIndex", AppIndex);

        if (appIndex < 0)   return;
        List<Integer> appLength = hour.getList("appLength");
        while (appLength.size() <= appIndex) {
            appLength.add(0);
        }

        appLength.set(appIndex, appLength.get(appIndex) + duration);
        hour.put("appLength", appLength);

        if (appIndex < 0)   return;
        appLength = day.getList("appLength");
        while (appLength.size() <= appIndex) {
            appLength.add(0);
        }

        appLength.set(appIndex, appLength.get(appIndex) + duration);
        day.put("appLength", appLength);

        Log.d(TAG, "appName: " + previousPackageName + ", startTime: " + startTime + ", duration: "
                + duration);
    }

    private void clickCount(int i, DayBlock dayBlock) {
        AppInfo appInfo = FetchAppUtil.getApp(i);
        if (appInfo == null) return;
        int index = TrackAccessibilityUtil.getCategoryUnion(appInfo.getCategory());
        List<Integer> categoryClickToday = dayBlock.getCategoryClick();

        if (index >= 0 && index < categoryClickToday.size())
            categoryClickToday.set(index, categoryClickToday.get(index) + 1);
        dayBlock.setCategoryClick(categoryClickToday);
    }

    private boolean checkIndex(String currentPackageName) {
        for (int i = 0; i < ignore.size(); ++i) {
            Log.d(TAG, "ignore " + i + "st: " + ignore.get(i));
            if (currentPackageName.equals(ignore.get(i))) {
                Log.d(TAG, "hit");
                appIndex = -2;
                return false;
            }
        }
        appIndex = FetchAppUtil.getAppIndex(currentPackageName);
        if (appIndex == -1) {
            startService(new Intent(this, GetAppsService.class));
            return false;
        }
        return true;
    }

    public static void updateAppIndex() {
        if (appIndex != -1) return;

        appIndex = FetchAppUtil.getAppIndex(previousPackageName);
        if (appIndex == -1) {
            ignore.add(previousPackageName);
            appIndex = -2;
        } else {
            resizeAppLength();
        }
    }

    private static void resizeAppLength() {
        long now = System.currentTimeMillis();
        HourBlock hourBlock = TrackAccessibilityUtil.getCurrentHour(now);
        DayBlock dayBlock = TrackAccessibilityUtil.getCurrentDay(now);

        List<Integer> appLength1 = hourBlock.getAppLength(),
                appLength2 = dayBlock.getAppLength();
        int size = FetchAppUtil.getSize();
        if (appLength1.size() < size) {
            for (int i = appLength1.size(); i < size; ++i)
                appLength1.add(0);
            hourBlock.setAppLength(appLength1);
        }
        if (appLength2.size() < size) {
            for (int i = appLength2.size(); i < size; ++i)
                appLength2.add(0);
            dayBlock.setAppLength(appLength2);
        }
    }

    private static void storeAppUsage(long now, int AppIndex) {
        final ParseObject temp = new ParseObject("AppUsage");
        ParseUser user = ParseUser.getCurrentUser();
        temp.put("User", user);
        temp.put("appIndex", appIndex);
        temp.put("startTime", startTime);
        temp.put("endTime", now);
        temp.put("duration:", (int) ((now - startTime) / 1000));
        temp.put("index", AppIndex);

        temp.saveEventually();
        temp.pinInBackground();
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        //Configure these here for compatibility with API 13 and below.
        Log.v(TAG, "onServiceConnected...");
        permissionOn = true;
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        if (Build.VERSION.SDK_INT >= 16)
            //Just in case this helps
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "onUnbind...");
        permissionOn = false;
        return super.onUnbind(intent);
    }

    @Override
    public void onInterrupt() {
        Log.v(TAG, "***** onInterrupt");
    }

    public void checkPermission() {
        int permission1 = PermissionChecker.checkSelfPermission(this, Manifest.permission
                .BIND_ACCESSIBILITY_SERVICE);
        int permission2 = PermissionChecker.checkCallingOrSelfPermission(this, Manifest.permission
                .BIND_ACCESSIBILITY_SERVICE);
        int permission3 = PermissionChecker.checkCallingPermission(this, Manifest.permission
                .BIND_ACCESSIBILITY_SERVICE, this.getPackageName());
        Log.d(TAG, "standard: " + PermissionChecker.PERMISSION_DENIED + ", " +
                PermissionChecker.PERMISSION_DENIED_APP_OP + ", " +
                PermissionChecker.PERMISSION_GRANTED);
        Log.d(TAG, permission1 + ", " + permission2 + ", " + permission3);
    }

    public static void logOut() {
        permissionOn = false;
        previousPackageName = "";
        startTime = 0;
        startHour = 0;
        appIndex = -1;
        appsUsage = new int[] {0, 0, 0, 0, 0, 0};
        blockTime = 0;
        inLockMode = false;
    }
}