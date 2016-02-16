package com.dots.focus.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.dots.focus.config.LimitType;
import com.dots.focus.model.AppInfo;
import com.dots.focus.model.DayBlock;
import com.dots.focus.model.HourBlock;
import com.dots.focus.util.FetchAppUtil;
import com.dots.focus.util.KickUtil;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TrackAccessibilityService extends AccessibilityService {

    public static String previousPackageName = "";
    public static long startTime = 0;
    public static long startHour = 0;
    public static final String TAG = "TrackService";
    public static List<String> ignore = new ArrayList<>();
    private static int appIndex = -1;
    private static int[] appsUsage = {0, 0, 0, 0, 0, 0};
    private static long blockTime = 0;


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("HourReceiver_broadcast_an_hour")) {
                Log.d(TAG, "Get HourReceiver's broadcast...");
                checkWindowState(previousPackageName, intent.getExtras().getLong("time"));
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction("HourReceiver_broadcast_an_hour");

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
        int count = 0;
        blockTime = System.currentTimeMillis();
        if (appIndex >= 0) {
            appsUsage[5] += (int)((blockTime - startTime) / 1000);
        }

        for (int i = 0; i < 6; ++i)
            count += appsUsage[i];
//        if (count >= 1200) // 20 mins
          if(count >= 10) {
            KickUtil.sendKickRequest(LimitType.HOUR_LIMIT.getValue(), count, blockTime,
                                    "我在耍廢，快來踢我！");
          }

        List<Integer> appLength = TrackAccessibilityUtil.getCurrentDay(blockTime).getAppLength();
        count = 0;
        for (int i = 0, size = appLength.size(); i < size; ++i)
            count += appLength.get(i);

        if (count >= 7200) // 2 hours
            KickUtil.sendKickRequest(LimitType.DAY_LIMIT.getValue(), count, blockTime,
                    "我在耍廢，快來踢我！");

        /*for (int i = 0; i < 5; ++i)
            appsUsage[i] = appsUsage[i + 1];*/
        System.arraycopy(appsUsage, 1, appsUsage, 0, 5);
        appsUsage[5] = 0;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.v(TAG, "***** onAccessibilityEvent");
            final String tempPackageName = event.getPackageName().toString();
            Log.d(TAG, "getPackageName: " + tempPackageName);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    checkWindowState(tempPackageName, System.currentTimeMillis());
                }
            };
            thread.start();
        }
    }

    @Override
    public void onInterrupt() {
        Log.v(TAG, "***** onInterrupt");
    }

    public void checkWindowState(String tempPackageName, long now) {

        if (startTime == 0 || previousPackageName.contentEquals("") || appIndex < 0) {
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
        Log.d(TAG, "appIndex: " + appIndex);
        storeInDatabase(now);

        startTime = now;
        previousPackageName = tempPackageName;

        if (!checkIndex(tempPackageName)) {
            startTime = now;
        }
    }
    // helper functions
    private void storeInDatabase (long now) {
        int duration = (int)((now - startTime) / 1000), blockDuration = duration;
        if (blockTime > startTime)  blockDuration = (int)((now - blockTime) / 1000);
        appsUsage[5] += blockDuration;

        if (appIndex < 0)   return;
        int endIndex = TrackAccessibilityUtil.getCurrentHour(startTime).getInt("endIndex"),
            AppIndex = ParseUser.getCurrentUser().getInt("AppIndex");
        if (endIndex != AppIndex)
            Log.d("TAG", "Different index, endIndex: " + endIndex + ", AppIndex: " + AppIndex);

        final ParseObject temp = new ParseObject("AppUsage");
        ParseUser user = ParseUser.getCurrentUser();
        temp.put("User", user);
        temp.put("appIndex", appIndex);
        temp.put("startTime", startTime);
        temp.put("duration", duration);
        temp.put("index", AppIndex);

        temp.pinInBackground();

        HourBlock hour = TrackAccessibilityUtil.getCurrentHour(startTime);
        DayBlock day = TrackAccessibilityUtil.getCurrentDay(startTime);

        clickCount(appIndex, day);

        if (endIndex > AppIndex)    AppIndex = endIndex;
        ++AppIndex;
        hour.put("endIndex", AppIndex);
        if (AppIndex > user.getInt("AppIndex"))
            user.put("AppIndex", AppIndex);

        List<Integer> appLength = hour.getList("appLength");
        int size = appLength.size();
        while (size <= appIndex) {
            appLength.add(0);
            ++size;
        }
        Log.d(TAG, "hour appLength.get: " + appLength.get(appIndex) + ", duration: " + duration);
        appLength.set(appIndex, appLength.get(appIndex) + duration);
        hour.put("appLength", appLength);
        Log.d(TAG, "Hour appLength.get: " + hour.getList("appLength").get(appIndex));

        appLength = day.getList("appLength");
        size = appLength.size();
        while (size <= appIndex) {
            appLength.add(0);
            ++size;
        }

        Log.d(TAG, "day appLength.get: " + appLength.get(appIndex) + ", duration: " + duration);
        appLength.set(appIndex, appLength.get(appIndex) + duration);
        day.put("appLength", appLength);
        Log.d(TAG, "Hour appLength.get: " + hour.getList("appLength").get(appIndex));

        Log.d(TAG, "appName: " + previousPackageName + ", startTime: " + startTime + ", duration: "
                + duration);
    }

    private void clickCount(int i, DayBlock dayBlock) {
        AppInfo appInfo = FetchAppUtil.getApp(i);
        if (appInfo == null)    return;
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
        }
        else {
            resizeAppLength();
        }
    }

    private static void resizeAppLength() {
        long now = System.currentTimeMillis();
        HourBlock hourBlock = TrackAccessibilityUtil.getCurrentHour(now);
        DayBlock  dayBlock  = TrackAccessibilityUtil.getCurrentDay(now);

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

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        //Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        if (Build.VERSION.SDK_INT >= 16)
            //Just in case this helps
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
    }




}