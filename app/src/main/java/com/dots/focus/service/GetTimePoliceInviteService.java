package com.dots.focus.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dots.focus.config.TimePoliceState;
import com.dots.focus.util.TimePoliceUtil;
import com.parse.FindCallback;
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

public class GetTimePoliceInviteService extends Service {
    private final IBinder mBinder = new GetTimePoliceInviteBinder();
    private Timer timer = null;
    private static String TAG = "GetTimePoliceInviteService";
    public static ArrayList<JSONObject> timePoliceInviteList = new ArrayList<>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "GetTimePoliceInviteService start...");
        if (timer != null) timer.cancel();
        timer = new Timer();
        timer.schedule(new CheckTimePoliceInvite(), 0, 60000);

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

    class CheckTimePoliceInvite extends TimerTask {
        public void run() {
            refresh();
        }
    }

    public class GetTimePoliceInviteBinder extends Binder {
        GetTimePoliceInviteService getService() {
            return GetTimePoliceInviteService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static void refresh() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)    return;
        Log.d(TAG, "start GetTimePoliceInviteService run...");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TimePoliceInvitation");
        query.whereEqualTo("user_id_invited", currentUser.getLong("user_id"));
        query.whereEqualTo("state", TimePoliceState.INVITE_NOT_DOWNLOADED.getValue());

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> inviteList, ParseException e) {
                if (e == null && inviteList != null && !inviteList.isEmpty()) {
                    for (int i = 0, size = inviteList.size(); i < size; ++i) {
                        ParseObject object = inviteList.get(i);
                        Long id = object.getLong("user_id_inviting");
                        TimePoliceUtil.invitedIdList.add(id);

                        object.put("state", TimePoliceState.INVITE_DOWNLOADED.getValue());
                        JSONObject invitation = new JSONObject();
                        try {
                            // FetchFriendUtil.checkRemoveMFL(id);

                            invitation.put("id", id);
                            invitation.put("name", object.getString("user_name_inviting"));
                            invitation.put("time", object.getLong("time"));
                            invitation.put("lock_time", object.getInt("lock_time"));
                            invitation.put("state", TimePoliceState.INVITE_DOWNLOADED.getValue()
                                    + TimePoliceUtil.timePoliceStateOffset);
                            invitation.put("objectId", object.getObjectId());
                            timePoliceInviteList.add(invitation);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                    ParseObject.saveAllInBackground(inviteList);
                    ParseObject.pinAllInBackground(inviteList);
                }
            }
        });
    }

    public static void checkLocal() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)    return;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TimePoliceInvitation");
        query.whereEqualTo("user_id_invited", currentUser.getLong("user_id"));
        query.whereEqualTo("state", TimePoliceState.INVITE_DOWNLOADED.getValue());
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null && !objects.isEmpty()) {
                    timePoliceInviteList.clear();
                    for (int i = 0, size = objects.size(); i < size; ++i) {
                        JSONObject invitation = new JSONObject();
                        ParseObject object = objects.get(i);
                        // add to kickedList
                        try {
                            invitation.put("id", object.getLong("user_id_inviting"));
                            invitation.put("name", object.getString("user_name_inviting"));
                            invitation.put("time", object.getLong("time"));
                            invitation.put("lock_time", object.getLong("lock_time"));
                            invitation.put("state", TimePoliceState.INVITE_DOWNLOADED.getValue()
                                    + TimePoliceUtil.timePoliceStateOffset);
                            invitation.put("objectId", object.getObjectId());
                            timePoliceInviteList.add(invitation);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public static void removeInviteList(Long id) {
        for (int i = 0, length = timePoliceInviteList.size(); i < length; ++i) {
            try {
                if (timePoliceInviteList.get(i).getLong("id") == id) {
                    timePoliceInviteList.remove(i);
                    return;
                }
            } catch (JSONException e) { Log.d(TAG, e.getMessage()); }
        }
        Log.d(TAG, "removeInviteList: cannot find id: " + id);
    }
}
