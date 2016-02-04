package com.dots.focus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.dots.focus.R;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro2 {

  static final String TAG = "IntroActivity";
  // Please DO NOT override onCreate. Use init
  @Override
  public void init(Bundle savedInstanceState) {

    int image = R.drawable.ic_slide1;

    // Instead of fragments, you can also use our default slide
    // Just set a title, description, background and image. AppIntro will do the rest
//    addSlide(AppIntroFragment.newInstance(title, description, image, background_colour));
    addSlide(AppIntroFragment.newInstance("First", "Welcome to first page Introduction",
      image, ContextCompat.getColor(this, R.color.blue)));
    addSlide(AppIntroFragment.newInstance("Second", "Welcome to second page Introduction",
      image, ContextCompat.getColor(this, R.color.green)));
    addSlide(AppIntroFragment.newInstance("Third", "Welcome to third page Introduction",
      image, ContextCompat.getColor(this, R.color.yellow)));
    addSlide(AppIntroFragment.newInstance("Fourth", "Welcome to fourth page Introduction",
      image, ContextCompat.getColor(this, R.color.red)));


    // Hide Skip/Done button
//    showSkipButton(true);
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
//    showLoginActivity();
//  }

  @Override
  public void onDonePressed() {
    // Do something when users tap on Done button.
    showLoginActivity();
  }
  private void showLoginActivity() {
    Log.d(TAG, "IntroActivity Login...");
    Intent intent = new Intent(this, LoginActivity.class);
    startActivity(intent);
  }
}