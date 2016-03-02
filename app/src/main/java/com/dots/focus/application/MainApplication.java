package com.dots.focus.application;

import android.app.Application;

import com.dots.focus.R;
import com.dots.focus.config.Config;
import com.dots.focus.model.DayBlock;
import com.dots.focus.model.HourBlock;
import com.dots.focus.util.FetchAppUtil;
import com.dots.focus.util.SettingsUtil;
import com.dots.focus.util.TimePoliceUtil;
import com.facebook.FacebookSdk;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;


public class MainApplication extends Application {

  static final String TAG = "MainApplication";
  private Tracker tracker;

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

    getDefaultTracker();
  }
  private void initialize() {
    SettingsUtil.searchSettings();
    FetchAppUtil.loadParseApps();

  }


  synchronized public Tracker getDefaultTracker() {
    if (tracker == null) {
      GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
      // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
      tracker = analytics.newTracker(R.xml.tracker_config);
      tracker.enableExceptionReporting(true);
      tracker.enableAdvertisingIdCollection(true); // ??
      tracker.enableAutoActivityTracking(true); // ??
    }
    return tracker;
  }
}

