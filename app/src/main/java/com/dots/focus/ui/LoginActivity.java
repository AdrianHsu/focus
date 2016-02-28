package com.dots.focus.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.dots.focus.R;

import com.dots.focus.service.GetAppsService;
import com.dots.focus.service.GetFriendConfirmService;
import com.dots.focus.service.GetFriendInviteService;
import com.dots.focus.service.GetKickRequestService;
import com.dots.focus.service.GetKickResponseService;
import com.dots.focus.service.GetKickedService;
import com.dots.focus.service.GetTimePoliceCancelOrDeleteService;
import com.dots.focus.service.GetTimePoliceInviteService;
import com.dots.focus.service.GetTimePoliceReplyService;
import com.dots.focus.service.TrackAccessibilityService;
import com.dots.focus.util.DashboardUtil;
import com.dots.focus.util.FetchAppUtil;
import com.dots.focus.util.LoginUtil;
import com.dots.focus.util.TimePoliceUtil;
import com.parse.LogInCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.content.Intent;
import android.os.Bundle;

import java.util.Arrays;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    static final String TAG = "LoginActivity";
    private Dialog progressDialog;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "LoginActivity onCreate...");
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_login);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (LoginUtil.checkPreviousLogin(this)) {
            showIntroActivity();
        }
    }

    public void onLoginClick(View v) {
        progressDialog = ProgressDialog.show(context, "", "Logging in...", true);

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
                        DashboardUtil.makeMeRequest();
                    }
                    signUp();
//                    showMainActivity();
                } else {
                    Log.d("FBUser", "is not new...");
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    Log.d("FBUser", "user == currentUser: " + (user == currentUser));
                    if ((currentUser != null) && currentUser.isAuthenticated()) {
                        Log.d("FBUser", "makeMeRequest...");
                        DashboardUtil.makeMeRequest();
                    }
                    signIn();
//                    showMainActivity();
                }

            }
        });
    }

    private void showIntroActivity() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
    }
//
    private void signUp() {
        afterLoginInitialize();

        startServices();

        showCreateInfoActivity();
    }
//    private void showDashboardActivity() {
//        Intent intent = new Intent(this, DashboardActivity.class);
//        startActivity(intent);
//    }

    private void signIn() {
        // search data on cloud...?

        afterLoginInitialize();

        startServices();

        showMainActivity();
    }


    private void resumeFocus() {
        startServices();

        showMainActivity();
    }
    private void showCreateInfoActivity() {
        Intent intent = new Intent(this, CreateInfoActivity.class);
        startActivity(intent);
    }
    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void afterLoginInitialize() {
        FetchAppUtil.setUser();
        TimePoliceUtil.afterLoginInitialize();
    }

    private void startServices() {
        startService(new Intent(this, TrackAccessibilityService.class));

        startService(new Intent(this, GetAppsService.class));

        startService(new Intent(this, GetFriendInviteService.class));
        startService(new Intent(this, GetFriendConfirmService.class));

        startService(new Intent(this, GetKickRequestService.class));
        startService(new Intent(this, GetKickedService.class));
        startService(new Intent(this, GetKickResponseService.class));

        startService(new Intent(this, GetTimePoliceInviteService.class));
        startService(new Intent(this, GetTimePoliceReplyService.class));
        startService(new Intent(this, GetTimePoliceCancelOrDeleteService.class));
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
