package com.dots.focus.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.dots.focus.R;
import com.dots.focus.config.Config;
import com.dots.focus.controller.LoginController;

import com.dots.focus.service.GetAppsService;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import android.content.Intent;
import android.os.Bundle;

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
import java.util.Arrays;
import java.util.List;

/**
 * Created by AdrianHsu on 15/9/23.
 */
public class LoginActivity extends BaseActivity {

  static final String TAG = "LoginActivity";
  private Dialog progressDialog;
  private LoginController mLoginController = new LoginController();

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    if( mLoginController.hasLoggedIn() ) {
      showSetInfoActivity();
//      Parse.enableLocalDatastore(this); //Exception not yet resolved
    }
  }
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected void onResume() {

    super.onResume();
    if ( mLoginController.checkPreviousLogin(this) ) {
      showIntroActivity();
    }
  }

  public void onLoginClick(View v) {
    progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging in...", true);

    List<String> permissions = Arrays.asList("public_profile", "user_friends");
    // NOTE: for extended permissions, like "user_about_me", your app must be reviewed by the Facebook team
    // (https://developers.facebook.com/docs/facebook-login/permissions/)

    ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
      @Override
      public void done(ParseUser user, ParseException err) {
        progressDialog.dismiss();
        if (user == null) {
          Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
        } else if (user.isNew()) {
          Log.d(TAG, "User signed up and logged in through Facebook!");
          showSetInfoActivity();
        } else {
          Log.d(TAG, "User logged in through Facebook!");
          showSetInfoActivity();
        }
      }
    });
    Log.d(TAG, "Service starts.");
    JSONObject obj = new JSONObject(), apps = new JSONObject();

    JSONArray list = new JSONArray();

    final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
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
      Log.d(TAG, "Stage 3.");
      JSONArray all = apps.names();
      for(int i = 0; i < all.length(); i++){
        Log.d("GetAppsService", all.getString(i) + ": " + apps.getString(all.getString(i)));
      }
      Log.d(TAG, "Stage 4.");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      httpClient.getConnectionManager().shutdown();
    }
  }
  private void showIntroActivity() {
    Intent intent = new Intent(this, IntroActivity.class);
    startActivity(intent);
  }

  private void showSetInfoActivity() {
    Intent intent = new Intent(this, SetInfoActivity.class);
    startActivity(intent);
  }

  private void showDashboardActivity() {


    Intent intent = new Intent(this, DashboardActivity.class);
    startActivity(intent);
  }

}
