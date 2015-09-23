package com.dots.focus.service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.parse.ParseObject;

import org.json.JSONObject;

/**
 * Created by Harvey Yang on 2015/9/22.
 */
public class TrackAccessibilityService extends AccessibilityService {

    public static String currentPackageName = "";
    //public static String tempPackageName = "";
    public static long startTime = 0;
    public static final String TAG = "MyService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            Log.v(TAG, "***** onAccessibilityEvent");

            String tempPackageName = event.getPackageName().toString();
            long now = System.currentTimeMillis(); // + 時區!!!

            if(startTime == 0 || currentPackageName.contentEquals("")){
                startTime = now;
                currentPackageName = tempPackageName;
                return;
            }

            ParseObject temp = new ParseObject("appUsage");
            temp.put("appName", currentPackageName);
            temp.put("startTime", startTime);
            temp.put("endTime", now);
            temp.pinInBackground();
            JSONObject a = new JSONObject();

            startTime = now;
            currentPackageName = tempPackageName;
        }
    }

    @Override
    public void onInterrupt() {
        Log.v(TAG, "***** onInterrupt");
    }
}



