package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dots.focus.R;
import com.dots.focus.model.AppInfo;
import com.dots.focus.util.FetchAppUtil;
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
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TopThreeAppUsageChartActivity extends OverviewChartActivity implements OnSeekBarChangeListener {

  private LineChart mChart;
  private Spinner spinner;
  private ArrayAdapter<String> timeInterval;
  private String[] timeIntervalArray = {"小時", "分鐘"};
  private Button pickAppBtn = null;
  public static View topThreeCardDailyView;
  public static List<List<Integer>> appLengths;
  public static int[] mIndexList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_top_three_app_usage_chart);
    topThreeCardDailyView = findViewById(R.id.top_three_itemview);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("前三應用軟體用量趨勢");


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

    final int length = FetchAppUtil.getSize();
    final String [] appNameArray = new String [length];
    for(int i = 0; i < length; i++) {
      appNameArray[i] = FetchAppUtil.getApp(i).getName();
    }
    pickAppBtn = (Button) findViewById(R.id.pick_app_button);
    pickAppBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        createPickAppDialog(appNameArray);
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

    LimitLine ll1 = new LimitLine(5f, "Upper Limit");
    ll1.setLineWidth(2f);
    ll1.enableDashedLine(2f, 2f, 2f);
    ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
    ll1.setTextSize(10f);
    ll1.setTextColor(Color.WHITE);
    y.addLimitLine(ll1);

    TopThreeChartMarkerView mv = new TopThreeChartMarkerView(this, R.layout
                            .top_three_chart_marker_view);
    // set the marker to the chart
    mChart.setMarkerView(mv);
    // add data
    for(int i = 0; i < 3; i++) {
      ArrayList<Entry> vals1 = setTopThreeData(i);
      drawChart(vals1, i);
    }

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
  private ArrayList<Entry> setTopThreeData(int appRank) {

    Calendar calendar = Calendar.getInstance();
    long time = System.currentTimeMillis() + TrackAccessibilityUtil.getTimeOffset() *
                            TrackAccessibilityUtil.anHour,
                            oneDay = 86400000;
    time = oneDay * (time / oneDay);
    calendar.setTimeInMillis(time);
    Log.d("TrackAccessibilityUtil", "calendar.get(Calendar.DAY_OF_WEEK): " + calendar.get
                            (Calendar.DAY_OF_WEEK));
    calendar.setTimeInMillis(time - TrackAccessibilityUtil.getTimeOffset() *
            TrackAccessibilityUtil.anHour - (calendar.get(Calendar.DAY_OF_WEEK)  - 1) * oneDay);
    //- 7 * oneDay * week

    appLengths = TrackAccessibilityUtil.weekAppUsage(calendar
                            .getTimeInMillis());
    List<Integer> appLength = appLengths.get(7);

    ArrayList<Entry> indexList = new ArrayList<>(appLength.size());
    for (int i = 0, size = appLength.size(); i < size; ++i)
      indexList.add(new Entry(appLength.get(i), i));

    Collections.sort(indexList, new Comparator<Entry>() {
      @Override
      public int compare(Entry e1, Entry e2) {
        return (int) (e2.getVal() - e1.getVal());
      }
    });
    mIndexList = new int[3];
    for (int i = 0; i < 3; i++) {
      mIndexList[i] = indexList.get(i).getXIndex();
    }
    // top 1 app
    ArrayList<Entry> vals1 = new ArrayList<>();
    for (int i = 0; i < 7; i++) {
      int mTime = appLengths.get(i).get(mIndexList[appRank]);
      vals1.add(new Entry(mTime, i));
    }
    return vals1;
  }
  private void drawChart(ArrayList<Entry> vals1, int index) {

    // create a dataset and give it a type
    LineDataSet set1 = new LineDataSet(vals1, "DataSet 1");
    set1.setDrawCubic(true);
//    set1.setCubicIntensity(0.2f);
    set1.setDrawFilled(true);
    set1.setDrawCircles(false);
    set1.setLineWidth(1.8f);
    set1.setCircleSize(4f);
    set1.setCircleColor(Color.WHITE);
    set1.setHighLightColor(Color.rgb(244, 117, 117));
    if(index == 0) {
      set1.setColor(ContextCompat.getColor(this, R.color.top_three_first));
      set1.setFillColor(ContextCompat.getColor(this, R.color.top_three_first));
    }
    else if (index == 1) {
      set1.setColor(ContextCompat.getColor(this, R.color.top_three_second));
      set1.setFillColor(ContextCompat.getColor(this, R.color.top_three_second));

    }
    else if (index == 2) {
      set1.setColor(ContextCompat.getColor(this, R.color.top_three_third));
      set1.setFillColor(ContextCompat.getColor(this, R.color.top_three_third));
    }

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

    ArrayList<String> xVals = new ArrayList<>();
    for (int i = 0; i < 7; i++) {
      xVals.add((i) + "");
    }
//     create a data object with the datasets
    LineData data = new LineData(xVals, dataSets);
    data.setValueTextSize(9f);
    data.setDrawValues(true);

    mChart.setData(data);
  }

  private void createPickAppDialog(String[] appNameList) {
    new MaterialDialog.Builder(this)
                            .title("選擇您感興趣的App(至多三個)") // at most 3 apps limited
                            .items(appNameList)
                            .itemsCallbackMultiChoice(new Integer[]{1, 3}, new MaterialDialog
                                                    .ListCallbackMultiChoice() {
                              @Override
                              public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                boolean allowSelection = which.length <= 3; // limit selection to
                                // 3, the new selection is included in the which array
                                if (!allowSelection) {

                                }
                                return allowSelection;
                              }
                            })
                            .positiveText("完成")
                            .alwaysCallMultiChoiceCallback() // the callback will always be called, to check if selection is still allowed
                            .show();
  }
  private String timeToString(int seconds) {
    int day = (int) TimeUnit.SECONDS.toDays(seconds);
    long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
    long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
    long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
    return String.format("%02d:%02d:%02d", hours, minute, second);
  }

}