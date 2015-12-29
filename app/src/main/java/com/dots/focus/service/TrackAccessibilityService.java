package com.dots.focus.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.dots.focus.model.AppInfo;
import com.dots.focus.model.DayBlock;
import com.dots.focus.model.HourBlock;
import com.dots.focus.util.FetchAppUtil;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TrackAccessibilityService extends AccessibilityService {

    public static String previousPackageName = "";
    public static long startTime = 0;
    public static long startHour = 0;
    public static final String TAG = "TrackService";
    public static List<String> ignore = new ArrayList<>();
    private ParseObject currentApp;
    private int appIndex;

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

        if (startTime == 0 || previousPackageName.contentEquals("")) {
            startTime = now;
            startHour = TrackAccessibilityUtil.anHour * (now / TrackAccessibilityUtil.anHour);
            previousPackageName = tempPackageName;
            checkIndex(tempPackageName);
            return;
        }
        if (!checkIndex(tempPackageName)) {
            previousPackageName = "";
            startTime = now;
            return;
        }

        Log.d(TAG, "appIndex: " + appIndex);

        while (now > startHour + TrackAccessibilityUtil.anHour) {
            //storeInDatabase((int)((startHour + TrackAccessibilityUtil.anHour - startTime) /
            //        1000), index);
            startTime = startHour = startHour + TrackAccessibilityUtil.anHour;
        }
        //storeInDatabase((int) ((now - startTime) / 1000), index);
        startTime = now;
        previousPackageName = tempPackageName;

        updateApp(true);
    }
    // helper functions
    void storeInDatabase(final int duration, final int index){
        int endIndex = TrackAccessibilityUtil.getCurrentHour(startTime).getInt("endIndex"),
            AppIndex = ParseUser.getCurrentUser().getInt("AppIndex");
        if (endIndex != AppIndex)
            Log.d("TAG", "Different index, endIndex: " + endIndex + ", AppIndex: " + AppIndex);

        final ParseObject temp = new ParseObject("AppUsage");
        temp.put("User", ParseUser.getCurrentUser());
        temp.put("appIndex", index);
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
        Log.d(TAG, "hour appLength.get: " + appLength.get(index) + ", duration: " + duration);
        appLength.set(index, appLength.get(index) + duration);
        hour.put("appLength", appLength);
        Log.d(TAG, "Hour appLength.get: " + hour.getList("appLength").get(index));

        appLength = day.getList("appLength");
        Log.d(TAG, "day appLength.get: " + appLength.get(index) + ", duration: " + duration);
        appLength.set(index, appLength.get(index) + duration);
        day.put("appLength", appLength);
        Log.d(TAG, "Hour appLength.get: " + hour.getList("appLength").get(index));

        Log.d(TAG, "appName: " + previousPackageName + ", startTime: " + startTime + ", duration: " + duration);
    }

    boolean newPackageName() {
        return false;
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
        return appIndex != -1;
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

    private ParseObject getCurrentApp() throws com.parse.ParseException {
        if (currentApp != null) return currentApp;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("CurrentApp");
        currentApp = query.getFirst();

        if (currentApp == null) {
            currentApp = new ParseObject("CurrentApp");
            currentApp.put("id", ParseUser.getCurrentUser().getLong("id"));
            currentApp.put("name", ParseUser.getCurrentUser().getString("name"));
        }

        return currentApp;
    }

    private void updateApp(boolean valid) {
        try {
            if (valid) {
                AppInfo appInfo = FetchAppUtil.getApp(previousPackageName);
                if (appInfo != null) {
                    Log.d(TAG, "updateApp: " + appInfo.getName());
                    getCurrentApp().put("App", appInfo.getName());
                    getCurrentApp().put("time", startTime);
                    getCurrentApp().saveEventually();
                    return;
                }
            }
            Log.d(TAG, "updateApp: Empty...");
                    getCurrentApp().put("App", "");
            getCurrentApp().saveEventually();
        } catch (com.parse.ParseException e) { e.getMessage(); }
    }
}