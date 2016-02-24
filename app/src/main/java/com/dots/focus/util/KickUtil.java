package com.dots.focus.util;

import android.util.Log;

import com.dots.focus.config.KickState;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

public class KickUtil {
    private static String TAG = "KickUtil";
    public static long expire_period = 300000;

    public static void sendKickRequest(int limitType, int period, long time, String content) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseObject kickRequest = new ParseObject("KickRequest");
        kickRequest.put("user_id", currentUser.getLong("user_id"));
        kickRequest.put("LimitType", limitType);
        kickRequest.put("period", period);
        kickRequest.put("time", time);
        kickRequest.put("content", content);
        kickRequest.put("state", KickState.REQUEST_NOT_DOWNLOADED.getValue());
        kickRequest.put("userId", currentUser.getObjectId());
        kickRequest.put("lock_max_period", currentUser.getInt("lock_max_period"));

        kickRequest.saveEventually();
    }
    public static void kick(final String content, String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("KickHistory");
        query.fromLocalDatastore();
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject kickHistory, ParseException e) {
                if (e == null && kickHistory != null) {
                    kickHistory.put("time2", System.currentTimeMillis());
                    kickHistory.put("content2", content);
                    kickHistory.put("state", KickState.KICK_NOT_DOWNLOADED.getValue());

                    kickHistory.saveEventually();
                } else if (e != null) {
                    Log.d(TAG, e.getMessage());
                }
            }
        });
    }
    public static void kickResponse(final String content, String objectId) {
      ParseQuery<ParseObject> query = ParseQuery.getQuery("KickHistory");
      query.fromLocalDatastore();
      query.getInBackground(objectId, new GetCallback<ParseObject>() {
        @Override
        public void done(ParseObject kickHistory, ParseException e) {
          if (e == null && kickHistory != null) {
            kickHistory.put("time3", System.currentTimeMillis());
            kickHistory.put("content3", content);
            kickHistory.put("state", KickState.RESPONSE_NOT_DOWNLOADED.getValue());
            kickHistory.saveEventually();
          } else if (e != null) {
            Log.d(TAG, e.getMessage());
          }
        }
      });
    }
    public static void deleteKickHistory(String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("KickHistory");
        query.fromLocalDatastore();
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null && parseObject != null) {
                    parseObject.unpinInBackground();
                    parseObject.deleteEventually();
                }
                else if (e != null)
                  Log.d(TAG, "deleteParseObject: " + e.getMessage());
            }
        });
    }
}
