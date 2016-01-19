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

import com.afollestad.materialdialogs.MaterialDialog;
import com.dots.focus.R;
import com.dots.focus.adapter.HourAppUsageRecyclerViewAdapter;
import com.dots.focus.adapter.TopThreeAppUsageRecyclerViewAdapter;
import com.dots.focus.util.CreateInfoUtil;
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
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TopThreeAppUsageChartActivity extends OverviewChartActivity implements OnSeekBarChangeListener {

  private LineChart mChart;
  private Spinner spinner;
  private ArrayAdapter<String> timeInterval;
  private String[] timeIntervalArray = {"小時", "分鐘"};
  private UltimateRecyclerView mRecyclerView;
  private LinearLayoutManager linearLayoutManager;
  private TopThreeAppUsageRecyclerViewAdapter topThreeAppUsageRecyclerViewAdapter;
  private Button pickAppBtn = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_top_three_app_usage_chart);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("前三應用軟體用量趨勢");

    final ArrayList<JSONObject> appUsageList = new ArrayList<>();

    for(int i = 0; i < 3; i++) {
      JSONObject appUsage = new JSONObject();
      try {
        appUsage.put("appName", "Facebook");
        appUsage.put("duration", 600); // 600sec
//      app.put(icon); // put icon
      } catch (JSONException e) {
        e.printStackTrace();
      }

      appUsageList.add(appUsage);
    }

    mRecyclerView = (UltimateRecyclerView) findViewById(R.id.top_three_app_usage_recycler_view);
    topThreeAppUsageRecyclerViewAdapter = new TopThreeAppUsageRecyclerViewAdapter( appUsageList,
                            this);
    linearLayoutManager = new LinearLayoutManager(this);

    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(topThreeAppUsageRecyclerViewAdapter);

//    DEBUG: W/System.err: java.lang.RuntimeException: Can't create handler inside thread that has
// not called Looper.prepare()
    spinner = (Spinner)findViewById(R.id.spinner);
    timeInterval = new ArrayAdapter<String>(this, android.R.layout
      .simple_spinner_dropdown_item, timeIntervalArray);
    spinner.setAdapter(timeInterval);

    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) { // hour by default

        } else {

        }
      }
      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {


      }
    });
    pickAppBtn = (Button) findViewById(R.id.pick_app_button);
    pickAppBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        createPickAppDialog();
      }
    });


    mChart = (LineChart) findViewById(R.id.chart1);
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
//    y.setAxisLineColor(Color.parseColor("#F3AE4E"));
    y.setAxisLineColor(Color.TRANSPARENT);

    mChart.getAxisRight().setEnabled(false);

    LimitLine ll1 = new LimitLine(130f, "Upper Limit");
    ll1.setLineWidth(2f);
    ll1.enableDashedLine(2f, 2f, 2f);
    ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
    ll1.setTextSize(10f);
    ll1.setTextColor(Color.WHITE);
    y.addLimitLine(ll1);

    ChartMarkerView mv = new ChartMarkerView(this, R.layout.chart_marker_view);
    // set the marker to the chart
    mChart.setMarkerView(mv);
    // add data
    setData(7, 5);

    mChart.getLegend().setEnabled(false);

    mChart.animateY(2000);
    for (DataSet<?> set : mChart.getData().getDataSets()) {
      set.setDrawValues(!set.isDrawValuesEnabled());
    }
    mChart.setNoDataText("No data Available");
    // dont forget to refresh the drawing
    mChart.invalidate();
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

  private void setData(int count, float range) {

    ArrayList<String> xVals = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      xVals.add((i) + "");
    }

    ArrayList<Entry> vals1 = new ArrayList<>();
    ArrayList<Entry> vals2 = new ArrayList<>();


    for (int i = 0; i < count; i++) {
      float mult = (range + 1);
      float val = (float) (Math.random() * mult);// + (float)
      // ((mult * 0.1) / 10);
      vals1.add(new Entry(val, i));
      vals2.add(new Entry((float)0.8, i));
    }

    // create a dataset and give it a type
    LineDataSet set1 = new LineDataSet(vals1, "DataSet 1");
    LineDataSet limitSet = new LineDataSet(vals2, "Daily Limit");
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
  }
  private void createPickAppDialog() {
    new MaterialDialog.Builder(this)
                            .title("選擇您感興趣的應用軟體") // at most 3 apps limited
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
}