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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetCurrentAppsService extends Service {
    private final IBinder mBinder = new GetCurrentAppsBinder();
    private static String TAG = "GetCurrentAppsService";
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

    public static void checkCurrentApps() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        final JSONArray friends = currentUser.getJSONArray("Friends");
        if (friends == null) return;

        if (friendCurrentAppList.size() != friends.length()) {

            friendCurrentAppList.clear();
            for (int i = 0, length = friends.length(); i < length; ++i) {
                try {
                    JSONObject tempFriend = new JSONObject();
                    tempFriend.put("id", friends.getJSONObject(i).getLong("id"));
                    tempFriend.put("name", friends.getJSONObject(i).getString("name"));
                    tempFriend.put("numKick", friends.getJSONObject(i).getInt("numKick"));
                    tempFriend.put("state", -1);

                    friendCurrentAppList.add(tempFriend);
                    Log.d(TAG, "User Name: " + friendCurrentAppList.get(i).getString
                      ("name"));
                    Log.d(TAG, "User Id: " + friendCurrentAppList.get(i).getString
                      ("id"));
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }

        final List<Long> ids = new ArrayList<>();
        for (int i = 0, size = friendCurrentAppList.size(); i < size; ++i) {
            try {
                ids.add(friendCurrentAppList.get(i).getLong("id"));
            } catch (JSONException e) { Log.d (TAG, e.getMessage()); }
        }
        final List<String> names = new ArrayList<>();
        for (int i = 0, size = friendCurrentAppList.size(); i < size; ++i) {
            try {
                names.add(friendCurrentAppList.get(i).getString("name"));
            } catch (JSONException e) { Log.d (TAG, e.getMessage()); }
        }


        ParseQuery<ParseObject> query = ParseQuery.getQuery("CurrentApp");
        //query.whereContainedIn("id", ids);
        query.whereContainedIn("name", names);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null) {
                    Log.d(TAG, "objects.size: " + objects.size());
                    for (int i = 0, size = objects.size(); i < size; ++i) {
                        Log.d(TAG, "AppName: " + objects.get(i).getString("AppName"));
                        try {
                            //int index = ids.indexOf(objects.get(i).getLong("id"));
                            int index = names.indexOf(objects.get(i).getString("name"));
                            if (index >= 0) {
                              friendCurrentAppList.get(index).put("AppName", objects.get(i).getString
                                ("AppName"));
                              friendCurrentAppList.get(index).put("AppPackageName", objects.get(i)
                                .getString("AppPackageName"));
                              friendCurrentAppList.get(index).put("time", objects.get(i)
                                .getLong("time"));
                              Log.d(TAG, "App Name: " + friendCurrentAppList.get(index).getString
                                ("AppName"));
                              Log.d(TAG, "User Name: " + friendCurrentAppList.get(index).getString
                                ("name"));
                            }
                        } catch (JSONException e1) { Log.d(TAG, e1.getMessage()); }
                    }
                    // refresh the apps and time(s)
                }
                else if (e != null)
                    Log.d(TAG, e.getMessage());
                else if (objects == null) {
                  Log.d(TAG, "objects == null");
                }
            }
        });

    }
}
