package com.dots.focus.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.dots.focus.R;
import com.dots.focus.controller.DashboardController;
import com.dots.focus.controller.LoginController;

import com.dots.focus.service.GetAppsService;
import com.dots.focus.util.CreateInfoUtil;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.content.Intent;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    static final String TAG = "LoginActivity";
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "LoginActivity onCreate...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (LoginController.hasLoggedIn()) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            Log.d(TAG, "Already Login...");
            if (currentUser.has("user_id"))
                Log.d(TAG, "" + currentUser.getLong("user_id"));
            if (currentUser.has("user_name"))
                Log.d(TAG, "" + currentUser.getString("user_name"));
            currentUser.saveEventually(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null)
                        Log.d(TAG, "save succeeded.");
                    else
                        Log.d(TAG, e.getMessage());
                }
            });
            showSetInfoActivity();
//          showMainActivity();
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
        if (LoginController.checkPreviousLogin(this)) {
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
                    Log.d("FBUser", err.toString());
                    Log.d("FBUser", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("FBUser", "is new...");
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    Log.d("FBUser", "user == currentUser: " + (user == currentUser));
                    if ((currentUser != null) && currentUser.isAuthenticated()) {
                        Log.d("FBUser", "makeMeRequest...");
                        DashboardController.makeMeRequest();
                    }
                    showSetInfoActivity();
//                    showMainActivity();
                } else {
                    Log.d("FBUser", "is not new...");
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    Log.d("FBUser", "user == currentUser: " + (user == currentUser));
                    if ((currentUser != null) && currentUser.isAuthenticated()) {
                        Log.d("FBUser", "makeMeRequest...");
                        DashboardController.makeMeRequest();
                    }
                    showSetInfoActivity();
//                    showMainActivity();
                }

            }
        });

        Intent intent = new Intent(this, GetAppsService.class);
        startService(intent);
    }

    private void showIntroActivity() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
    }
//
    private void showSetInfoActivity() {
        Intent intent = new Intent(this, CreateInfoActivity.class);
        startActivity(intent);
    }
//    private void showDashboardActivity() {
//        Intent intent = new Intent(this, DashboardActivity.class);
//        startActivity(intent);
//    }
    private void showMainActivity() {
      Intent intent = new Intent(this, MainActivity.class);
      startActivity(intent);
    }
//
//
//    private void setProfile() {
//        ParseUser user = ParseUser.getCurrentUser();
//        if (!user.has("profile"))   return;
//        if (user.has("id")) Log.d("FBUser", user.getString("id"));
//        if (user.has("name"))   Log.d("FBUser", user.getString("name"));
//
//        JSONObject profile = user.getJSONObject("profile");
//        try {
//            CreateInfoUtil.setUserInfo("id", profile.getString("facebookId"), false);
//            CreateInfoUtil.setUserInfo("name", profile.getString("name"), false);
//        } catch (JSONException e) { e.getMessage(); }
//        user.remove("profile");
//        Log.d("FBUser", user.toString());
//        Log.d("FBUser", user.getString("id"));
//        Log.d("FBUser", user.getString("name"));
//
//        user.saveEventually();
//
//    }
}
