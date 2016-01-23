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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetKickedService extends Service {
    private final IBinder mBinder = new GetKickedBinder();
    private static String TAG = "GetKickedService";
    public static ArrayList<JSONObject> kickedList = new ArrayList<>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "GetKickedService start...");
        queryKicked();

        return 0;
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
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("KickHistory");
        query.whereEqualTo("user_id_kicked", currentUser.getLong("user_id"));
        // query.whereEqualTo("state", 0);
        query.whereLessThan("state", 2); // 0 or 1
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null) {
                    if (objects.isEmpty())  return;

                    kickedList.clear();
                    for (int i = 0, size = objects.size(); i < size; ++i) {
                        objects.get(i).put("state", 1);
                        JSONObject kickMessage = new JSONObject();
                        // add to kickedList
                        try {
                            kickMessage.put("id", objects.get(i).getLong("user_id_kicking"));
                            kickMessage.put("name", objects.get(i).getString("user_name_kicking"));
                            kickMessage.put("state", objects.get(i).getLong("state"));
                            kickMessage.put("content", objects.get(i).getString("content2"));
                            kickMessage.put("objectId", objects.get(i).getObjectId());
                            kickedList.add(kickMessage);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }


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
