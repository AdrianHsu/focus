package com.dots.focus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.dots.focus.R;
import com.dots.focus.service.TrackAccessibilityService;
import com.dots.focus.ui.fragment.CreateInfoSlide;
import com.dots.focus.util.CreateInfoUtil;
import com.dots.focus.util.FetchAppUtil;
import com.github.paolorotolo.appintro.AppIntro2;

public class CreateInfoActivity extends AppIntro2 {
  public final String TAG = "CreateInfoActivity";
  //static final String TAG = "CreateInfoActivity";
  // Please DO NOT override onCreate. Use init
  @Override
  public void init(Bundle savedInstanceState) {

    addSlide(CreateInfoSlide.newInstance(R.layout.set_info_access, this));
    addSlide(CreateInfoSlide.newInstance(R.layout.set_info_basic, this));
    addSlide(CreateInfoSlide.newInstance(R.layout.set_goal, this));
    addSlide(CreateInfoSlide.newInstance(R.layout.set_idle, this));
    addSlide(CreateInfoSlide.newInstance(R.layout.set_lock, this));
    addSlide(CreateInfoSlide.newInstance(R.layout.set_done, this));


    // Hide Skip/Done button
//    showSkipButton(false);
    showDoneButton(true);
    setDepthAnimation();

    // Turn vibration on and set intensity
    // NOTE: you will probably need to ask VIBRATE permission in Manifest
    setVibrate(true);
    setVibrateIntensity(50);
  }
//  @Override
//  public void onSkipPressed() {
//    // Do something when users tap on Done button.
////    showDashboardActivity();
//  }

  @Override
  public void onDonePressed() {
    // Do something when users tap on Done button.

    CreateInfoUtil.update();
    FetchAppUtil.printApps();
    FetchAppUtil.setApps();
    CreateInfoSlide.onDonePressed();
    //Intent intent = new Intent(this, GetAppsService.class);
    //startService(intent);
    startTrackService();
    showDashboardActivity();
  }
  private void showDashboardActivity() {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
  }
  private void startTrackService() {
    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
    startActivity(intent);
    Log.d(TAG, "start starting TrackService.");
    Intent intent2 = new Intent(this, TrackAccessibilityService.class);
    startService(intent2);


    Log.d(TAG, "finish starting TrackService.");
  }
}