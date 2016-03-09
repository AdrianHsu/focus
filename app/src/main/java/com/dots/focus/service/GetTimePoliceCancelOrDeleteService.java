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

public class GetTimePoliceCancelOrDeleteService extends Service {
    private final IBinder mBinder = new GetTimePoliceCancelOrDeleteBinder();
    private Timer timer = null;
    private static String TAG = "GetTimePoliceCancelOrDeleteService";
    public static ArrayList<JSONObject> timePoliceCancelList = new ArrayList<>();
    public static ArrayList<JSONObject> timePoliceDeleteList = new ArrayList<>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "GetTimePoliceCancelOrDeleteService start...");
        if (timer != null) timer.cancel();
        timer = new Timer();
        timer.schedule(new CheckTimeCancelOrDelete(), 0, 60000);

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

    class CheckTimeCancelOrDelete extends TimerTask {
        public void run() {
            refresh();
        }
    }

    public class GetTimePoliceCancelOrDeleteBinder extends Binder {
        GetTimePoliceCancelOrDeleteService getService() {
            return GetTimePoliceCancelOrDeleteService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static void refresh() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)    return;
        Long myId = currentUser.getLong("user_id");
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("TimePoliceCancellation");
        query1.whereEqualTo("user_id_cancelled", myId);
        query1.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> cancelList, ParseException e) {
                if (e == null && cancelList != null && !cancelList.isEmpty()) {
                    for (int i = 0, size = cancelList.size(); i < size; ++i) {
                        ParseObject object = cancelList.get(i);
                        JSONObject cancellation = new JSONObject();
                        Long id = object.getLong("user_id_cancelling");
                        String name = object.getString("user_name_cancelling");

                        TimePoliceUtil.getTimePoliceCancel(id);
                        try {
                            cancellation.put("id", id);
                            cancellation.put("name", name);
                            cancellation.put("time", object.getLong("time"));
                            cancellation.put("state", TimePoliceState.CANCEL.getValue() +
                                    TimePoliceUtil.timePoliceStateOffset);
                            cancellation.put("objectId", object.getObjectId());
                            timePoliceCancelList.add(cancellation);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                    ParseObject.deleteAllInBackground(cancelList);
                }
            }
        });
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("TimePoliceDeletion");
        query2.whereEqualTo("user_id_deleted", myId);
        query2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> deletelist, ParseException e) {
                if (e == null && deletelist != null && !deletelist.isEmpty()) {
                    for (int i = 0, size = deletelist.size(); i < size; ++i) {
                        ParseObject object = deletelist.get(i);
                        JSONObject deletion = new JSONObject();
                        Long id = object.getLong("user_id_deleting");
                        String name = object.getString("user_name_deleting");

                        TimePoliceUtil.getTimePoliceDelete(id);
                        try {
                            deletion.put("id", id);
                            deletion.put("name", name);
                            deletion.put("time", object.getLong("time"));
                            deletion.put("state", TimePoliceState.DELETE.getValue() +
                                    TimePoliceUtil.timePoliceStateOffset);
                            deletion.put("objectId", object.getObjectId());
                            timePoliceDeleteList.add(deletion);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                    ParseObject.deleteAllInBackground(deletelist);
                }
            }
        });
    }

    public static void removeCancelList(Long id, final String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TimePoliceCancellation");
        query.fromLocalDatastore();
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null)
                    object.unpinInBackground();

                else if (e != null)
                    Log.d(TAG, "Cannot find TimePoliceCancellation whose objectId is : " +
                            objectId);
            }
        });

        for (int i = 0, length = timePoliceCancelList.size(); i < length; ++i) {
            try {
                if (timePoliceCancelList.get(i).getLong("id") == id) {
                    timePoliceCancelList.remove(i);
                    return;
                }
            } catch (JSONException e) { Log.d(TAG, e.getMessage()); }
        }
        Log.d(TAG, "removeCancelList: cannot find id: " + id);
    }
    public static void removeDeleteList(Long id, final String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TimePoliceDeletion");
        query.fromLocalDatastore();
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null)
                    object.unpinInBackground();

                else if (e != null)
                    Log.d(TAG, "Cannot find TimePoliceDeletion whose objectId is : " +
                            objectId);
            }
        });

        for (int i = 0, length = timePoliceDeleteList.size(); i < length; ++i) {
            try {
                if (timePoliceDeleteList.get(i).getLong("id") == id) {
                    timePoliceDeleteList.remove(i);
                    return;
                }
            } catch (JSONException e) { Log.d(TAG, e.getMessage()); }
        }
        Log.d(TAG, "removeDeleteList: cannot find id: " + id);
    }
}
