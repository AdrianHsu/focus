package com.dots.focus.application;

/**
 * Created by AdrianHsu on 15/8/14.
 */

import android.app.Application;

import com.dots.focus.config.Config;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class MainApplication extends Application {

  static final String TAG = "MainApplication";

  @Override
  public void onCreate() {
    super.onCreate();
    FacebookSdk.sdkInitialize(getApplicationContext());

//    Parse.enableLocalDatastore(this);
    Parse.initialize(this,
      Config.FOCUS_APPLICATION_ID,
      Config.FOCUS_CLIENT_ID
    );

    ParseFacebookUtils.initialize(this);
  }
}
