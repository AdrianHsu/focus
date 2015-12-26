package com.dots.focus.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GetFriendConfirmService extends Service {
    private final IBinder mBinder = new GetFriendConfirmBinder();
    private final String TAG = "GetFriendInviteService";

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

    class CheckFriendConfirmation extends TimerTask {
        public void run() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendConfirmation");
            query.whereEqualTo("user_id_inviting", ParseUser.getCurrentUser().getLong("id"));
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> inviteList, ParseException e) {
                    if (e == null && inviteList != null) {
                        for (int i = 0, size = inviteList.size(); i < size; ++i) {
                            // showFriendConfirm(inviteList.get(i).getString("user_id_invited"),
                            //                  inviteList.get(i).getString("time"));
                        }
                        ParseObject.deleteAllInBackground(inviteList);
                    }
                }
            });
        }
    }
}
