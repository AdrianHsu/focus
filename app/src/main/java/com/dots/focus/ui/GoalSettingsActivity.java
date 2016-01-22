package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.focus.R;

public class GoalSettingsActivity extends BaseActivity {

  private SeekBar seekBar;
  private TextView textView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_goal_settings);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("目標設定");
    seekBar = (SeekBar) findViewById(R.id.seekBar1);
    textView = (TextView) findViewById(R.id.textView1);

    // Initialize the textview with '0'.
    textView.setText(seekBar.getProgress() + "/" + seekBar.getMax() + " (以分鐘計)");

    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      int progress = 120;

      @Override
      public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
        progress = progresValue;
        Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        textView.setText("Covered: " + progress + "/" + seekBar.getMax());
        Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
      }
    });
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }
}