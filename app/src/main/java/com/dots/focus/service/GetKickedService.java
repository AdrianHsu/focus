package com.dots.focus.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dots.focus.config.KickState;
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

public class GetKickedService extends Service {
    private final IBinder mBinder = new GetKickedBinder();
    private static String TAG = "GetKickedService";
    public static ArrayList<JSONObject> kickedList = new ArrayList<>();
    public static ArrayList<JSONObject> respondList = new ArrayList<>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "GetKickedService start...");

        Timer timer = new Timer();
        timer.schedule(new CheckKicked(), 0, 60000);

        return 0;
    }

    class CheckKicked extends TimerTask {
        public void run() {
            queryKicked();
        }
    }

    public class GetKickedBinder extends Binder {
        GetKickedService getService() {
            return GetKickedService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static void queryKicked() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("KickHistory");
        query.whereEqualTo("user_id_kicked", ParseUser.getCurrentUser().getLong("user_id"));
        query.whereEqualTo("state", KickState.KICK_NOT_DOWNLOADED.getValue());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null && !objects.isEmpty()) {
                    for (int i = 0, size = objects.size(); i < size; ++i) {
                        objects.get(i).put("state", KickState.KICK_DOWNLOADED.getValue());
                        JSONObject kickMessage = new JSONObject();
                        ParseObject object = objects.get(i);
                        long id = object.getLong("user_id_kicking");
                        // add to kickedList
                        try {
                            kickMessage.put("user_id", id);
                            kickMessage.put("user_name", FetchFriendUtil.getFriendName(id));
                            kickMessage.put("state", object.getLong("state"));
                            kickMessage.put("LimitType", object.getInt("LimitType"));
                            kickMessage.put("period", object.getInt("period"));
                            kickMessage.put("time1", object.getLong("time1"));
                            kickMessage.put("content1", object.getString("content1"));
                            kickMessage.put("time2", object.getLong("time2"));
                            kickMessage.put("content2", object.getString("content2"));
                            kickMessage.put("is_me", false);
                            kickMessage.put("objectId", object.getObjectId());
                            kickedList.add(kickMessage);
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
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("KickHistory");
        query.whereEqualTo("user_id_kicked", currentUser.getLong("user_id"));
        query.whereEqualTo("state", KickState.KICK_DOWNLOADED.getValue());
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null && !objects.isEmpty()) {
                    kickedList.clear();
                    for (int i = 0, size = objects.size(); i < size; ++i) {
                        JSONObject kickMessage = new JSONObject();
                        ParseObject object = objects.get(i);
                        long id = object.getLong("user_id_kicking");
                        // add to kickedList
                        try {
                            kickMessage.put("user_id", id);
                            kickMessage.put("user_name", FetchFriendUtil.getFriendName(id));
                            kickMessage.put("state", object.getLong("state"));
                            kickMessage.put("LimitType", object.getInt("LimitType"));
                            kickMessage.put("period", object.getInt("period"));
                            kickMessage.put("time1", object.getLong("time1"));
                            kickMessage.put("content1", object.getString("content1"));
                            kickMessage.put("time2", object.getLong("time2"));
                            kickMessage.put("content2", object.getString("content2"));
                            kickMessage.put("is_me", false);
                            kickMessage.put("objectId", object.getObjectId());
                            kickedList.add(kickMessage);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        checkRespondHistory();
    }
    private static void checkRespondHistory() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("KickHistory");
        query.whereEqualTo("user_id_kicked", currentUser.getLong("user_id"));
        query.whereEqualTo("state", KickState.RESPONSE_NOT_DOWNLOADED.getValue());
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null && !objects.isEmpty()) {
                    respondList.clear();
                    for (int i = 0, size = objects.size(); i < size; ++i) {
                        JSONObject kickMessage = new JSONObject();
                        ParseObject object = objects.get(i);
                        long id = object.getLong("user_id_kicking");
                        // add to kickedList
                        try {
                            kickMessage.put("user_id", id);
                            kickMessage.put("user_name", FetchFriendUtil.getFriendName(id));
                            kickMessage.put("state", object.getLong("state"));
                          kickMessage.put("LimitType", object.getInt("LimitType"));
                          kickMessage.put("period", object.getInt("period"));
                          kickMessage.put("time1", object.getLong("time1"));
                          kickMessage.put("content1", object.getString("content1"));
                            kickMessage.put("time2", object.getLong("time2"));
                            kickMessage.put("content2", object.getString("content2"));
                            kickMessage.put("time3", object.getLong("time3"));
                            kickMessage.put("content3", object.getString("content3"));
                            kickMessage.put("is_me", true);
                            kickMessage.put("objectId", object.getObjectId());
                            respondList.add(kickMessage);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
