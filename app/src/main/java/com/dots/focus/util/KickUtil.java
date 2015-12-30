package com.dots.focus.util;

import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

public class KickUtil {

    public static void sendKickRequest(int limitType, int period, long time, String content) {
        ParseObject kickRequest = new ParseObject("KickRequest");
        kickRequest.put("id", ParseUser.getCurrentUser().getLong("id"));
        kickRequest.put("limitType", limitType);
        kickRequest.put("period", period);
        kickRequest.put("time", time);
        kickRequest.put("content", content);

        kickRequest.saveEventually();
    }
    public static void kick(long id, String name, String content, long period, String AppName,
                            String AppPackageName) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseObject kickHistory = new ParseObject("KickHistory");
        kickHistory.put("period", period);
        kickHistory.put("AppName", AppName);
        kickHistory.put("AppPackageName", AppPackageName);
        kickHistory.put("user_id_kicked", id);
        kickHistory.put("user_name_kicked", name);
        kickHistory.put("user_id_kicking", currentUser.getLong("id"));
        kickHistory.put("user_name_kicking", currentUser.getString("name"));
        kickHistory.put("time2", System.currentTimeMillis());
        kickHistory.put("content2", content);
        kickHistory.put("state", 0);

        kickHistory.saveEventually();
    }
    public static void kickResponse(ParseObject kickHistory, String content) {
        kickHistory.put("time3", System.currentTimeMillis());
        kickHistory.put("content3", content);
        kickHistory.put("state", 2);

        kickHistory.saveEventually();
    }
}
