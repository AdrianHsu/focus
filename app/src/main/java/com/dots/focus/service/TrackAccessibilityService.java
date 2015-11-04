package com.dots.focus.service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.dots.focus.util.TrackAccessibilityUtil;
import com.parse.ParseObject;

public class TrackAccessibilityService extends AccessibilityService {

    public static String currentPackageName = "";
    //public static String tempPackageName = "";
    public static long startTime = 0;
    public static long startHour = 0;
    public static final String TAG = "MyService";

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

        long now = TrackAccessibilityUtil.getTimeInMilli();

        if(startTime == 0 || currentPackageName.contentEquals("")){
            startTime = now;
            startHour = TrackAccessibilityUtil.anHour * (now / TrackAccessibilityUtil.anHour);
            currentPackageName = tempPackageName;
            return;
        }

        ParseObject temp = new ParseObject("appUsage");
        temp.put("appName", currentPackageName);
        temp.put("startTime", startTime);
        temp.put("endTime", now);
        temp.pinInBackground();
        temp.saveEventually();
        TrackAccessibilityUtil.getCurrentHour(startTime).getList("appUsages").add(temp.getObjectId());

        startTime = now;
        currentPackageName = tempPackageName;
    }
}
