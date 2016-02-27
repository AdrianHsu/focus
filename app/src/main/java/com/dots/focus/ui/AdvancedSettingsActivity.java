package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.dots.focus.Manifest;
import com.dots.focus.R;
import com.dots.focus.service.TrackAccessibilityService;

public class AdvancedSettingsActivity extends BaseActivity {

  private Boolean checked;
  private Switch access;

  private static String TAG = "AdvancedSettingsActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_advanced_settings);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.title_advanced_setting));

    access = (Switch) findViewById(R.id.switch2);

//    Intent intent = new Intent();
//    intent.setAction("check permission");
//    sendBroadcast(intent);
//
//
//    int permissionCheck = ContextCompat.checkSelfPermission(TrackAccessibilityService.service,
//                            android.Manifest.permission
//                            .BIND_ACCESSIBILITY_SERVICE);
//
//    if(permissionCheck == PackageManager.PERMISSION_GRANTED)
//      checked = true;
//    else if(permissionCheck == PackageManager.PERMISSION_DENIED)
//      checked = false;
//    else
//      checked = false;
//
//    access.setChecked(checked);
//    Log.d("Permission", "C: permissionCheck: " + permissionCheck);
//    Log.d("Permission", "C: PackageManager.PERMISSION_GRANTED: " + PackageManager
//                            .PERMISSION_GRANTED);
//    Log.d("Permission", "C: PackageManager.PERMISSION_DENIED: " + PackageManager.PERMISSION_DENIED);

    access.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }

}