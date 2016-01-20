package com.dots.focus.ui;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.dots.focus.R;

/**
 * Created by AdrianHsu on 2015/12/13.
 */



public class DailyReportChartActivity extends OverviewChartActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_daily_report_chart);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("許秉鈞's 每日報表");

    AlertDialog.Builder alert = new AlertDialog.Builder(
                            this);

    alert.setTitle("許秉鈞's 每日報表");

    WebView wv = new WebView(this);
    wv.getSettings().setJavaScriptEnabled(true);
    wv.loadUrl("http://adrianhsu.github.io");
    wv.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view,
                                              String url) {
        view.loadUrl(url);

        return true;
      }
    });

    alert.setNegativeButton("關閉",
                            new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int id) {
                              }
                            });
    Dialog d = alert.setView(wv).create();
    d.show();
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    lp.copyFrom(d.getWindow().getAttributes());
    lp.width = WindowManager.LayoutParams.FILL_PARENT;
    lp.height = WindowManager.LayoutParams.FILL_PARENT;
    d.getWindow().setAttributes(lp);

  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }
}