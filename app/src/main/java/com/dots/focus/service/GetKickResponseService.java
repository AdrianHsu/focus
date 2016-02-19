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

public class GetKickResponseService extends Service {
    private final IBinder mBinder = new GetKickedBinder();
    private static String TAG = "GetKickResponseService";
    public static ArrayList<JSONObject> kickResponseList = new ArrayList<>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "GetKickResponseService start...");

        Timer timer = new Timer();
        timer.schedule(new CheckKickResponse(), 0, 60000);

        return 0;
    }

    class CheckKickResponse extends TimerTask {
        public void run() {
            queryKickResponse();
        }
    }

    public class GetKickedBinder extends Binder {
        GetKickResponseService getService() {
            return GetKickResponseService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static void queryKickResponse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("KickHistory");
        query.whereEqualTo("user_id_kicking", ParseUser.getCurrentUser().getLong("user_id"));
        query.whereEqualTo("state", KickState.RESPONSE_NOT_DOWNLOADED.getValue());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null) {
                    Log.d(TAG, "GetKickResponse objects.size(): " + objects.size());
                    for (int i = 0, size = objects.size(); i < size; ++i) {
                        objects.get(i).put("state", KickState.RESPONSE_DOWNLOADED.getValue());
                        JSONObject kickResponse = new JSONObject();
                        ParseObject object = objects.get(i);
                        long id = object.getLong("user_id_kicked");
                        // add to kickResponseList
                        try {
                            kickResponse.put("user_id", id);
                            kickResponse.put("user_name", FetchFriendUtil.getFriendName(id));
                            kickResponse.put("state", object.getLong("state"));
                            kickResponse.put("LimitType", object.getInt("LimitType"));
                            kickResponse.put("period", object.getInt("period"));
                            kickResponse.put("time1", object.getLong("time1"));
                            kickResponse.put("content1", object.getString("content1"));
                            kickResponse.put("time2", object.getLong("time2"));
                            kickResponse.put("content2", object.getString("content2"));
                            kickResponse.put("time3", object.getLong("time3"));
                            kickResponse.put("content3", object.getString("content3"));
                            kickResponse.put("is_me", false);
                            kickResponse.put("objectId", object.getObjectId());
                            kickResponseList.add(kickResponse);
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
        ParseQuery<ParseObject> query = ParseQuery.getQuery("KickHistory");
        query.whereEqualTo("user_id_kicking", ParseUser.getCurrentUser().getLong("user_id"));
        query.whereEqualTo("state", KickState.RESPONSE_DOWNLOADED.getValue());
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null) {
                    kickResponseList.clear();

                    for (int i = 0, size = objects.size(); i < size; ++i) {
                        JSONObject kickResponse = new JSONObject();
                        ParseObject object = objects.get(i);
                        long id = object.getLong("user_id_kicked");
                        // add to kickResponseList
                        try {
                            kickResponse.put("user_id", id);
                            kickResponse.put("user_name", FetchFriendUtil.getFriendName(id));
                            kickResponse.put("state", object.getLong("state"));
                            kickResponse.put("LimitType", object.getInt("LimitType"));
                            kickResponse.put("period", object.getInt("period"));
                            kickResponse.put("time1", object.getLong("time1"));
                            kickResponse.put("content1", object.getString("content1"));
                            kickResponse.put("time2", object.getLong("time2"));
                            kickResponse.put("content2", object.getString("content2"));
                            kickResponse.put("time3", object.getLong("time3"));
                            kickResponse.put("content3", object.getString("content3"));
                          kickResponse.put("is_me", false);
                          kickResponse.put("objectId", object.getObjectId());
                            kickResponseList.add(kickResponse);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
