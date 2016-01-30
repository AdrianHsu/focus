package com.dots.focus.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

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
  private static final String TAG = "GetFriendInviteService";
  public static ArrayList<JSONObject> friendWaitingReplyList = new ArrayList<>();

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(TAG, "GetFriendInviteService start...");
    updateList(); // friendWaitingReplyList.clear();
    Timer timer = new Timer();
    timer.schedule(new CheckFriendInvitation(), 0, 60000);
    return 0;
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
    Log.d(TAG, "start GetFriendInviteService run...");
    ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendInvitation");
    query.whereEqualTo("user_id_invited", ParseUser.getCurrentUser().getLong("user_id"));
    query.whereEqualTo("downloaded", false);

    query.findInBackground(new FindCallback<ParseObject>() {
      public void done(List<ParseObject> inviteList, ParseException e) {
        if (e == null && inviteList != null && !inviteList.isEmpty()) {
//          friendWaitingReplyList.clear();
          Log.d(TAG, "inviteList.size() == " + inviteList.size());
          for (int i = 0, size = inviteList.size(); i < size; ++i) {
            long id = inviteList.get(i).getLong("user_id_inviting");
            String name = inviteList.get(i).getString("user_name_inviting");

            if (!FetchFriendUtil.mConfirmingFriendList.contains(id))
              FetchFriendUtil.mConfirmingFriendList.add(id);

            inviteList.get(i).put("downloaded", true);
            JSONObject jsonObject = new JSONObject();
            try {
              FetchFriendUtil.checkRemoveMFL(id);

              jsonObject.put("id", id);
              jsonObject.put("name", name);
//              Log.d(TAG, "user_name_inviting == " + jsonObject.getString("name"));
              jsonObject.put("time", inviteList.get(i).getLong("time"));
              jsonObject.put("state", 1);
              friendWaitingReplyList.add(jsonObject);

            } catch (JSONException e1) {
              e1.printStackTrace();
            }
          }
          try {
            ParseObject.saveAll(inviteList);
            ParseObject.pinAll(inviteList);
          } catch (ParseException e1) {
//            Log.d(TAG, e1.getMessage());
          }
        }
      }
    });
  }

  class CheckFriendInvitation extends TimerTask {
    public void run() {
      refresh();
    }
  }

  public static void updateList() {
    friendWaitingReplyList.clear();

    ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendInvitation");
    query.whereEqualTo("user_id_invited", ParseUser.getCurrentUser().getLong("user_id"));
    query.fromLocalDatastore();
    query.findInBackground(new FindCallback<ParseObject>() {
      public void done(List<ParseObject> inviteList, ParseException e) {

        if (e == null && inviteList != null) {
          Log.d("GetFriendInviteService", "download = true: inviteList.size() == " + inviteList
                  .size());
//          if (!inviteList.isEmpty())
//            friendWaitingReplyList.clear();
          for (int i = 0, size = inviteList.size(); i < size; ++i) {
            JSONObject jsonObject = new JSONObject();

            try {
              jsonObject.put("user_id", inviteList.get(i).getLong
                ("user_id_inviting"));
              jsonObject.put("user_name", inviteList.get(i).getString
                ("user_name_inviting"));
              Log.d("GetFriendInviteService", "user_name_inviting == " + jsonObject.getString("name"));
              jsonObject.put("time", inviteList.get(i).getLong("time"));
              jsonObject.put("invite", false);

              friendWaitingReplyList.add(jsonObject);

            } catch (JSONException e1) {
              e1.printStackTrace();
            }
          }
        }
      }
    });
  }
}
