package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.dots.focus.R;
import com.dots.focus.util.SettingsUtil;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Set;

public class NotificationSettingsActivity extends BaseActivity {

//  private TextView notiTimeTextView;
  private Switch sound;
  private Switch vibrate;
  private Switch screenOn;
  private Switch screenOff;

  private Boolean mSound = false;
  private Boolean mVibrate = false;
  private Boolean mScreenOn = false;
  private Boolean mScreenOff = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_notification_settings);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.title_notif_setting));

    sound = (Switch) findViewById(R.id.sound);
    vibrate = (Switch) findViewById(R.id.vibrate);
    screenOn = (Switch) findViewById(R.id.turn_on_screen);
    screenOff = (Switch) findViewById(R.id.turn_off_screen);

    mSound = SettingsUtil.getBooleen("sound");
    mVibrate = SettingsUtil.getBooleen("vibrate");
    mScreenOn =SettingsUtil.getBooleen("screenOn");
    mScreenOff = SettingsUtil.getBooleen("screenOff");

    sound.setChecked(mSound);
    vibrate.setChecked(mVibrate);
    screenOn.setChecked(mScreenOn);
    screenOff.setChecked(mScreenOff);

    sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mSound = b;
      }
    });
    vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mVibrate = b;
      }
    });
    screenOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mScreenOn = b;
      }
    });
    screenOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mScreenOff = b;
      }
    });

//    notiTimeTextView = (TextView) findViewById(R.id.noti_time_textview);
//    notiTimeTextView.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//
//        Calendar now = Calendar.getInstance();
//        TimePickerDialog tpd = TimePickerDialog.newInstance(
//                                NotificationSettingsActivity.this,
//                                now.get(Calendar.HOUR_OF_DAY),
//                                now.get(Calendar.MINUTE),
//                                true // mode 24 hours is true
//        );
//        tpd.setThemeDark(true);
//        tpd.vibrate(true);
//        tpd.dismissOnPause(true);
//        tpd.enableSeconds(false);
//        tpd.setAccentColor(getResources().getColor(R.color.red));
//        tpd.setTitle("重設通知時間");
//        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
//          @Override
//          public void onCancel(DialogInterface dialogInterface) {
//          }
//        });
//        tpd.show(getFragmentManager(), "TimePickerDialog");
//      }
//    });
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    onBackPressed();
    return true;
  }
  @Override
  public void onBackPressed() {
    SettingsUtil.put("sound", mSound);
    SettingsUtil.put("vibrate", mVibrate);
    SettingsUtil.put("screenOn", mScreenOn);
    SettingsUtil.put("screenOff", mScreenOff);
    super.onBackPressed();
  }

//  @Override
//  public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
//    String hourString = hourOfDay < 10 ? "0"+hourOfDay : "" + hourOfDay;
//    String minuteString = minute < 10 ? "0"+minute : "" + minute;
//    String secondString = second < 10 ? "0"+second : "" + second;
//    String time = hourString+"h"+minuteString+"m"+secondString+"s";
//    notiTimeTextView.setText(time);
//  }
}