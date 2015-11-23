package com.dots.focus.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.dots.focus.util.FetchAppUtil;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class TrackAccessibilityService extends AccessibilityService {

    public static String currentPackageName = "";
    //public static String tempPackageName = "";
    public static long startTime = 0;
    public static long startHour = 0;
    public static final String TAG = "TrackService";
    public static List<String> ignore = new ArrayList<>();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.v(TAG, "***** onAccessibilityEvent");
            final String tempPackageName = event.getPackageName().toString();

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

        if (startTime == 0 || currentPackageName.contentEquals("")) {
            startTime = now;
            startHour = TrackAccessibilityUtil.anHour * (now / TrackAccessibilityUtil.anHour);
            currentPackageName = tempPackageName;
            return;
        }
        for (int i = 0; i < ignore.size(); ++i) {
            if (currentPackageName.equals(ignore.get(i))) {
                startTime = now;
                return;
            }
        }
        int index = FetchAppUtil.getAppIndex(currentPackageName);
        if (index == 0)
            if (newPackageName())   return;

        while (now > startHour + TrackAccessibilityUtil.anHour) {
            storeInDatabase(startHour + TrackAccessibilityUtil.anHour, index);
            startTime = startHour = startHour + TrackAccessibilityUtil.anHour;
        }
        storeInDatabase(now, index);

        startTime = now;
        currentPackageName = tempPackageName;
    }
    // helper functions
    void storeInDatabase(final long endTime, final int index){
        ParseObject temp = new ParseObject("AppUsage");
        temp.put("appName", currentPackageName);
        temp.put("startTime", startTime);
        temp.put("endTime", endTime);
        temp.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ParseObject hour = TrackAccessibilityUtil.getCurrentHour(startTime);
                ParseObject day = TrackAccessibilityUtil.getCurrentDay(startTime);

                // List<String> appUsages = hour.getList("appUsages");
                // appUsages.add(temp.getObjectId());
                // hour.put("appUsages", appUsages);

                List<Integer> appLength;
                appLength = hour.getList("appLength");
                appLength.set(index, appLength.get(index) + (int)((endTime - startTime) / 1000));
                hour.put("appLength", appLength);

                appLength = day.getList("appLength");
                appLength.set(index, appLength.get(index) + (int)((endTime - startTime) / 1000));
                day.put("appLength", appLength);
            }
        });

        Log.d(TAG, "appName: " + currentPackageName + ", startTime: " + startTime + ", endTime: " + endTime);


    }

    boolean newPackageName() {
        return false;
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

