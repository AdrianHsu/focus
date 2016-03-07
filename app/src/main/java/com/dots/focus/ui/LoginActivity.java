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
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
                    ParseUser tempUser = null;
                    test(tempUser);
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
    private void test(ParseUser user) {
        Log.d(TAG, user.toString());
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
        searchAppUsages();

        afterLoginInitialize();

        startServices();

        showMainActivity();
    }
    private void searchAppUsages() {
        final long now = System.currentTimeMillis();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("AppUsage");
        query.whereGreaterThan("endTime", now - 1800000); // last 30 min
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> appUsages, ParseException e) {
                if (e == null && appUsages != null) {
                    for (int i = 0, length = appUsages.size(); i < length; ++i) {
                        ParseObject appUsage = appUsages.get(i);
                        loadUsageRecord(now, appUsage.getLong("startTime"),
                                appUsage.getLong("endTime"));
                    }
                } else if (e != null) {
                    Log.d(TAG, e.getMessage());
                }
            }
        });
    }
    private void loadUsageRecord(long now, long startTime, long endTime) {
        if (startTime == 0 || endTime == 0 || endTime > now) return;
        if (startTime < now - 1800000) startTime = now - 1800000;

        while (true) {
            long temp = nextFiveMinute(now, startTime);
            if (endTime > temp) {
                saveInService(getIndex(now, startTime), (int)((temp - startTime) / 1000));
                startTime = temp;
            } else {
                saveInService(getIndex(now, startTime), (int)((endTime - startTime) / 1000));
                break;
            }
        }
    }
    private long nextFiveMinute(long now, long startTime) {
        int fiveMin = 300000;
        now -= fiveMin * ((now - startTime) / fiveMin);
        if (now == startTime) now += fiveMin;
        return now;
    }
    private int getIndex(long now, long startTime) {
        int fiveMin = 300000, indexBeta = (int)((now - startTime) / fiveMin);
        if ((now - startTime) % fiveMin == 0)
            indexBeta -= 1;
        return TrackAccessibilityService.appsUsage.length - indexBeta - 1;
    }
    private void saveInService(int index, int duration) {
        int[] array = TrackAccessibilityService.appsUsage;
        if (index < 0 || index >= array.length) return;
        array[index] += duration;
    }

//    private void resumeFocus() {
//        startServices();
//
//        showMainActivity();
//    }
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
//        startService(new Intent(this, TrackAccessibilityService.class));

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
