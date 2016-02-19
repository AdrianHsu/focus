package com.dots.focus.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.focus.R;
import com.dots.focus.util.SettingsUtil;
import com.rey.material.widget.Slider;

public class GoalSettingsActivity extends BaseActivity {

  private Slider slider;
  private TextView textView;

  private int progress;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_goal_settings);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.title_goal_setting));
    slider = (Slider) findViewById(R.id.slider1);
    progress = SettingsUtil.getInt("goal");
    slider.setValue(progress, true);
    textView = (TextView) findViewById(R.id.textView1);

    // Initialize the textview with '0'.
    textView.setText(slider.getValue() + "/" + slider.getMaxValue() + " (以分鐘計)");

    slider.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
      @Override
      public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {

        progress = newValue;
        textView.setText(newValue + "/" + slider.getMaxValue() + " (以分鐘計)");
        Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();

      }
    });
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }
  @Override
  public void onBackPressed() {
    SettingsUtil.put("goal", progress);
    super.onBackPressed();
  }
}