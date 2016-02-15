package com.dots.focus.util;

import android.util.Log;

import com.dots.focus.model.AppInfo;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FetchAppUtil {
    private static List<AppInfo> apps = new ArrayList<>();
    private static ParseObject ParseApps = null;
    private static boolean searching = false;
    private static String TAG = "FetchAppUtil";

    static {
        loadParseApps();
    }

    public static int getSize(){
        return apps.size();
    }

    public static boolean setApps() {
        if (ParseApps == null) {
            if (!searching) {
                Log.d(TAG, "ParseApps is null, loadParseApps...");
                loadParseApps();
            }
            Log.d(TAG, "setApps: return false");
            return false;
        }
        List<String> name = new ArrayList<>(), packageName = new ArrayList<>(), category = new ArrayList<>();
        for(int i = 0, size = apps.size(); i < size; ++i) {
            name.add(apps.get(i).getName());
            packageName.add(apps.get(i).getPackageName());
            category.add(apps.get(i).getCategory());
        }
        ParseApps.put("name", name);
        ParseApps.put("packageName", packageName);
        ParseApps.put("category", category);

        ParseApps.saveEventually();
        Log.d(TAG, "setApps: return true");
        return true;
    }

    public static void setUser() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null && ParseApps != null)
            ParseApps.put("User", currentUser);
    }

    public static void addApp(AppInfo app) {
        apps.add(app);
        Log.d(TAG, "app: " + app.getPackageName() + ", apps.size(): " + apps.size());
    }

    public static boolean findApp(String _packageName) {
        for (int i = 0, size = apps.size(); i < size; i++)
            if (apps.get(i).getPackageName().equals(_packageName)) return true;
        return false;
    }

    public static int getAppIndex(String _packageName) {
        for (int i = 0, size = apps.size(); i < size; i++)
            if (apps.get(i).getPackageName().equals(_packageName)) return i;

        return -1;
    }

    public static AppInfo getApp(String _packageName) {
        for (int i = 0, size = apps.size(); i < size; i++)
            if (apps.get(i).getPackageName().equals(_packageName)) return apps.get(i);
        return null;
    }

    public static AppInfo getApp(int index) {
        if (index >= 0 && index < apps.size())
          return apps.get(index);
        return null;
    }

    public static void printApps() {
        int size = apps.size();
        Log.d(TAG, "printing, apps.size(): " + size);
        if (searching) Log.d(TAG, "still searching...");
        for (int i = 0; i < size; i++) {
            Log.d(TAG, "i: " + i);
            Log.d(TAG, apps.get(i).getName() + "," + apps.get(i).getPackageName() + "," + apps.get(i).getCategory());
        }
    }

    public static void loadParseApps() {
        /*
        Log.d(TAG, "checking currentUser...");
        try {
            boolean shouldWait = false;
            while (ParseUser.getCurrentUser() == null) {
                Log.d(TAG, "currentUser == null");
                if (shouldWait) Thread.sleep(5000);
                shouldWait = true;
                //if (CreateInfoUtil.loggingIn) // TBD
                while (CreateInfoUtil.loggingIn) {
                    Thread.sleep(500);
                    Log.d(TAG, "loggingIn");
                }
            }
        } catch(Exception e) {
            Log.d(TAG, e.getMessage());
        }
        */

        Log.d(TAG, "start loading");
        if (ParseApps != null) return;
        searching = true;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Apps");
        query.fromLocalDatastore();


        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null &&object != null) {
                    ParseApps = object;
                    ParseApps.put("User", ParseUser.getCurrentUser());
                    mergeApps();

                    searching = false;
                    printApps();
                }
                else {
                    if (e != null)
                        Log.d(TAG, e.getMessage());
                    loadExceptionOrNull();
                }
            }
        });
    }

    // helper functions
    private static void loadExceptionOrNull() {
        Log.d("GetAppsService", "database is empty.");
        ParseApps = new ParseObject("Apps");
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null)
            ParseApps.put("User", currentUser);
        List<String> name = new ArrayList<>(), packageName = new ArrayList<>(), category = new ArrayList<>();
        for(int i = 0; i < apps.size(); ++i) {
            name.add(apps.get(i).getName());
            packageName.add(apps.get(i).getPackageName());
            category.add(apps.get(i).getCategory());
        }
        ParseApps.put("name", name);
        ParseApps.put("packageName", packageName);
        ParseApps.put("category", category);

        ParseApps.pinInBackground();
        ParseApps.saveEventually();

        searching = false;
        printApps();
        Log.d("GetAppsService", "new database done.");
    }
    private static void mergeApps() {
        String localTAG = "mergeApps";
        List<String> name, packageName, category;

        name = ParseApps.getList("name");
        packageName = ParseApps.getList("packageName");
        category = ParseApps.getList("category");
        List<AppInfo> extras = new ArrayList<>();

        int i = 0;
        for (int size = apps.size(); i < size; ++i) {
            if (i < packageName.size()) {
                if(! packageName.get(i).equals(apps.get(i).getPackageName())) {
                    boolean flag = false;
                    int j = i + 1;
                    String p = apps.get(i).getPackageName();
                    for(; j < packageName.size(); ++j){
                        if(packageName.get(j).equals(p)){
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        for(int c = i; c < j; ++c)
                            apps.add(c, new AppInfo(name.get(c), packageName.get(c), category.get(c)));
                        i = j;
                    }
                    else {
                        extras.add(apps.remove(i));
                        --i;
                    }
                }
            }
        }
        Log.d(localTAG, "i: " + i + ", packageName.size(): " + packageName.size());
        for(int size = packageName.size(); i < size; ++i){
            Log.d(localTAG, "packageName: " + packageName.get(i));
            addApp(new AppInfo(name.get(i), packageName.get(i), category.get(i)));
        }
        i = 0;
        for (int size = extras.size(); i < size; ++i)
            addApp(extras.get(i));

    }
}
