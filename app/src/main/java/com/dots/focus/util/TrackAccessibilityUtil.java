package com.dots.focus.util;

import android.content.Intent;
import android.util.Log;

import com.dots.focus.service.GetAppsService;

import java.util.TimeZone;

/**
 * Created by Harvey Yang on 2015/9/30.
 */
public class TrackAccessibilityUtil {


    public static long getTimeInMilli(){
        return TimeZone.getDefault().getOffset(System.currentTimeMillis());
    }
}
