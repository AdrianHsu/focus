package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.dots.focus.R;

import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;


import com.dots.focus.config.MyValueFormatter;
import com.dots.focus.config.MyValueYFormatter;
import com.dots.focus.service.TrackAccessibilityService;
import com.dots.focus.ui.fragment.GlobalPiggyBankFragment;
import com.dots.focus.util.SettingsUtil;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.LineDataProvider;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class WeeklyAppUsageChartActivity extends OverviewChartActivity implements OnSeekBarChangeListener {

  private LineChart mChart;
  private Spinner spinner;
  private ArrayAdapter<String> timeInterval;
  private String[] timeIntervalArray = {"分鐘", "小時"};
  private TextView weekSwitchTv;
  private TextView addictDayTv;
  private TextView weekTotalTv;
  private Button daySwitchLeftBtn;
  private Button daySwitchRightBtn;
  private boolean IS_MINUTE = true;
  private int CURRENT_WEEK = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_weekly_app_usage_chart);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.title_weekly_app_usage_overview));

//    DEBUG: W/System.err: java.lang.RuntimeException: Can't create handler inside thread that has
// not called Looper.prepare()
    spinner = (Spinner)findViewById(R.id.spinner);
    timeInterval = new ArrayAdapter<String>(this, android.R.layout
      .simple_spinner_dropdown_item, timeIntervalArray);
    spinner.setAdapter(timeInterval);

    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) { // second by default
          IS_MINUTE = true;
        } else if (i == 1) {
          IS_MINUTE = false;
        }
        // add data
        CURRENT_WEEK = 0;
        if (CURRENT_WEEK == 0)
          daySwitchRightBtn.setEnabled(false);
        ArrayList<Entry> val = setData(CURRENT_WEEK);
        drawChart(val);
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });
    addictDayTv = (TextView) findViewById(R.id.addict_day);
    weekTotalTv = (TextView) findViewById(R.id.week_total);

    refreshCard();

    weekSwitchTv = (TextView) findViewById(R.id.day_switch_textview);
    String week = TrackAccessibilityUtil.weekPeriodString(CURRENT_WEEK);
    weekSwitchTv.setText(week);

    daySwitchLeftBtn = (Button) findViewById(R.id.day_switch_left_btn);
    daySwitchLeftBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        CURRENT_WEEK++;

        refreshCard();

        weekSwitchTv = (TextView) findViewById(R.id.day_switch_textview);
        String week = TrackAccessibilityUtil.weekPeriodString(CURRENT_WEEK);
        weekSwitchTv.setText(week);
        ArrayList<Entry> val = setData(CURRENT_WEEK);
        drawChart(val);
        daySwitchRightBtn.setEnabled(true);
//        daySwitchLeftBtn.setEnabled(false);
      }
    });
    daySwitchRightBtn = (Button) findViewById(R.id.day_switch_right_btn);
    daySwitchRightBtn.setEnabled(false);
    daySwitchRightBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        CURRENT_WEEK--;

        refreshCard();

        weekSwitchTv = (TextView) findViewById(R.id.day_switch_textview);
        String week = TrackAccessibilityUtil.weekPeriodString(CURRENT_WEEK);
        weekSwitchTv.setText(week);
        ArrayList<Entry> val = setData(CURRENT_WEEK);
        drawChart(val);
        if (CURRENT_WEEK == 0)
          daySwitchRightBtn.setEnabled(false);
        daySwitchLeftBtn.setEnabled(true);
      }
    });

    mChart = (LineChart) findViewById(R.id.chart1);
    initChart();
    // add data
    ArrayList<Entry> val = setData(0);
    drawChart(val);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }
  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    // redraw
    mChart.invalidate();
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub

  }
  private void initChart() {

//    mChart.setViewPortOffsets(80, 40, 80, 40);
    mChart.setViewPortOffsets(35, 20, 20, 30);
    mChart.setBackgroundColor(Color.parseColor("#ff424242"));
    // no description text
    mChart.setDescription("");

    // enable touch gestures
    mChart.setTouchEnabled(true);

    // enable scaling and dragging
//    mChart.setDragEnabled(true);
//    mChart.setScaleEnabled(true);
    mChart.setDragEnabled(false);
    mChart.setScaleEnabled(false);

    // if disabled, scaling can be done on x- and y-axis separately
    mChart.setPinchZoom(false);
//    mChart.setPinchZoom(true);
    mChart.setDrawGridBackground(false);


    XAxis x = mChart.getXAxis();
//    x.setEnabled(false);
    x.setDrawGridLines(true);
    x.setGridLineWidth(3.0f);
    x.setGridColor(Color.parseColor("#ff303030"));
    x.setTextColor(Color.WHITE);
    x.setTextSize(8);


    x.setPosition(XAxis.XAxisPosition.BOTTOM);
    x.setAxisLineWidth(3.0f);

    x.setAxisLineColor(Color.parseColor("#F3AE4E"));


    YAxis y = mChart.getAxisLeft();
    y.setEnabled(true);
    y.setLabelCount(6, false);
    y.setStartAtZero(true);
    y.setTextColor(Color.WHITE);
    y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
    y.setDrawGridLines(false);
    y.setAxisLineWidth(3.0f);
    y.setTextSize(5);
    y.setValueFormatter(new MyValueYFormatter());
//    y.setAxisLineColor(Color.parseColor("#F3AE4E"));
    y.setAxisLineColor(Color.TRANSPARENT);

    ChartMarkerView mv = new ChartMarkerView(this, R.layout.chart_marker_view);
    // set the marker to the chart
    mChart.setMarkerView(mv);

    mChart.getAxisRight().setEnabled(false);
  }
  private void drawChart(ArrayList<Entry> vals1) {

    // create a dataset and give it a type
    LineDataSet set1 = new LineDataSet(vals1, "DataSet 1");
    ArrayList<String> xVals = new ArrayList<>();
    for (int i = 0; i < 7; i++) {
      xVals.add((i) + "");
    }


    LimitLine ll1;
    int DAILY_USAGE_UPPER_LIMIT_MINUTE = SettingsUtil.getInt("goal");
    if(IS_MINUTE)
      ll1= new LimitLine(DAILY_USAGE_UPPER_LIMIT_MINUTE, "Upper Limit");
    else
      ll1= new LimitLine( ((float)DAILY_USAGE_UPPER_LIMIT_MINUTE) / 60, "Upper Limit");

    ll1.setLineWidth(2f);
    ll1.enableDashedLine(2f, 2f, 2f);
    ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
    ll1.setTextSize(10f);
    ll1.setTextColor(Color.WHITE);

    YAxis y = mChart.getAxisLeft();
    y.removeAllLimitLines();
    y.addLimitLine(ll1);


    set1.setDrawCubic(true);
//    set1.setCubicIntensity(0.2f);
    set1.setDrawFilled(true);
    set1.setDrawCircles(false);
    set1.setLineWidth(1.8f);
    set1.setCircleSize(4f);
    set1.setCircleColor(Color.WHITE);
    set1.setHighLightColor(Color.rgb(244, 117, 117));
    set1.setColor(Color.WHITE);
    set1.setFillColor(Color.parseColor("#607D8B"));
    set1.setFillAlpha(50);
    set1.setDrawHorizontalHighlightIndicator(false);
    set1.setFillFormatter(new FillFormatter() {
      @Override
      public float getFillLinePosition(LineDataSet dataSet, LineDataProvider dataProvider) {
        return -10;
      }
    });

    ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
    dataSets.add(set1); // add the datasets

//     create a data object with the datasets
    LineData data = new LineData(xVals, dataSets);
    data.setValueTextSize(9f);
    data.setDrawValues(true);
    data.setValueFormatter(new MyValueFormatter());

    mChart.setData(data);
    mChart.getLegend().setEnabled(false);

    mChart.animateY(2000);
    for (DataSet<?> set : mChart.getData().getDataSets()) {
      set.setDrawValues(!set.isDrawValuesEnabled());
    }
    mChart.setNoDataText("No data Available");
    // dont forget to refresh the drawing
    mChart.invalidate();
  }
  private void refreshCard() {

    int[] weekUsage = TrackAccessibilityUtil.weekUsage(CURRENT_WEEK, this);
    int weekTotal = TrackAccessibilityUtil.getTotalInArray(weekUsage);
    int[] addict = TrackAccessibilityUtil.dayCategoryClicksInWeek(CURRENT_WEEK, this);
    int[] addictDay = TrackAccessibilityUtil.getUsageValuation(addict);
    int heavyAddict = addictDay[0];

    addictDayTv.setText(String.valueOf(heavyAddict));
    weekTotalTv.setText(String.valueOf(weekTotal));
  }


  private ArrayList<Entry> setData(int week) { // 0: current week, 1: last week
    long time = TrackAccessibilityUtil.getPrevXWeek(week);
    int[] x = TrackAccessibilityUtil.weekUsage(time, this);

    ArrayList<Entry> vals1 = new ArrayList<>();

    for (int i = 0; i < 7; i++) {
        if (IS_MINUTE)
          x[i] /= 60;
        else
          x[i] /= 3600;
        float val = (float) x[i];
        vals1.add(new Entry(val, i));
//      if(IS_MINUTE)
//        vals1.add(new Entry((val / 60), i));
//      else
//        vals1.add(new Entry((val / 3600), i));
    }
    return vals1;
  }

}