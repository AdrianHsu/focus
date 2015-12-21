package com.dots.focus.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;


import com.dots.focus.model.AppInfo;
import com.dots.focus.util.FetchAppUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

public class gGetAppsService extends IntentService {
    //private List<AppInfo> applicationList = new ArrayList<AppInfo>();

    public GetAppsService(){
        super("GetAppsService");
    }

    @Override
    public void onHandleIntent(Intent intent){
        String TAG = "GetAppsService";

        JSONObject obj = new JSONObject(), apps = new JSONObject();
        JSONArray list = new JSONArray();
        boolean needToStore = false;

        List<ResolveInfo> ril = getAllAppsInDevice();
        for (ResolveInfo ri : ril) {
            if (!FetchAppUtil.findApp(ri.activityInfo.packageName)) {
                needToStore = true;
                try {
                    getAppNameAndStoreIt(ri, list);
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }

        HttpClient httpClient = new DefaultHttpClient(); //Deprecated
        try {
            obj.put("packages", list);

            HttpPost httpPost = new HttpPost("http://getdatafor.appspot.com/data");
            httpPost.setHeader("Content-type", "application/json");
            StringEntity params = new StringEntity(obj.toString());
            httpPost.setEntity(params);
            HttpResponse lResp = httpClient.execute(httpPost);

            ByteArrayOutputStream lBOS = new ByteArrayOutputStream();
            lResp.getEntity().writeTo(lBOS);
            String lInfoStr = lBOS.toString("UTF-8");
            JSONObject categoryResponse = new JSONObject(lInfoStr);
            JSONArray appArr = categoryResponse.getJSONArray("apps");

            for (int i = 0; i < appArr.length(); i++) {
                JSONObject appObj = appArr.getJSONObject(i);
                String packageVal = appObj.optString("package", null);
                String categoryVal = appObj.optString("category", null);

                if (packageVal == null || categoryVal == null)
                    continue;
                apps.put(packageVal, categoryVal);
            }
            Log.d(TAG, apps.toString());
            JSONArray all = apps.names();
            for (int i = 0; i < all.length(); i++) {
                Log.d("GetAppsService", all.getString(i) + ": " + apps.getString(all.getString(i)));
                AppInfo temp = FetchAppUtil.getApp(all.getString(i));
                if(temp != null)    temp.setCategory(apps.getString(all.getString(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        FetchAppUtil.printApps();
        if(needToStore) FetchAppUtil.loadParseApps();
    }

    public List<ResolveInfo> getAllAppsInDevice() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return getPackageManager().queryIntentActivities(mainIntent, 0);
    }

    public void getAppNameAndStoreIt(ResolveInfo ri, JSONArray list) throws Exception{
        String name;
        if (ri.activityInfo == null)    return;

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
        list.put(ri.activityInfo.packageName);
        FetchAppUtil.addApp(new AppInfo(name, ri.activityInfo.packageName, ri.loadIcon(getPackageManager())));

    }
}
