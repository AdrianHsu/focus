package com.dots.focus.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GetFriendInviteService extends Service {
    //private List<AppInfo> applicationList = new ArrayList<AppInfo>();
    private final IBinder mBinder = new GetFriendInviteBinder();
    private final String TAG = "GetFriendInviteService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "GetFriendInviteService start...");
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

    class CheckFriendInvitation extends TimerTask {

        public void run() {
            Log.d(TAG, "start GetFriendInviteService run...");
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendInvitation");
            query.whereEqualTo("user_id_invited", ParseUser.getCurrentUser().getLong("id"));
            query.whereEqualTo("downloaded", false);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> inviteList, ParseException e) {
                    if (e == null && inviteList != null) {
                        for (int i = 0, size = inviteList.size(); i < size; ++i) {
                            Log.d(TAG, String.valueOf(inviteList.get(i).getLong
                                    ("user_id_inviting")));
                            Log.d(TAG, String.valueOf(inviteList.get(i).getLong("time")));
                            inviteList.get(i).put("downloaded", true);
                            inviteList.get(i).saveEventually();
                            // showFriendInvite(inviteList.get(i).getString("user_id_inviting"),
                            //                  inviteList.get(i).getString("time"));
                        }
                    }
                }
            });}
    }
}
