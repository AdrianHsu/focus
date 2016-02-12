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

public class LockSettingsActivity extends BaseActivity {

  private Button doneBtn;
  private Button cancelBtn;
  private Button lockBtn;
  private SeekBar seekBar;
  private TextView textView;
  private TextView typeTextView;

  private int progress = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_lock_settings);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.title_lock_setting));
    seekBar = (SeekBar) findViewById(R.id.seekBar1);
    final int temp = SettingsUtil.getInt("lock");
    progress = temp;
    seekBar.setProgress(temp);
    textView = (TextView) findViewById(R.id.textView1);
    typeTextView = (TextView) findViewById(R.id.type);
    doneBtn =(Button) findViewById(R.id.button);
    cancelBtn = (Button) findViewById(R.id.cancel_button);
    lockBtn = (Button) findViewById(R.id.lock_condition_button);

    // Initialize the textview with '0'.
    textView.setText(seekBar.getProgress() + "/" + seekBar.getMax() + " (以分鐘計)");

    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

      @Override
      public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
        progress = progresValue;
        textView.setText(seekBar.getProgress() + "/" + seekBar.getMax() + " (以分鐘計)");
        Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        textView.setText(seekBar.getProgress() + "/" + seekBar.getMax() + " (以分鐘計)");
        Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
      }
    });

    doneBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        SettingsUtil.put("lock", progress);
        onBackPressed();
      }
    });
    cancelBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onBackPressed();
      }
    });
    lockBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        createLockConditionDialog();
      }
    });
  }
  private void createLockConditionDialog() {
    new MaterialDialog.Builder(this)
                            .title("選擇您欲使用的鎖屏監護")
                            .items(R.array.lockConditionList)
                            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                              @Override
                              public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                if (typeTextView != null && text != null) {
                                  String temp = text.toString();
                                  typeTextView.setText(temp);
                                }
                                return true;
                              }
                            })
                            .positiveText("完成").show();
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }
}