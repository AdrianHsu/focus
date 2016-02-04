package com.dots.focus.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.focus.R;
import com.dots.focus.util.SettingsUtil;

public class GoalSettingsActivity extends BaseActivity {

  private Button doneBtn;
  private SeekBar seekBar;
  private TextView textView;

  private int progress;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_goal_settings);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("目標設定");
    seekBar = (SeekBar) findViewById(R.id.seekBar1);
    progress = SettingsUtil.getInt("goal");
    seekBar.setProgress(progress);
    textView = (TextView) findViewById(R.id.textView1);
    doneBtn =(Button) findViewById(R.id.button);

    // Initialize the textview with '0'.
    textView.setText(seekBar.getProgress() + "/" + seekBar.getMax() + " (以分鐘計)");

    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

      @Override
      public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
        progress = progresValue;
        textView.setText("Covered: " + progress + "/" + seekBar.getMax());
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

    doneBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        SettingsUtil.put("goal", progress);
        onBackPressed();
      }
    });
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }
}