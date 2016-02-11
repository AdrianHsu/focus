package com.dots.focus.ui;

/**
 * Created by AdrianHsu on 2016/1/19.
 */
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dots.focus.R;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.parse.ParseUser;


import java.util.ArrayList;

public class RadarChartActivity extends OverviewChartActivity {

  private RadarChart mChart;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_radar_chart);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    getSupportActionBar().setTitle(getResources().getString(R.string.title_my_radar_chart_overview));

    mChart = (RadarChart) findViewById(R.id.chart1);

    mChart.setDescription("");

    mChart.setWebLineWidth(1.5f);
    mChart.setWebLineWidthInner(0.75f);
    mChart.setWebAlpha(100);

    // create a custom MarkerView (extend MarkerView) and specify the layout
    // to use for it
    ChartMarkerView mv = new ChartMarkerView(this, R.layout.chart_marker_view);

    // set the marker to the chart
    mChart.setMarkerView(mv);
    mChart.setRotationEnabled(true);

    setData();

    XAxis xAxis = mChart.getXAxis();
    xAxis.setTextSize(9f);
    xAxis.setTextColor(Color.WHITE);

    YAxis yAxis = mChart.getYAxis();
    yAxis.setLabelCount(5, false);
    yAxis.setTextSize(9f);
    yAxis.setStartAtZero(true);
    yAxis.setTextColor(Color.WHITE);

    Legend l = mChart.getLegend();
    l.setPosition(LegendPosition.RIGHT_OF_CHART);
    l.setXEntrySpace(7f);
    l.setYEntrySpace(5f);
    l.setTextColor(Color.WHITE);
  }

  private String[] mParties = new String[]{
                          "社交行為", "理財工作", "吸收新知", "生活資訊", "遊戲動漫", "攝像影音"
  };

  public void setData() {

    int cnt = 6;

    ArrayList<Entry> yVals1 = new ArrayList<Entry>();
    ArrayList<Entry> yVals2 = new ArrayList<Entry>();
    int[] categoryUsage = TrackAccessibilityUtil.getCategory();

    // IMPORTANT: In a PieChart, no values (Entry) should have the same
    // xIndex (even if from different DataSets), since no values can be
    // drawn above each other.
    for (int i = 0; i < cnt; i++) {
      yVals1.add(new Entry((float) categoryUsage[i], i));
    }

//    for (int i = 0; i < cnt; i++) {
//      yVals2.add(new Entry((float) (Math.random() * mult) + mult / 2, i));
//    }

    ArrayList<String> xVals = new ArrayList<String>();

    for (int i = 0; i < cnt; i++)
      xVals.add(mParties[i % mParties.length]);

    RadarDataSet set1 = new RadarDataSet(yVals1, "使用時間");
    set1.setColor(ContextCompat.getColor(this, R.color.top_three_first));
    set1.setDrawFilled(true);
    set1.setLineWidth(2f);

//    RadarDataSet set2 = new RadarDataSet(yVals2, "上癮程度");
//    set2.setColor(ContextCompat.getColor(this, R.color.top_three_second));
//    set2.setDrawFilled(true);
//    set2.setLineWidth(2f);

    ArrayList<RadarDataSet> sets = new ArrayList<>();
    sets.add(set1);
//    sets.add(set2);

    RadarData data = new RadarData(xVals, sets);
    data.setValueTextSize(8f);
    data.setDrawValues(false);

    mChart.setData(data);
    mChart.getData().setHighlightEnabled(true);
    mChart.invalidate();
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }
}
