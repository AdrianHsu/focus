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
import android.widget.TextView;

import com.dots.focus.R;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class NotificationSettingsActivity extends BaseActivity implements
                        TimePickerDialog.OnTimeSetListener {

  private TextView notiTimeTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_notification_settings);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("通知設定");

    notiTimeTextView = (TextView) findViewById(R.id.noti_time_textview);
    notiTimeTextView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                                NotificationSettingsActivity.this,
                                now.get(Calendar.HOUR_OF_DAY),
                                now.get(Calendar.MINUTE),
                                true // mode 24 hours is true
        );
        tpd.setThemeDark(true);
        tpd.vibrate(true);
        tpd.dismissOnPause(true);
        tpd.enableSeconds(true);
        if (true) {
          tpd.setAccentColor(Color.parseColor("#D96383"));
        }
        if (true) {
          tpd.setTitle("TimePicker Title");
        }
        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
          @Override
          public void onCancel(DialogInterface dialogInterface) {
          }
        });
        tpd.show(getFragmentManager(), "TimePickerDialog");

      }
    });
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }

  @Override
  public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
    String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
    String minuteString = minute < 10 ? "0"+minute : ""+minute;
    String secondString = second < 10 ? "0"+second : ""+second;
    String time = hourString+"h"+minuteString+"m"+secondString+"s";
    notiTimeTextView.setText(time);
  }
}