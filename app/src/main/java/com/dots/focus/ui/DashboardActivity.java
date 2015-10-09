package com.dots.focus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dots.focus.R;
import com.dots.focus.application.MainApplication;
import com.dots.focus.controller.DashboardController;
import com.dots.focus.ui.fragment.DragTopLayoutFragment;
import com.dots.focus.ui.fragment.ToolbarFragment;
import com.parse.ParseUser;


/**
 * Created by AdrianHsu on 2015/10/9.
 */

public class DashboardActivity extends BaseActivity {

  static final String TAG = "DashboardActivity";
  DashboardController mDashboardController = new DashboardController();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dashboard);

    //Fetch Facebook user info if it is logged
    ParseUser currentUser = ParseUser.getCurrentUser();
    if ((currentUser != null) && currentUser.isAuthenticated()) {
      mDashboardController.makeMeRequest();
    }

    createToolbarFrag();
    createDragTopLayoutFrag();

  }

  @Override
  public void onBackPressed() {
    //handle the back press, close the drawer first and if the drawer is closed close the activity
    if (MainApplication.result != null && MainApplication.result.isDrawerOpen()) {
      MainApplication.result.closeDrawer();
    } else {
//      super.onBackPressed();
      Intent a = new Intent(Intent.ACTION_MAIN);
      a.addCategory(Intent.CATEGORY_HOME);
      a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(a);
    }
  }
  private void createToolbarFrag() {

    FragmentManager fm = getSupportFragmentManager();
    Fragment fragment = fm.findFragmentById(R.id.frameToolbar);

    if(fragment == null) {
      fragment = new ToolbarFragment();
      fm.beginTransaction()
        .add(R.id.frameToolbar, fragment)
        .commit();
    }
  }
  private void createDragTopLayoutFrag() {

    FragmentManager fm = getSupportFragmentManager();
    Fragment fragment = fm.findFragmentById(R.id.frameDragTopLayout);

    if(fragment == null) {
      fragment = new DragTopLayoutFragment();
      fm.beginTransaction()
        .add(R.id.frameDragTopLayout, fragment)
        .commit();
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
    Intent intent = new Intent(this, LoginActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }
}