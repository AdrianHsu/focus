package com.dots.focus.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.IBinder;
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


/**
 * Created by Harvey Yang on 2015/9/23.
 */
public class GetAppsService extends Service {
    //private List<AppInfo> applicationList = new ArrayList<AppInfo>();


    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId){
        super.onStart(intent, startId);
        Log.d("GetAppsService", "Service onstart now.");
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        JSONObject obj = new JSONObject(), apps = new JSONObject();

        JSONArray list = new JSONArray();

        List<ResolveInfo> ril = getPackageManager().queryIntentActivities(mainIntent, 0);
        for (ResolveInfo ri : ril) {
            //applicationList.add(new AppInfo(LoginActivity.this, ri));
            list.put(ri.resolvePackageName);
        }

        try {
            obj.put("packages", list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpClient httpClient = new DefaultHttpClient(); //Deprecated
        try {
            HttpPost httpPost = new HttpPost("http://getdatafor.appspot.com/data");
            httpPost.setHeader("Content-type", "application/json");
            StringEntity params = new StringEntity(obj.toString());
            httpPost.setEntity(params);

            HttpResponse lResp = httpClient.execute(httpPost);

            ByteArrayOutputStream lBOS = new ByteArrayOutputStream();
            String lInfoStr = null;
            JSONObject categoryResponse = null;

            lResp.getEntity().writeTo(lBOS);
            lInfoStr = lBOS.toString("UTF-8");
            categoryResponse = new JSONObject(lInfoStr);

            JSONArray appArr = categoryResponse.getJSONArray("apps");
            for(int i=0; i<appArr.length(); i++){
                JSONObject appObj = appArr.getJSONObject(i);
                String packageVal = appObj.optString("package", null);
                String categoryVal = appObj.optString("category", null);

                if(packageVal == null || categoryVal == null)
                    continue;
                apps.put(packageVal, categoryVal);
                /*
                for(int n = 0; n < appNames.size(); n++) {
                    if (packageVal.equalsIgnoreCase(appNames.get(i))) {

                    }
                }
                */
            }
            JSONArray all = apps.names();
            for(int i = 0; i < all.length(); i++){
                Log.d("GetAppsService", all.getString(i) + ": " + apps.getString(all.getString(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }


}
