package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dots.focus.R;
import com.dots.focus.util.SettingsUtil;
import com.rey.material.widget.Slider;

public class LockSettingsActivity extends BaseActivity {

  private Button lockBtn;
  private Slider slider;
  private TextView textView;
  private TextView typeTextView;
  private Boolean friendLock = true;
  private int progress = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_lock_settings);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.title_lock_setting));
    slider = (Slider) findViewById(R.id.slider1);
    final int temp = SettingsUtil.getInt("lock");
    progress = temp;
    slider.setValue(temp, true);
    textView = (TextView) findViewById(R.id.textView1);
    typeTextView = (TextView) findViewById(R.id.type);
    lockBtn = (Button) findViewById(R.id.lock_condition_button);

    friendLock = SettingsUtil.getBooleen("friendLock");
    if(friendLock)
      typeTextView.setText(getResources().getString(R.string.lock_friend_to_self));
    else
      typeTextView.setText(getResources().getString(R.string.lock_never));
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
                                if(which == 0)
                                  friendLock = true;
                                else if(which == 1)
                                  friendLock = false;

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
  @Override
  public void onBackPressed() {
    SettingsUtil.put("lock", progress);
    SettingsUtil.put("friendLock", friendLock);
    super.onBackPressed();
  }
}