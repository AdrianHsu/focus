package com.dots.focus.util;

import android.util.Log;

import com.dots.focus.ui.IdleSettingsActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SettingsUtil {
    public static ParseObject settings = null;

    private static String TAG = "SettingsUtil";

    static {
        Log.d(TAG, "static runned...");
        // searchSettings();
    }

    //通知設定
    public static void searchSettings() {
        Log.d(TAG, "searchSettings start...");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Settings");
        query.fromLocalDatastore();
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                Log.d(TAG, "searchSettings done...");
                if (e == null && parseObject != null) {
                    settings = parseObject;
                    String idleApps = settings.getString("idleApps");
                    if (idleApps != null) {
                        try {
                            JSONArray array = new JSONArray(idleApps);
                            int length = array.length();
                            Integer[] idles = new Integer[length];
                            for (int i = 0; i < length; ++i)
                                idles[i] = array.getInt(i);
                            IdleSettingsActivity.setDefaultMultiChoice(idles);
                        } catch (JSONException e1) { Log.d(TAG, e1.getMessage()); }
                    }
                }
                else {
                    if (e != null)
                        Log.d(TAG, e.getMessage());
                    else
                        Log.d(TAG, "No Settings stored...");

                    defaultSettings();
                }
            }
        });
    }
    public static void defaultSettings() {
        settings = new ParseObject("Settings");
        settings.put("goal", 120);  // 目標設定
        settings.put("idle", 20);   // 耍廢條件設定
        settings.put("lock", 15);   // 鎖屏功能設定

        JSONArray array = new JSONArray();
        settings.put("idleApps", array.toString());

        // 通知設定
            // 每日報表
        settings.put("dailyReportPush", false);
        settings.put("dailyReportTime", 22 * 3600); // 10:00 p.m.
            // 通知方式
        settings.put("sound", false);
        settings.put("vibrate", false);
            // 訊息顯示
        settings.put("screenOn", false);
        settings.put("screenOff", false);
        settings.put("friendLock", true);
        settings.put("kickRequest", "我手機玩太久～快戳我一下QQ");
        settings.put("kickHistory", "你被戳了一下！不要再當低頭族囉^_^");
        settings.put("kickResponse", "謝謝你戳了我一下。");

        // 進階設定

        settings.saveEventually();
        settings.pinInBackground();
    }

    public static void put(String s, boolean bool) {
        settings.put(s, bool);
        Log.d(TAG, "Set " + s + ": " + bool);
    }
    public static void put(String s, int i) {
        settings.put(s, i);
        Log.d(TAG, "Set " + s + ": " + i);
    }
    public static void put(String s, String i) {
        settings.put(s, i);
        Log.d(TAG, "Set " + s + ": " + i);
    }
    public static boolean getBooleen(String s) {
        return settings.getBoolean(s);
    }
    public static int getInt(String s) { // default 0
        return settings.getInt(s);
    }
    public static String getString(String s) {
        return settings.getString(s);
    }

    public static void setIdles(Integer[] idles) {
        JSONArray array = new JSONArray();
        for (int idle : idles)
            array.put(idle);
        settings.put("idleApps", array.toString());
    }
}
