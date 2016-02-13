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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by AdrianHsu on 2015/12/13.
 */



public class DailyReportChartActivity extends OverviewChartActivity {

  MaterialCalendarView materialCalendarView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_daily_report_chart);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.title_my_daily_report_overview));

    AlertDialog.Builder alert = new AlertDialog.Builder(
                            this);

    alert.setTitle("許秉鈞's 每日報表");

    WebView wv = new WebView(this);
    wv.getSettings().setJavaScriptEnabled(true);
    wv.loadUrl("http://stay-focused.herokuapp.com");
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
    final Dialog d = alert.setView(wv).create();
    d.show();
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    lp.copyFrom(d.getWindow().getAttributes());
    lp.width = WindowManager.LayoutParams.FILL_PARENT;
    lp.height = WindowManager.LayoutParams.FILL_PARENT;
    d.getWindow().setAttributes(lp);

    materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

//    materialCalendarView.setArrowColor(getResources().getColor(R.color.sample_primary));
//    materialCalendarView.setLeftArrowMask(getResources().getDrawable(R.drawable.ic_navigation_arrow_back));
//    materialCalendarView.setRightArrowMask(getResources().getDrawable(R.drawable.ic_navigation_arrow_forward));
//    materialCalendarView.setSelectionColor(getResources().getColor(R.color.sample_primary));

    materialCalendarView.setCurrentDate(CalendarDay.today());
    materialCalendarView.setShowOtherDates(MaterialCalendarView.SHOW_OTHER_MONTHS);
    materialCalendarView.setHeaderTextAppearance(R.style.TextAppearance_AppCompat_Large);
    materialCalendarView.setWeekDayTextAppearance(R.style.TextAppearance_AppCompat_Large);
    materialCalendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array
                            .custom_months)));
    materialCalendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array
                            .custom_weekdays)));
    materialCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);
    materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
      @Override
      public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
        d.show();
      }
    });

  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }
}