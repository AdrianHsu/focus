package com.dots.focus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.dots.focus.R;
import com.dots.focus.controller.DashboardController;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import github.chenupt.dragtoplayout.DragTopLayout;

public class DashboardActivity extends BaseActivity {

  static final String TAG = "DashboardActivity";
  DashboardController mDashboardController = new DashboardController();

//  private ProfilePictureView userProfilePictureView;
//  private TextView userNameView;
//  private TextView userGenderView;
//  private TextView userEmailView;

  private Toolbar toolbar;
  private DragTopLayout dragLayout;
  private ViewPager viewPager;
  private PagerSlidingTabStrip pagerSlidingTabStrip;

//  private ImageView topImageView;

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
    createView();

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

  private void createView() {

    toolbar = (Toolbar) findViewById(R.id.tool_bar);
    viewPager = (ViewPager) findViewById(R.id.view_pager);
//    topImageView = (ImageView) findViewById(R.id.image_view);
    pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);

//    setSupportActionBar(toolbar);
//    // init pager
//    PagerModelManager factory = new PagerModelManager();
//    factory.addCommonFragment(getFragments(), getTitles());
//    adapter = new ModelPagerAdapter(getSupportFragmentManager(), factory);
//    viewPager.setAdapter(adapter);
//    pagerSlidingTabStrip.setViewPager(viewPager);
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