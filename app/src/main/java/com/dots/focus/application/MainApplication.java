package com.dots.focus.application;

import android.app.Application;

import com.dots.focus.config.Config;
import com.dots.focus.model.DayBlock;
import com.dots.focus.model.HourBlock;
import com.dots.focus.util.FetchAppUtil;
import com.dots.focus.util.SettingsUtil;
import com.dots.focus.util.TimePoliceUtil;
import com.facebook.FacebookSdk;
import com.flurry.android.FlurryAgent;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;


public class MainApplication extends Application {

  static final String TAG = "MainApplication";

  @Override
  public void onCreate() {
    super.onCreate();
    FacebookSdk.sdkInitialize(getApplicationContext());

    Parse.enableLocalDatastore(this);
    ParseObject.registerSubclass(DayBlock.class);
    ParseObject.registerSubclass(HourBlock.class);
    Parse.initialize(this,
      Config.FOCUS_APPLICATION_ID,
      Config.FOCUS_CLIENT_ID
    );

    FlurryAgent.init(this, Config.FLURRY_KEY);

    ParseFacebookUtils.initialize(this);
    initialize();
  }
  private void initialize() {
    SettingsUtil.searchSettings();
    FetchAppUtil.loadParseApps();

  }
}

