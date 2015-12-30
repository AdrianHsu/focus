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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetKickResponseService extends Service {
    private final IBinder mBinder = new GetKickedBinder();
    private static String TAG = "GetKickResponseService";
    public static ArrayList<JSONObject> kickResponseList = new ArrayList<>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "GetKickResponseService start...");
        queryKickResponse();

        return 0;
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

    private void queryKickResponse() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("KickHistory");
        query.whereEqualTo("user_id_kicking", currentUser.getLong("id"));
        // query.whereEqualTo("state", 2);
        query.whereGreaterThan("state", 1); // 2 or 3
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null) {
                    if (objects.isEmpty())  return;

                    for (int i = 0, size = objects.size(); i < size; ++i) {
                        objects.get(i).put("state", 3);
                        // add to kickResponseList
                    }
                    try {
                        ParseObject.saveAll(objects);
                        ParseObject.pinAll(objects);
                    } catch (ParseException e1) { Log.d(TAG, e1.getMessage()); }
                    // refresh the content
                }
            }
        });
    }
}
