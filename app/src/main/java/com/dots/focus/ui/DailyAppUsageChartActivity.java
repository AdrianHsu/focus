package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.dots.focus.R;
import com.dots.focus.adapter.MessagesRecyclerViewAdapter;
import com.dots.focus.util.SettingsUtil;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.LineDataProvider;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DailyAppUsageChartActivity extends OverviewChartActivity implements OnSeekBarChangeListener {

  private LineChart mChart;
  private Spinner spinner;
  private ArrayAdapter<String> timeInterval;
  private String[] timeIntervalArray = {"分段", "累計"};
  public static View topThreeCardHourlyView;
  public static int day = 0;
  private TextView daySwitchTv;
  private Button daySwitchLeftBtn;
  private Button daySwitchRightBtn;
  private int CURRENT_DAY;
  private boolean IS_ACCUMULATE;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_daily_app_usage_chart);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.title_daily_app_usage_overview));
    topThreeCardHourlyView = findViewById(R.id.top_three_card_hourly);

    daySwitchTv = (TextView) findViewById(R.id.day_switch_textview);
    String day = TrackAccessibilityUtil.dayString(0);
    daySwitchTv.setText(day);

    daySwitchLeftBtn = (Button) findViewById(R.id.day_switch_left_btn);
    daySwitchLeftBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        CURRENT_DAY++;
        daySwitchTv = (TextView) findViewById(R.id.day_switch_textview);
        String day = TrackAccessibilityUtil.dayString(CURRENT_DAY);
        daySwitchTv.setText(day);
        ArrayList<Entry> val = setData(CURRENT_DAY, IS_ACCUMULATE);
        drawChart(val, IS_ACCUMULATE);
        daySwitchRightBtn.setEnabled(true);
//        daySwitchLeftBtn.setEnabled(false);
      }
    });
    daySwitchRightBtn = (Button) findViewById(R.id.day_switch_right_btn);
    daySwitchRightBtn.setEnabled(false);
    daySwitchRightBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        CURRENT_DAY--;
        daySwitchTv = (TextView) findViewById(R.id.day_switch_textview);
        String day = TrackAccessibilityUtil.dayString(CURRENT_DAY);
        daySwitchTv.setText(day);
        ArrayList<Entry> val = setData(CURRENT_DAY, IS_ACCUMULATE);
        drawChart(val, IS_ACCUMULATE);
        if (CURRENT_DAY == 0)
          daySwitchRightBtn.setEnabled(false);
        daySwitchLeftBtn.setEnabled(true);
      }
    });

//    DEBUG: W/System.err: java.lang.RuntimeException: Can't create handler inside thread that has
// not called Looper.prepare()
    spinner = (Spinner)findViewById(R.id.spinner);
    timeInterval = new ArrayAdapter<String>(this, android.R.layout
      .simple_spinner_dropdown_item, timeIntervalArray);
    spinner.setAdapter(timeInterval);

    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) { // separate by default
          IS_ACCUMULATE = false;
        } else if (i == 1) {
          IS_ACCUMULATE = true;
        }
        ArrayList<Entry> val = setData(CURRENT_DAY, IS_ACCUMULATE);
        drawChart(val, IS_ACCUMULATE);
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {


      }
    });
    mChart = (LineChart) findViewById(R.id.chart1);

    // add data
    ArrayList<Entry> val = setData(CURRENT_DAY, false);
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
  private void drawChart(ArrayList<Entry> vals1, boolean IS_ACCUMULATE) {

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
    x.setDrawGridLines(false);
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
    y.setTextSize(6);
    y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
    y.setDrawGridLines(false);
    y.setAxisLineWidth(3.0f);
//    y.setAxisLineColor(Color.parseColor("#F3AE4E"));
    y.setAxisLineColor(Color.TRANSPARENT);

    mChart.getAxisRight().setEnabled(false);

    int DAILY_USAGE_UPPER_LIMIT_SECOND = SettingsUtil.getInt("goal") * 60;

    LimitLine ll1 = new LimitLine(DAILY_USAGE_UPPER_LIMIT_SECOND, "Upper Limit");
    ll1.setLineWidth(2f);
    ll1.enableDashedLine(2f, 2f, 2f);
    ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
    ll1.setTextSize(10f);
    ll1.setTextColor(Color.WHITE);
    y.addLimitLine(ll1);

    DailyChartMarkerView mv = new DailyChartMarkerView(this, R.layout.daily_chart_marker_view);

    // set the marker to the chart
    mChart.setMarkerView(mv);
    // create a dataset and give it a type
    LineDataSet set1 = new LineDataSet(vals1, "DataSet 1");
    set1.setDrawCubic(true);
    set1.setCubicIntensity(0.2f);
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

    ArrayList<String> xVals = new ArrayList<>();
    for (int i = 0; i < 24; i++) {
      xVals.add((i) + "");
    }

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
  private ArrayList<Entry> setData(int day, boolean IS_ACCUMULATE) {

    long time = System.currentTimeMillis(),
        offset = TrackAccessibilityUtil.getTimeOffset() * TrackAccessibilityUtil.anHour;
    time = 86400000 * ((time + offset) / 86400000 - day) - offset;

    int[] x = TrackAccessibilityUtil.dayUsage(time);
    ArrayList<Entry> vals1 = new ArrayList<>();

    if(!IS_ACCUMULATE) {
      for (int i = 0; i < 24; i++) {
        vals1.add(new Entry((float)x[i], i));
      }
    } else {
      int count = 0;
      for (int i = 0; i < 24; i++) {
        count += x[i];
        vals1.add(new Entry(count, i));
      }
    }

    return vals1;
  }
}