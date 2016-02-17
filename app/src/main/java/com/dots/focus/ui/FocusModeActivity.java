package com.dots.focus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.dots.focus.R;
import com.dots.focus.service.FocusModeService;

/**
 * Created by AdrianHsu on 2016/2/16.
 */
public class FocusModeActivity extends BaseActivity {
  private BaseActivity nowActivity;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_focus_mode);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(getResources().getString(R.string.title_new_focus_mode));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    nowActivity = FocusModeActivity.this;

    findViewById(R.id.btn_lock_screen).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(nowActivity, FocusModeService.class);
        i.setAction(FocusModeService.LOCK_ACTION);
        startService(i);
				Toast.makeText(nowActivity, "開始專注模式！", Toast.LENGTH_SHORT).show();
        finish();
      }
    });
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }
}
