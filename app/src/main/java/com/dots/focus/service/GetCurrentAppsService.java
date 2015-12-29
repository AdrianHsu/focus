package com.dots.focus.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetCurrentAppsService extends Service {
    private final IBinder mBinder = new GetCurrentAppsBinder();
    private static String TAG = "getCurrentAppsService";
    public static ArrayList<JSONObject> friendCurrentAppList = new ArrayList<>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "GetCurrentAppsService start...");
        checkCurrentApps();

        return 0;
    }

    public class GetCurrentAppsBinder extends Binder {
        GetCurrentAppsService getService() {
            return GetCurrentAppsService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void checkCurrentApps() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        JSONArray friends = currentUser.getJSONArray("Friends");
        if (friends == null) return;

        if (friendCurrentAppList.size() != friends.length()) {
            for (int i = 0, length = friends.length(); i < length; ++i) {
                try {
                    JSONObject tempFriend = new JSONObject();
                    tempFriend.put("id", currentUser.getLong("id"));
                    tempFriend.put("name", currentUser.getString("name"));

                    friendCurrentAppList.add(tempFriend);
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }

        ParseQuery query = ParseQuery.getQuery("CurrentApp");
    }
}
