package com.dots.focus.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;

import com.dots.focus.R;
import com.dots.focus.controller.LoginController;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import android.content.Intent;
import android.os.Bundle;

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
      showDashboardActivity();
      Parse.enableLocalDatastore(this);
    }
  }
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
  }

  public void onLoginClick(View v) {
    progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging in...", true);

    List<String> permissions = Arrays.asList("public_profile", "email");
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
          showDashboardActivity();
        } else {
          Log.d(TAG, "User logged in through Facebook!");
          showDashboardActivity();
        }
      }
    });
  }

  private void showDashboardActivity() {
    Intent intent = new Intent(this, DashboardActivity.class);
    startActivity(intent);
  }

}
