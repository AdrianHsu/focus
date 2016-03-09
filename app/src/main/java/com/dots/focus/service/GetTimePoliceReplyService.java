package com.dots.focus.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dots.focus.config.TimePoliceState;
import com.dots.focus.util.TimePoliceUtil;
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

public class GetTimePoliceReplyService extends Service {
    private final IBinder mBinder = new GetTimePoliceReplyBinder();
    private Timer timer = null;
    private static String TAG = "GetTimePoliceReplyService";
    public static ArrayList<JSONObject> timePoliceReplyList = new ArrayList<>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "GetTimePoliceReplyService start...");
        if (timer != null) timer.cancel();
        timer = new Timer();
        timer.schedule(new CheckTimePoliceReply(), 0, 60000);

        return 0;
    }
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy...");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }

    class CheckTimePoliceReply extends TimerTask {
        public void run() {
            refresh();
        }
    }

    public class GetTimePoliceReplyBinder extends Binder {
        GetTimePoliceReplyService getService() {
            return GetTimePoliceReplyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static void refresh() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)    return;
        Log.d(TAG, "start GetTimePoliceReplyService run...");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TimePoliceInvitation");
        query.whereEqualTo("user_id_inviting", currentUser.getLong("user_id"));
        query.whereEqualTo("state", TimePoliceState.REPLY_NOT_DOWNLOADED.getValue());

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> inviteList, ParseException e) {
                if (e == null && inviteList != null && !inviteList.isEmpty()) {
                    for (int i = 0, size = inviteList.size(); i < size; ++i) {
                        ParseObject object = inviteList.get(i);
                        JSONObject invitation = new JSONObject();
                        boolean reply = object.getBoolean("reply");
                        Long id = object.getLong("user_id_invited");
                        String name = object.getString("user_name_invited");
                        int lock_time = object.getInt("lock_time");

                        TimePoliceUtil.getReply(id);
                        if (reply)
                            TimePoliceUtil.timePoliceConfirm(id, name, lock_time);

                        object.put("state", TimePoliceState.REPLY_DOWNLOADED.getValue());
                        try {
                            invitation.put("id", id);
                            invitation.put("name", name);
                            invitation.put("time", object.getLong("time"));
                            invitation.put("lock_time", lock_time);
                            invitation.put("reply", reply);
                            invitation.put("state", TimePoliceState.REPLY_DOWNLOADED.getValue()
                                    + TimePoliceUtil.timePoliceStateOffset);
                            invitation.put("objectId", object.getObjectId());
                            timePoliceReplyList.add(invitation);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                    ParseObject.deleteAllInBackground(inviteList);
                    ParseObject.pinAllInBackground(inviteList);
                }
            }
        });
    }

    public static void checkLocal() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)    return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TimePoliceInvitation");
        query.whereEqualTo("user_id_inviting", currentUser.getLong("user_id"));
        query.whereEqualTo("state", TimePoliceState.REPLY_DOWNLOADED.getValue());
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null && !objects.isEmpty()) {
                    timePoliceReplyList.clear();
                    for (int i = 0, size = objects.size(); i < size; ++i) {
                        JSONObject invitation = new JSONObject();
                        ParseObject object = objects.get(i);
                        try {
                            invitation.put("id", object.getLong("user_id_inviting"));
                            invitation.put("name", object.getString("user_name_inviting"));
                            invitation.put("time", object.getLong("time"));
                            invitation.put("lock_time", object.getLong("lock_time"));
                            invitation.put("reply", object.getBoolean("reply"));
                            invitation.put("state", TimePoliceState.REPLY_DOWNLOADED.getValue()
                                    + TimePoliceUtil.timePoliceStateOffset);
                            invitation.put("objectId", object.getObjectId());
                            timePoliceReplyList.add(invitation);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public static void removeReplyList(Long id, final String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TimePoliceInvitation");
        query.fromLocalDatastore();
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
          @Override
          public void done(ParseObject parseObject, ParseException e) {
            if (e == null && parseObject != null)
              parseObject.unpinInBackground();

            else if (e != null)
              Log.d(TAG, "Cannot find TimePoliceInvitation whose objectId is : " + objectId);
          }
        });

        for (int i = 0, length = timePoliceReplyList.size(); i < length; ++i) {
            try {
                if (timePoliceReplyList.get(i).getLong("id") == id) {
                    timePoliceReplyList.remove(i);
                    return;
                }
            } catch (JSONException e) { Log.d(TAG, e.getMessage()); }
        }
        Log.d(TAG, "removeReplyList: cannot find id: " + id);
    }
}
