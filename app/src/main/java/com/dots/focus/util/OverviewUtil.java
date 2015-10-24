package com.dots.focus.util;


import android.util.Log;

import com.dots.focus.model.AppInfo;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;


import java.util.ArrayList;
import java.util.List;

public class OverviewUtil {
    private static List<AppInfo> apps = new ArrayList<>();
    private static ParseObject ParseApps = null;
    private static boolean searching = false;
    private static String TAG = "OverviewUtil";

    public static List<AppInfo> getApps() {
        return new ArrayList<>(apps);
    }

    public static boolean setApps() {
        if (ParseApps == null) {
            if (!searching) loadParseApps();
            return false;
        }
        List<String> name = new ArrayList<>(), packageName = new ArrayList<>(), category = new ArrayList<>();
        for(int i = 0; i < apps.size(); ++i) {
            name.add(apps.get(i).getName());
            packageName.add(apps.get(i).getPackageName());
            category.add(apps.get(i).getCategory());
        }
        ParseApps.put("name", name);
        ParseApps.put("packageName", packageName);
        ParseApps.put("category", category);

        ParseApps.saveEventually();
        return true;
    }

    public static void addApp(AppInfo app) {
        apps.add(app);
        Log.d(TAG, "apps.size(): " + apps.size());
    }

    public static boolean findApp(String _packageName) {
        for (int i = 0; i < apps.size(); i++)
            if (apps.get(i).getPackageName().equals(_packageName)) return true;
        return false;
    }

    public static AppInfo getApp(String _packageName) {
        for (int i = 0; i < apps.size(); i++)
            if (apps.get(i).getPackageName().equals(_packageName)) return apps.get(i);
        return null;
    }

    public static void loadParseApps() {
        Log.d("GetAppsService", "start loading");
        if (ParseApps == null) {
            searching = true;
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Apps");
            query.fromLocalDatastore();
            /*
            ParseObject temp = null;

            try {
                Log.d("GetAppsService", "getting first");
                temp = query.getFirst();
            } catch(ParseException e) {
                ParseApps = new ParseObject("Apps");
                ParseApps.pinInBackground();
                ParseApps.saveEventually();
            }
            if(temp == null) {
                Log.d("GetAppsService", "database is empty.");
                ParseApps = new ParseObject("Apps");
                ParseApps.pinInBackground();
                ParseApps.saveEventually();
                searching = false;

                printApps();
            } else {
                Log.d("GetAppsService", "loading");
                ParseApps = temp;

                List<AppInfo> tempApps = apps;
                apps = ParseApps.getList("apps");
                for (int i = 0; i < tempApps.size(); ++i)
                    if (!findApp(tempApps.get(i).getPackageName()))
                        addApp(tempApps.get(i));

                searching = false;
                printApps();
            }
            */

            query.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (object == null || e != null) {
                        Log.d("GetAppsService", "database is empty.");
                        ParseApps = new ParseObject("Apps");
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
                    } else {
                        Log.d("GetAppsService", "loading");
                        ParseApps = object;
                        List<String> name, packageName, category;

                        name = ParseApps.getList("name");
                        packageName = ParseApps.getList("packageName");
                        category = ParseApps.getList("category");

                        List<AppInfo> extras = apps;
                        int i = 0;
                        for (; i < apps.size(); ++i) {
                            if (i < packageName.size()){
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
                        for(; i < packageName.size(); ++i){
                            addApp(new AppInfo(name.get(i), packageName.get(i), category.get(i)));
                        }
                        for(i = 0; i < extras.size(); ++i)
                            addApp(extras.get(i));
                        searching = false;
                        printApps();
                    }
                }
            });

        }
    }

    public static void printApps() {
        Log.d(TAG, "printing, apps.size(): " + apps.size());
        if (searching) Log.d(TAG, "still searching...");
        for (int i = 0; i < apps.size(); i++) {
            Log.d(TAG, "i: " + i);
            Log.d(TAG, apps.get(i).getName() + "," + apps.get(i).getPackageName() + "," + apps.get(i).getCategory());
        }
    }
}
