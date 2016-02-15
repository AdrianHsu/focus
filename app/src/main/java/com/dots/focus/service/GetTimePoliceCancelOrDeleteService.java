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

public class GetTimePoliceCancelOrDeleteService extends Service {
    private final IBinder mBinder = new GetTimePoliceCancelOrDeleteBinder();
    private static String TAG = "GetTimePoliceCancelOrDeleteService";
    public static ArrayList<JSONObject> timePoliceCancelList = new ArrayList<>();
    public static ArrayList<JSONObject> timePoliceDeleteList = new ArrayList<>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "GetTimePoliceCancelOrDeleteService start...");
        refresh();

        return 0;
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
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TimePoliceCancellation");
        query.whereEqualTo("user_id_cancelled", ParseUser.getCurrentUser().getLong("user_id"));

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> cancelList, ParseException e) {
                if (e == null && cancelList != null && !cancelList.isEmpty()) {
                    for (int i = 0, size = cancelList.size(); i < size; ++i) {
                        ParseObject object = cancelList.get(i);
                        JSONObject cancellation = new JSONObject();
                        boolean reply = object.getBoolean("reply");
                        Long id = object.getLong("user_id_cancelling");
                        String name = object.getString("user_name_cancelling");

                        TimePoliceUtil.invitingIdList.remove(id);

                        object.put("state", TimePoliceState.REPLY_DOWNLOADED.getValue());
                        try {
                            cancellation.put("id", id);
                            cancellation.put("name", name);
                            cancellation.put("time", object.getLong("time"));
                            timePoliceCancelList.add(cancellation);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                    ParseObject.deleteAllInBackground(cancelList);
                }
            }
        });
    }
}
