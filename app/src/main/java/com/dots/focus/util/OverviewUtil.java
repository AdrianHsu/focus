package com.dots.focus.util;

import android.util.Log;

import com.dots.focus.model.AppInfo;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harvey Yang on 2015/9/30.
 */
public class OverviewUtil {
    private static List<AppInfo> apps;
    private static ParseObject ParseApps = null;

    public static List<AppInfo> getApps() {
        return new ArrayList<>(apps);
    }

    public static void setApps(List<AppInfo> _apps) {
        apps = new ArrayList<>(_apps);
        /*
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Apps");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    ParseObject Apps = new ParseObject("Apps");
                    Apps.put("apps", apps);
                    Apps.pinInBackground();
                } else {
                    object.put("apps", apps);
                }
            }
        });
        */
        getParseApps().put("apps", apps);
    }

    public static void addApp(AppInfo app){
        apps.add(app);
        getParseApps().put("apps", apps);
    }
    public static boolean findApp(String _packageName){
        for(int i = 0; i < apps.size(); i++)
            if(apps.get(i).getPackageName().equals(_packageName))   return true;
        return false;
    }
    public static ParseObject getParseApps() {
        if(ParseApps == null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Apps");
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (object == null) {
                        ParseObject _Apps = new ParseObject("Apps");
                        _Apps.pinInBackground();
                        ParseApps = _Apps;
                    } else {
                        ParseApps = object;
                    }
                }
            });
        }
        return ParseApps;
    }
}
