package com.dots.focus.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dots.focus.config.KickState;
import com.dots.focus.util.FetchFriendUtil;
import com.dots.focus.util.KickUtil;
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

public class GetKickRequestService extends Service {
    private final IBinder mBinder = new GetKickRequestBinder();
    private static String TAG = "GetKickRequestService";
    public static ArrayList<JSONObject> friendKickRequestList = new ArrayList<>();
    public static ArrayList<JSONObject> friendWaitingKickResponse = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "GetKickRequestService start...");

        Timer timer = new Timer();
        timer.schedule(new CheckKickRequest(), 0, 60000);

        return 0;
    }

    class CheckKickRequest extends TimerTask {
        public void run() {
            queryKickRequest();
        }
    }

    public class GetKickRequestBinder extends Binder {
        GetKickRequestService getService() {
            return GetKickRequestService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static void queryKickRequest() {
        final long expire_time = System.currentTimeMillis() + KickUtil.expire_period;
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("KickHistory");
        query.whereEqualTo("user_id_kicking", currentUser.getLong("user_id"));
        query.whereEqualTo("state", KickState.REQUEST_NOT_DOWNLOADED.getValue());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null && !objects.isEmpty()) {
                    for (int i = 0, size = objects.size(); i < size; ++i) {
                        JSONObject kickMessage = new JSONObject();
                        objects.get(i).put("state", KickState.REQUEST_DOWNLOADED.getValue());
                        ParseObject object = objects.get(i);
                        long id = object.getLong("user_id_kicked"),
                             time1 = object.getLong("time1");
                        // add to friendKickRequestList
                        try {
                            kickMessage.put("user_id", id);
                            kickMessage.put("user_name", FetchFriendUtil.getFriendName(id));
                            kickMessage.put("state", object.getLong("state"));
                            kickMessage.put("LimitType", object.getInt("LimitType"));
                            kickMessage.put("period", object.getInt("period"));
                            kickMessage.put("time1", time1);
                            kickMessage.put("content1", object.getString("content1"));
                            kickMessage.put("expired", time1 > expire_time);
                            kickMessage.put("objectId", object.getObjectId());
                            friendKickRequestList.add(kickMessage);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                    ParseObject.saveAllInBackground(objects);
                    ParseObject.pinAllInBackground(objects);
                }
            }
        });
    }

    public static void checkLocal() {
        final long expire_time = System.currentTimeMillis() + KickUtil.expire_period;
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("KickHistory");
        query.whereEqualTo("user_id_kicking", currentUser.getLong("user_id"));
        query.whereEqualTo("state", KickState.REQUEST_DOWNLOADED.getValue());
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null && !objects.isEmpty()) {
                    friendKickRequestList.clear();
                    for (int i = 0, size = objects.size(); i < size; ++i) {
                        JSONObject kickMessage = new JSONObject();
                        ParseObject object = objects.get(i);
                        long id = object.getLong("user_id_kicked"),
                             time1 = object.getLong("time1");
                        // add to friendKickRequestList
                        try {
                            kickMessage.put("user_id", id);
                            kickMessage.put("user_name", FetchFriendUtil.getFriendName(id));
                            kickMessage.put("state", object.getLong("state"));
                            kickMessage.put("LimitType", object.getInt("LimitType"));
                            kickMessage.put("period", object.getInt("period"));
                            kickMessage.put("time1", time1);
                            kickMessage.put("content1", object.getString("content1"));
                            kickMessage.put("expired", time1 > expire_time);
                            kickMessage.put("objectId", object.getObjectId());
                            friendKickRequestList.add(kickMessage);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        checkWaitingKickResponse();
    }
    private static void checkWaitingKickResponse() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("KickHistory");
        query.whereEqualTo("user_id_kicking", currentUser.getLong("user_id"));
        query.whereEqualTo("state", KickState.KICK_NOT_DOWNLOADED.getValue());
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null && !objects.isEmpty()) {
                    friendWaitingKickResponse.clear();
                    for (int i = 0, size = objects.size(); i < size; ++i) {
                        JSONObject kickMessage = new JSONObject();
                        ParseObject object = objects.get(i);
                        long id = object.getLong("user_id_kicked");
                        // add to friendKickRequestList
                        try {
                            kickMessage.put("user_id", id);
                            kickMessage.put("user_name", FetchFriendUtil.getFriendName(id));
                            kickMessage.put("state", object.getLong("state"));
                            kickMessage.put("LimitType", object.getInt("LimitType"));
                            kickMessage.put("period", object.getInt("period"));
                            kickMessage.put("time1", object.getLong("time1"));
                            kickMessage.put("content1", object.getString("content1"));
                            kickMessage.put("objectId", object.getObjectId());
                            friendWaitingKickResponse.add(kickMessage);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
