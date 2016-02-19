package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dots.focus.R;
import com.dots.focus.ui.fragment.CreateInfoSlide;
import com.dots.focus.util.FetchAppUtil;
import com.dots.focus.util.SettingsUtil;
import com.rey.material.widget.Slider;

import org.w3c.dom.Text;

public class IdleSettingsActivity extends BaseActivity {

  private Button pickAppBtn;
  private Slider slider;
  private TextView textView;
  public static Integer[] defaultMultiChoice = null;
  private static Integer[] pickedMultiChoice = null;

  private TextView appPickedTv;
  private int progress;

  private static String TAG = "IdleSettingsActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_idle_settings);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.title_idle_setting));
    slider = (Slider) findViewById(R.id.slider1);
    appPickedTv = (TextView) findViewById(R.id.app_picked);
    progress = SettingsUtil.getInt("idle");
    slider.setValue(progress, true);
    // Adrian: 連續要改掉..
    textView = (TextView) findViewById(R.id.textView1);
    pickAppBtn = (Button) findViewById(R.id.pick_app_button);

    final int length = FetchAppUtil.getSize();
    final String [] appNameList = new String [length];
    for (int i = 0; i < length; ++i)
      appNameList[i] = FetchAppUtil.getApp(i).getName();
    pickedMultiChoice = defaultMultiChoice;
    appPickedTv.setText(CreateInfoSlide.getExcludedApps(appNameList, defaultMultiChoice, length));

    pickAppBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        createPickAppDialog();
      }
    });

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
  private void createPickAppDialog() {
    final int length = FetchAppUtil.getSize();
    final String [] appNameList = new String [length];
    for (int i = 0; i < length; ++i)
      appNameList[i] = FetchAppUtil.getApp(i).getName();

    pickedMultiChoice = defaultMultiChoice;

    appPickedTv.setText(CreateInfoSlide.getExcludedApps(appNameList, defaultMultiChoice, length));
    new MaterialDialog.Builder(this)
        .title("選擇您欲排除的應用軟體")
        .items(appNameList)
        .itemsCallbackMultiChoice(defaultMultiChoice, new MaterialDialog.ListCallbackMultiChoice() {
          @Override
          public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
            pickedMultiChoice = which;
            return true;
          }
        })
        .dismissListener(new DialogInterface.OnDismissListener() {
          @Override
          public void onDismiss(DialogInterface dialogInterface) {
            Log.v(TAG, "on dismiss");
            defaultMultiChoice = pickedMultiChoice;
            appPickedTv.setText(CreateInfoSlide.getExcludedApps(appNameList, defaultMultiChoice,
                    length));
          }
        })

        .cancelListener(new DialogInterface.OnCancelListener() {
          @Override
          public void onCancel(DialogInterface dialogInterface) {
            pickedMultiChoice = defaultMultiChoice;
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
  @Override
  public void onBackPressed() {
    SettingsUtil.put("idle", progress);
    super.onBackPressed();
  }
}