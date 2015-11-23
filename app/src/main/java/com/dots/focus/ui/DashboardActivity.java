package com.dots.focus.ui;

import android.content.Intent;
import android.os.Bundle;

import com.dots.focus.R;
import com.dots.focus.controller.DashboardController;
import com.dots.focus.util.FetchAppUtil;
import com.parse.ParseUser;

public class DashboardActivity extends BaseActivity {

  static final String TAG = "DashboardActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dashboard);

    //Fetch Facebook user info if it is logged
    ParseUser currentUser = ParseUser.getCurrentUser();
    if ((currentUser != null) && currentUser.isAuthenticated()) {
      DashboardController.makeMeRequest();
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    ParseUser currentUser = ParseUser.getCurrentUser();
    if (currentUser != null) {
      // Check if the user is currently logged
      // and show any cached content
//      updateViewsWithProfileInfo();
    } else {
      // If the user is not logged in, go to the
      // activity showing the login view.
      startLoginActivity();
    }
  }

  private void startLoginActivity() {
    FetchAppUtil.loadParseApps();
    Intent intent = new Intent(this, LoginActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }
}