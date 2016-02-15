package com.dots.focus.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dots.focus.config.FriendRelationship;
import com.dots.focus.util.FetchFriendUtil;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GetFriendConfirmService extends Service {
    private final IBinder mBinder = new GetFriendConfirmBinder();
    private static final String TAG = "GetFriendConfirmService";
    public static ArrayList<JSONObject> friendRepliedList = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timer timer = new Timer();
        timer.schedule(new CheckFriendConfirmation(), 0, 60000);
        return 0;
    }

    class CheckFriendConfirmation extends TimerTask {
        public void run() {
            Log.d(TAG, "checkCycle...");
            refresh();
        }
    }

    public class GetFriendConfirmBinder extends Binder {
        GetFriendConfirmService getService() {
            return GetFriendConfirmService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static void refresh() {
      ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendConfirmation");
      query.whereEqualTo("user_id_inviting", ParseUser.getCurrentUser().getLong("user_id"));
      query.whereEqualTo("downloaded", false);
      query.findInBackground(new FindCallback<ParseObject>() {
        public void done(List<ParseObject> inviteList, ParseException e) {
          if (e == null && inviteList != null) {
            for (int i = 0, size = inviteList.size(); i < size; ++i) {
              ParseObject invite = inviteList.get(i);
              invite.put("downloaded", true);
              JSONObject jsonObject = new JSONObject();
              try {
                Long id = invite.getLong("user_id_invited");
                String name = invite.getString("user_name_invited");

                jsonObject.put("id", id);
                jsonObject.put("name", name);
                jsonObject.put("time", invite.getLong("time"));
                jsonObject.put("state", FriendRelationship.FRIEND_CONFIRMED.getValue());
                jsonObject.put("objectId", invite.getObjectId());
                friendRepliedList.add(jsonObject);

                FetchFriendUtil.getFriendConfirm(id, name);
              } catch (JSONException e1) {
                Log.d(TAG, e1.getMessage());
              }
            }
            ParseObject.deleteAllInBackground(inviteList);
          }
        }
      });
    }

    public static void checkLocal() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendConfirmation");
        query.whereEqualTo("user_id_inviting", ParseUser.getCurrentUser().getLong("user_id"));
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> inviteList, ParseException e) {
                if (e == null && inviteList != null && !inviteList.isEmpty()) {
                    friendRepliedList.clear();
                    for (int i = 0, size = inviteList.size(); i < size; ++i) {
                        JSONObject jsonObject = new JSONObject();
                        ParseObject invite = inviteList.get(i);
                        long id = invite.getLong("user_id_inviting");
                        try {
                            jsonObject.put("id", id);
                            jsonObject.put("name", invite.getString("user_name_inviting"));
                            jsonObject.put("time", invite.getLong("time"));
                            jsonObject.put("state", FriendRelationship.FRIEND_CONFIRMED.getValue());
                            jsonObject.put("objectId", invite.getObjectId());
                            friendRepliedList.add(jsonObject);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public static void removeRepliedList(final String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendConfirmation");
        query.fromLocalDatastore();
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null && parseObject != null)
                    parseObject.unpinInBackground();

                else if (e != null)
                    Log.d(TAG, "Cannot find FriendConfirmation whose objectId is : " + objectId);
            }
        });
    }
}
