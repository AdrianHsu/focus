package com.dots.focus.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Harvey Yang on 2015/9/23.
 */
public class GetAppsService extends IntentService {
    //private List<AppInfo> applicationList = new ArrayList<AppInfo>();

    public GetAppsService(){
        super("GetAppsService");
    }

    @Override
    public void onHandleIntent(Intent intent){
        String TAG = "GetAppsService";

        Log.d(TAG, "Service onstart now.");

        JSONObject obj = new JSONObject(), apps = new JSONObject();

        JSONArray list = new JSONArray();

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> ril = getPackageManager().queryIntentActivities(mainIntent, 0);
        for (ResolveInfo ri : ril) {
            //applicationList.add(new AppInfo(LoginActivity.this, ri));
            String name = ri.resolvePackageName;

            try {
                if (ri.activityInfo != null) {
                    Resources res = getPackageManager().getResourcesForApplication(ri.activityInfo.applicationInfo);

                    AssetManager assets = res.getAssets();
                    DisplayMetrics metrics = res.getDisplayMetrics();
                    Configuration config = new Configuration(res.getConfiguration());
                    config.locale = Locale.US;

                    Resources engRes = new Resources(assets, metrics, config);

                    if (ri.activityInfo.labelRes != 0) {
                        name = engRes.getString(ri.activityInfo.labelRes);

                        if (name.equals("")) {
                            name = res.getString(ri.activityInfo.labelRes);
                        }

                    } else {
                        name = ri.activityInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            }
            Log.d(TAG, "Name: " + name);
            Log.d(TAG, "PackageName: " + ri.activityInfo.packageName);
            list.put(ri.activityInfo.packageName);
        }


        try {
            for (int i = 0; i < list.length(); i++) {
                Log.d(TAG, "The " + i + "st: " + list.getString(i));
            }
            obj.put("packages", list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Stage 1.");
        HttpClient httpClient = new DefaultHttpClient(); //Deprecated
        try {

            Log.d(TAG, "Stage 1-1.");
            HttpPost httpPost = new HttpPost("http://getdatafor.appspot.com/data");
            httpPost.setHeader("Content-type", "application/json");
            StringEntity params = new StringEntity(obj.toString());
            httpPost.setEntity(params);
            Log.d(TAG, "Stage 1-2.");
            HttpResponse lResp = httpClient.execute(httpPost);

            ByteArrayOutputStream lBOS = new ByteArrayOutputStream();
            String lInfoStr = null;
            JSONObject categoryResponse = null;
            Log.d(TAG, "Stage 1-3.");
            lResp.getEntity().writeTo(lBOS);
            lInfoStr = lBOS.toString("UTF-8");
            categoryResponse = new JSONObject(lInfoStr);
            JSONArray appArr = categoryResponse.getJSONArray("apps");

            Log.d(TAG, "Stage 2.");
            for (int i = 0; i < appArr.length(); i++) {
                JSONObject appObj = appArr.getJSONObject(i);
                String packageVal = appObj.optString("package", null);
                String categoryVal = appObj.optString("category", null);

                if (packageVal == null || categoryVal == null)
                    continue;
                apps.put(packageVal, categoryVal);
                /*
                for(int n = 0; n < appNames.size(); n++) {
                    if (packageVal.equalsIgnoreCase(appNames.get(i))) {

                    }
                }
                */
            }
            Log.d(TAG, "Stage 3.");
            Log.d(TAG, apps.toString());
            JSONArray all = apps.names();
            for (int i = 0; i < all.length(); i++) {
                Log.d("GetAppsService", all.getString(i) + ": " + apps.getString(all.getString(i)));
            }
            Log.d(TAG, "Stage 4.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }


}
