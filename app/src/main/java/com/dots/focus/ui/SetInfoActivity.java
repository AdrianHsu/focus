package com.dots.focus.ui;

import android.content.Intent;
import android.os.Bundle;

import com.dots.focus.R;
import com.dots.focus.ui.fragment.SetInfoSlide;
import com.github.paolorotolo.appintro.AppIntro;

/**
 * Created by AdrianHsu on 15/9/27.
 */
public class SetInfoActivity extends AppIntro {

  static final String TAG = "SetInfoActivity";
  // Please DO NOT override onCreate. Use init
  @Override
  public void init(Bundle savedInstanceState) {

    super.getSupportActionBar().hide();
    addSlide(SetInfoSlide.newInstance(R.layout.set_info_access));
    addSlide(SetInfoSlide.newInstance(R.layout.set_info_email));
    addSlide(SetInfoSlide.newInstance(R.layout.set_info_gender));
    addSlide(SetInfoSlide.newInstance(R.layout.set_info_birth));
    addSlide(SetInfoSlide.newInstance(R.layout.set_info_occupation));
    addSlide(SetInfoSlide.newInstance(R.layout.set_info_welcome));

    // Hide Skip/Done button
    showSkipButton(false);
    showDoneButton(true);
    setDepthAnimation();

    // Turn vibration on and set intensity
    // NOTE: you will probably need to ask VIBRATE permission in Manifest
    setVibrate(true);
    setVibrateIntensity(50);
  }
  @Override
  public void onSkipPressed() {
    // Do something when users tap on Done button.
//    showDashboardActivity();
  }

  @Override
  public void onDonePressed() {
    // Do something when users tap on Done button.
    showDashboardActivity();
  }
  private void showDashboardActivity() {
    Intent intent = new Intent(this, DashboardActivity.class);
    startActivity(intent);
  }
}