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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;


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

import java.util.ArrayList;
import java.util.Calendar;

public class WeeklyAppUsageChartActivity extends OverviewChartActivity implements OnSeekBarChangeListener {

  private LineChart mChart;
  private Spinner spinner;
  private ArrayAdapter<String> timeInterval;
  private String[] timeIntervalArray = {"秒鐘", "分鐘"};


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_weekly_app_usage_chart);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("每週總時數趨勢");


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
          // add data
          ArrayList<Entry> val = setData(0, false);
          drawChart(val, false);
        } else if (i == 1) {
          // add data
          ArrayList<Entry> val = setData(0, true);
          drawChart(val, false);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });

    mChart = (LineChart) findViewById(R.id.chart1);

    // add data
    ArrayList<Entry> val = setData(0, false);
    drawChart(val, false);
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
  private void drawChart(ArrayList<Entry> vals1, boolean IS_MINUTE) {

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
    // create a dataset and give it a type
    LineDataSet set1 = new LineDataSet(vals1, "DataSet 1");
    ArrayList<String> xVals = new ArrayList<>();
    for (int i = 0; i < 7; i++) {
      xVals.add((i) + "");
    }

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
//    y.setAxisLineColor(Color.parseColor("#F3AE4E"));
    y.setAxisLineColor(Color.TRANSPARENT);

    mChart.getAxisRight().setEnabled(false);

    LimitLine ll1;
    int DAILY_USAGE_UPPER_LIMIT_MINUTE = 5;
    if(IS_MINUTE)
      ll1= new LimitLine(DAILY_USAGE_UPPER_LIMIT_MINUTE, "Upper Limit");
    else
      ll1= new LimitLine(DAILY_USAGE_UPPER_LIMIT_MINUTE * 60, "Upper Limit");

    ll1.setLineWidth(2f);
    ll1.enableDashedLine(2f, 2f, 2f);
    ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
    ll1.setTextSize(10f);
    ll1.setTextColor(Color.WHITE);
    y.addLimitLine(ll1);

    ChartMarkerView mv = new ChartMarkerView(this, R.layout.chart_marker_view);
    // set the marker to the chart
    mChart.setMarkerView(mv);

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
  private ArrayList<Entry> setData(int week, boolean IS_MINUTE) { // 0: current week, 1: last week

    Calendar calendar = Calendar.getInstance();
    long time = System.currentTimeMillis() + TrackAccessibilityUtil.getTimeOffset() *
            TrackAccessibilityUtil.anHour,
         oneDay = 86400000;
    time = oneDay * (time / oneDay);
    calendar.setTimeInMillis(time);
    Log.d("TrackAccessibilityUtil", "calendar.get(Calendar.DAY_OF_WEEK): " + calendar.get
            (Calendar.DAY_OF_WEEK));
    calendar.setTimeInMillis(time - 7 * oneDay * week - (calendar.get(Calendar.DAY_OF_WEEK) - 1) *
            oneDay - TrackAccessibilityUtil.getTimeOffset() * TrackAccessibilityUtil.anHour);
    int[] x = TrackAccessibilityUtil.weekUsage(calendar);

    ArrayList<Entry> vals1 = new ArrayList<>();

    for (int i = 0; i < 7; i++) {
      if(IS_MINUTE)
        vals1.add(new Entry( (float)(x[i] / 60), i));
      else
        vals1.add(new Entry(x[i], i));
    }
    return vals1;
  }

}