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

    addSlide(CreateInfoSlide.newInstance(R.layout.set_info_access));
    addSlide(CreateInfoSlide.newInstance(R.layout.set_info_email));
    addSlide(CreateInfoSlide.newInstance(R.layout.set_info_gender));
    addSlide(CreateInfoSlide.newInstance(R.layout.set_info_birth));
    addSlide(CreateInfoSlide.newInstance(R.layout.set_info_occupation));
    addSlide(CreateInfoSlide.newInstance(R.layout.set_info_welcome));

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
    startService(new Intent(this, TrackAccessibilityService.class));
    Log.d(TAG, "finish starting TrackService.");
  }
}