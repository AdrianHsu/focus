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
      query.whereEqualTo("user_id_inviting", ParseUser.getCurrentUser().getLong("id"));
      query.findInBackground(new FindCallback<ParseObject>() {
        public void done(List<ParseObject> inviteList, ParseException e) {
          if (e == null && inviteList != null) {
            friendRepliedList.clear();

            for (int i = 0, size = inviteList.size(); i < size; ++i) {

              // showFriendConfirm(inviteList.get(i).getString("user_id_invited"),
              //                  inviteList.get(i).getString("time"));
              JSONObject jsonObject = new JSONObject();
              try {
                Long id = inviteList.get(i).getLong("user_id_invited");
                String name = inviteList.get(i).getString("user_name_invited");
                jsonObject.put("id", id);
                jsonObject.put("name", name);
                jsonObject.put("time", inviteList.get(i).getLong("time"));
                jsonObject.put("state", 2);
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
    class CheckFriendConfirmation extends TimerTask {
        public void run() {
            refresh();
        }
    }
}
