package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dots.focus.R;
import com.dots.focus.util.SettingsUtil;

public class IdleSettingsActivity extends BaseActivity {

  private Button doneBtn;
  private Button pickAppBtn;
  private SeekBar seekBar;
  private TextView textView;

  private int progress;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_idle_settings);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("耍廢條件設定");
    seekBar = (SeekBar) findViewById(R.id.seekBar1);
    progress = SettingsUtil.getInt("idle");
    seekBar.setProgress(progress);
    // Adrian: 連續要改掉..
    textView = (TextView) findViewById(R.id.textView1);
    doneBtn = (Button) findViewById(R.id.button);
    pickAppBtn = (Button) findViewById(R.id.pick_app_button);

    pickAppBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        createPickAppDialog();
      }
    });

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
        SettingsUtil.put("idle", progress);
        onBackPressed();
      }
    });
  }
  private void createPickAppDialog() {
    new MaterialDialog.Builder(this)
                            .title("選擇您欲排除的應用軟體")
                            .items(R.array.testAppList)
                            .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                              @Override
                              public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                /**
                                 * If you use alwaysCallMultiChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected check box to actually be selected.
                                 * See the limited multi choice dialog example in the sample project for details.
                                 **/
                                return true;
                              }
                            })
                            .positiveText("完成")
                            .show();
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }
}