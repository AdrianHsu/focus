package com.dots.focus.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.dots.focus.R;
import com.dots.focus.controller.LoginController;

import com.dots.focus.service.GetAppsService;
import com.parse.LogInCallback;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
        if (mLoginController.hasLoggedIn()) {
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
        if (mLoginController.checkPreviousLogin(this)) {
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
        Intent intent = new Intent(this, GetAppsService.class);
        startService(intent);
    }

    private void showIntroActivity() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
    }

    private void showSetInfoActivity() {
        Intent intent = new Intent(this, CreateInfoActivity.class);
        startActivity(intent);
    }

    private void showDashboardActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

}
