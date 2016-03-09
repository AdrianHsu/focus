package com.dots.focus.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dots.focus.config.FriendRelationship;
import com.dots.focus.util.FetchFriendUtil;
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

public class GetFriendInviteService extends Service {
    private final IBinder mBinder = new GetFriendInviteBinder();
    private Timer timer = null;
    private static final String TAG = "GetFriendInviteService";
    public static ArrayList<JSONObject> friendWaitingReplyList = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (timer != null) timer.cancel();
        timer = new Timer();
        timer.schedule(new CheckFriendInvitation(), 0, 60000);
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

    class CheckFriendInvitation extends TimerTask {
        public void run() {
            Log.d(TAG, "checkCycle...");
            refresh();
        }
    }

    public class GetFriendInviteBinder extends Binder {
        GetFriendInviteService getService() {
            return GetFriendInviteService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static void refresh() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)    return;
        Log.d(TAG, "start GetFriendInviteService run...");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendInvitation");
        query.whereEqualTo("user_id_invited", currentUser.getLong("user_id"));
        query.whereEqualTo("downloaded", false);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> inviteList, ParseException e) {
                if (e == null && inviteList != null && !inviteList.isEmpty()) {
                    for (int i = 0, size = inviteList.size(); i < size; ++i) {
                        ParseObject invite = inviteList.get(i);
                        long id = invite.getLong("user_id_inviting");
                        String name = invite.getString("user_name_inviting");

                        if (!FetchFriendUtil.mConfirmingFriendList.contains(id))
                            FetchFriendUtil.mConfirmingFriendList.add(id);

                        invite.put("downloaded", true);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            FetchFriendUtil.checkRemoveMFL(id);

                            jsonObject.put("id", id);
                            jsonObject.put("name", name);
                            jsonObject.put("time", invite.getLong("time"));
                            jsonObject.put("state", FriendRelationship.FRIEND_INVITED.getValue());
                            friendWaitingReplyList.add(jsonObject);

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
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendInvitation");
        query.whereEqualTo("user_id_invited", currentUser.getLong("user_id"));
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> inviteList, ParseException e) {

                if (e == null && inviteList != null && !inviteList.isEmpty()) {
                    friendWaitingReplyList.clear();
                    for (int i = 0, size = inviteList.size(); i < size; ++i) {
                        JSONObject jsonObject = new JSONObject();
                        ParseObject invite = inviteList.get(i);
                        Log.d(TAG, "checkLocal id: " + invite.getLong("user_id_inviting"));
                        try {
                            jsonObject.put("id", invite.getLong("user_id_inviting"));
                            jsonObject.put("name", invite.getString("user_name_inviting"));
                            jsonObject.put("time", invite.getLong("time"));
                            jsonObject.put("state", FriendRelationship.FRIEND_INVITED.getValue());
                            friendWaitingReplyList.add(jsonObject);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public static void removeWaitingReplyList(Long id) {
    /*
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
    */

        for (int i = 0, length = friendWaitingReplyList.size(); i < length; ++i) {
            try {
                if (friendWaitingReplyList.get(i).getLong("id") == id) {
                    friendWaitingReplyList.remove(i);
                    return;
                }
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
        }
        Log.d(TAG, "removeRepliedList: cannot find id : " + id);
    }
}
