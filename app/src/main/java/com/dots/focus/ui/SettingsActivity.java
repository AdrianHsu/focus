package com.dots.focus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.dots.focus.R;
import com.dots.focus.controller.DashboardController;
import com.dots.focus.controller.SettingsController;
import com.dots.focus.ui.fragment.DashboardDragFragment;
import com.parse.ParseUser;


/**
 * Created by AdrianHsu on 2015/10/9.
 */

public class SettingsActivity extends BaseActivity {

  static final String TAG = "SettingsActivity";
  SettingsController mSettingsController = new SettingsController();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    super.createToolbarFragment();
  }
}