package com.dots.focus.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dots.focus.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;


import java.util.Arrays;
import java.util.List;


public class DashboardActivity extends ActionBarActivity {

  private Dialog progressDialog;
  static final String TAG = "DashboardActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dashboard);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_dashboard, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public void onLoginClick(View v) {
    progressDialog = ProgressDialog.show(DashboardActivity.this, "", "Logging in...", true);

    // "public_profile, email, user_status, user_friends, user_about_me, user_location"
    List<String> permissions = Arrays.asList("public_profile, user_friends");
    Log.d(TAG, Arrays.toString(permissions.toArray()));

    // for extended permissions, like "user_about_me", your app must be reviewed by Facebook team
    // (https://developers.facebook.com/docs/facebook-login/permissions/)
    ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
      @Override
      public void done(ParseUser user, ParseException err) {
        progressDialog.dismiss();
        if (user == null) {
          Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
        } else if (user.isNew()) {
          Log.d(TAG, "User signed up and logged in through Facebook!");
          showUserDetailsActivity();
        } else {
          Log.d(TAG, "User logged in through Facebook!");
          Log.d(TAG, AccessToken.getCurrentAccessToken().getPermissions().toString());
          showUserDetailsActivity();
        }
      }
    });
  }

  private void showUserDetailsActivity() {
    Intent intent = new Intent(this, UserDetailsActivity.class);
    startActivity(intent);
  }
}
