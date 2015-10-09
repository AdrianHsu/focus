package com.dots.focus.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.dots.focus.R;
import com.dots.focus.controller.DashboardController;
import com.dots.focus.ui.fragment.DragTopLayoutFragment;
import com.dots.focus.ui.fragment.ToolbarFragment;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardActivity extends BaseActivity {

  static final String TAG = "DashboardActivity";
  DashboardController mDashboardController = new DashboardController();

//  private ProfilePictureView userProfilePictureView;
//  private TextView userNameView;
//  private TextView userGenderView;
//  private TextView userEmailView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_dashboard);

//    userProfilePictureView = (ProfilePictureView) findViewById(R.id.userProfilePicture);
//    userNameView = (TextView) findViewById(R.id.userName);
//    userGenderView = (TextView) findViewById(R.id.userGender);
//    userEmailView = (TextView) findViewById(R.id.userEmail);

    //Fetch Facebook user info if it is logged
    ParseUser currentUser = ParseUser.getCurrentUser();
    if ((currentUser != null) && currentUser.isAuthenticated()) {
      mDashboardController.makeMeRequest();
      // Show the user info
//      updateViewsWithProfileInfo();
    }

    createToolbarFrag();
    createDragTopLayoutFrag();

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
      updateViewsWithProfileInfo();
    } else {
      // If the user is not logged in, go to the
      // activity showing the login view.
      startLoginActivity();
    }
  }
  public void updateViewsWithProfileInfo() {
    ParseUser currentUser = ParseUser.getCurrentUser();
    if (currentUser.has("profile")) {
      JSONObject userProfile = currentUser.getJSONObject("profile");
      try {

        if (userProfile.has("facebookId")) {
//          userProfilePictureView.setProfileId(userProfile.getString("facebookId"));
        } else {
          // Show the default, blank user profile picture
//          userProfilePictureView.setProfileId(null);
        }

        if (userProfile.has("name")) {
//          userNameView.setText(userProfile.getString("name"));
        } else {
//          userNameView.setText("");
        }

//        if (userProfile.has("gender")) {
//          userGenderView.setText(userProfile.getString("gender"));
//        } else {
//          userGenderView.setText("");
//        }

        if (userProfile.has("email")) {
//          userEmailView.setText(userProfile.getString("email"));
        } else {
//          userEmailView.setText("");
        }
//        if (userProfile.has("friend")) {
//          Log.v(TAG, userProfile.getString("friend"));
//        } else {
//          Log.v(TAG, "friend not found");
//        }
        if (userProfile.has("locale")) {
          Log.v(TAG, userProfile.getString("locale"));
        } else {
          Log.v(TAG, "locale not found");
        }

      } catch (JSONException e) {
        Log.d(TAG, "Error parsing saved user data.");
      }
    }
  }

  public void onLogoutClick(View v) {
    logout();
  }

  private void logout() {
    // Log the user out
    ParseUser.logOut();

    // Go to the login view
    startLoginActivity();
  }

  private void startLoginActivity() {
    Intent intent = new Intent(this, LoginActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }
}