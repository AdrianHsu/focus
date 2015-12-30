package com.dots.focus.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.dots.focus.model.AppInfo;
import com.dots.focus.model.DayBlock;
import com.dots.focus.model.HourBlock;
import com.dots.focus.util.FetchAppUtil;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrackAccessibilityService extends AccessibilityService {

    public static String previousPackageName = "";
    public static long startTime = 0;
    public static long startHour = 0;
    public static final String TAG = "TrackService";
    public static List<String> ignore = new ArrayList<>();
    private static ParseObject currentApp;
    private static int appIndex;

    @Override
    public void onCreate() {
        super.onCreate();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("CurrentApp");
        query.fromLocalDatastore();
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null) {
                    currentApp = object;
                }
                else {
                    Log.d(TAG, "Cannot find the currentApp...");
                }
            }
        });

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
                    checkWindowState(tempPackageName);
                }
            };
            thread.start();
        }
    }

    @Override
    public void onInterrupt() {
        Log.v(TAG, "***** onInterrupt");
    }

    private void checkWindowState(String tempPackageName){

        long now = System.currentTimeMillis();

        if (startTime == 0 || previousPackageName.contentEquals("") || appIndex < 0) {
            startTime = now;
            startHour = TrackAccessibilityUtil.anHour * (now / TrackAccessibilityUtil.anHour);
            previousPackageName = tempPackageName;
            checkIndex(tempPackageName);
            return;
        }

        while (now > startHour + TrackAccessibilityUtil.anHour) {
            storeInDatabase((int)((startHour + TrackAccessibilityUtil.anHour - startTime) / 1000));
            startTime = startHour = startHour + TrackAccessibilityUtil.anHour;
        }
        storeInDatabase((int) ((now - startTime) / 1000));
        Log.d(TAG, "appIndex: " + appIndex);
        updateApp(true);

        startTime = now;
        previousPackageName = tempPackageName;

        if (!checkIndex(tempPackageName)) {
            startTime = now;
            if (appIndex == -2) updateApp(false);
        }
    }
    // helper functions
    private void storeInDatabase (final int duration) {
        if (appIndex < 0)   return;
        int endIndex = TrackAccessibilityUtil.getCurrentHour(startTime).getInt("endIndex"),
            AppIndex = ParseUser.getCurrentUser().getInt("AppIndex");
        if (endIndex != AppIndex)
            Log.d("TAG", "Different index, endIndex: " + endIndex + ", AppIndex: " + AppIndex);

        final ParseObject temp = new ParseObject("AppUsage");
        temp.put("User", ParseUser.getCurrentUser());
        temp.put("appIndex", appIndex);
        temp.put("startTime", startTime);
        temp.put("duration", duration);
        temp.put("index", AppIndex);

        temp.pinInBackground();

        HourBlock hour = TrackAccessibilityUtil.getCurrentHour(startTime);
        DayBlock day = TrackAccessibilityUtil.getCurrentDay(startTime);

        if (endIndex > AppIndex)    AppIndex = endIndex;
        ++AppIndex;
        hour.put("endIndex", AppIndex);
        if (AppIndex > ParseUser.getCurrentUser().getInt("AppIndex"))
            ParseUser.getCurrentUser().put("AppIndex", AppIndex);

        List<Integer> appLength = hour.getList("appLength");
        Log.d(TAG, "hour appLength.get: " + appLength.get(appIndex) + ", duration: " + duration);
        appLength.set(appIndex, appLength.get(appIndex) + duration);
        hour.put("appLength", appLength);
        Log.d(TAG, "Hour appLength.get: " + hour.getList("appLength").get(appIndex));

        appLength = day.getList("appLength");
        Log.d(TAG, "day appLength.get: " + appLength.get(appIndex) + ", duration: " + duration);
        appLength.set(appIndex, appLength.get(appIndex) + duration);
        day.put("appLength", appLength);
        Log.d(TAG, "Hour appLength.get: " + hour.getList("appLength").get(appIndex));

        Log.d(TAG, "appName: " + previousPackageName + ", startTime: " + startTime + ", duration: " + duration);
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
            updateApp(false);
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

    private static ParseObject getCurrentApp() throws com.parse.ParseException {
        if (currentApp == null) {
            Log.d(TAG, "new currentApp...");
            currentApp = new ParseObject("CurrentApp");
            currentApp.put("id", ParseUser.getCurrentUser().getLong("id"));
            currentApp.put("name", ParseUser.getCurrentUser().getString("name"));
            currentApp.saveEventually();
        } else {
            Log.d(TAG, "currentApp is not null...");
        }

        return currentApp;
    }

    private static void updateApp(boolean valid) {
        try {
            if (valid) {
                AppInfo appInfo = FetchAppUtil.getApp(previousPackageName);
                if (appInfo != null) {
                    Log.d(TAG, "updateApp: " + appInfo.getName());
                    getCurrentApp().put("AppName", appInfo.getName());
                    getCurrentApp().put("AppPackageName", appInfo.getPackageName());
                    getCurrentApp().put("time", startTime);
                    getCurrentApp().saveEventually();
                    return;
                }
            }
            Log.d(TAG, "updateApp: Empty...");
            getCurrentApp().put("AppName", "");
            getCurrentApp().put("AppPackageName", "");
            getCurrentApp().saveEventually();
        } catch (com.parse.ParseException e) { Log.d(TAG, e.getMessage()); }
    }
}