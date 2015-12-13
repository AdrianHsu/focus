package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import com.dots.focus.R;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.LineDataProvider;

import java.util.ArrayList;

public class TotalAppUsageWeeklyChartActivity extends OverviewChartActivity implements OnSeekBarChangeListener {

  private LineChart mChart;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//      WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_linechart);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    mChart = (LineChart) findViewById(R.id.chart1);
//    mChart.setViewPortOffsets(80, 40, 80, 40);
    mChart.setViewPortOffsets(0, 0, 0, 0);
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


    x.setPosition(XAxis.XAxisPosition.BOTTOM);
    x.setAxisLineWidth(3.0f);

    x.setAxisLineColor(Color.parseColor("#F3AE4E"));

    YAxis y = mChart.getAxisLeft();
    y.setEnabled(false);
    y.setLabelCount(6, false);
    y.setStartAtZero(true);
    y.setTextColor(Color.WHITE);
    y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
    y.setDrawGridLines(false);
    y.setAxisLineWidth(3.0f);
//    y.setAxisLineColor(Color.parseColor("#F3AE4E"));
    y.setAxisLineColor(Color.TRANSPARENTg);

    mChart.getAxisRight().setEnabled(false);

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

//  @Override
//  public boolean onCreateOptionsMenu(Menu menu) {
//    getMenuInflater().inflate(R.menu.line, menu);
//    return true;
//  }
//
//  @Override
//  public boolean onOptionsItemSelected(MenuItem item) {
//
//    switch (item.getItemId()) {
//      case R.id.actionToggleValues: {
//        for (DataSet<?> set : mChart.getData().getDataSets())
//          set.setDrawValues(!set.isDrawValuesEnabled());
//
//        mChart.invalidate();
//        break;
//      }
//      case R.id.actionToggleHighlight: {
//        if(mChart.getData() != null) {
//          mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
//          mChart.invalidate();
//        }
//        break;
//      }
//      case R.id.actionToggleFilled: {
//
//        ArrayList<LineDataSet> sets = (ArrayList<LineDataSet>) mChart.getData()
//          .getDataSets();
//
//        for (LineDataSet set : sets) {
//          if (set.isDrawFilledEnabled())
//            set.setDrawFilled(false);
//          else
//            set.setDrawFilled(true);
//        }
//        mChart.invalidate();
//        break;
//      }
//      case R.id.actionToggleCircles: {
//        ArrayList<LineDataSet> sets = (ArrayList<LineDataSet>) mChart.getData()
//          .getDataSets();
//
//        for (LineDataSet set : sets) {
//          if (set.isDrawCirclesEnabled())
//            set.setDrawCircles(false);
//          else
//            set.setDrawCircles(true);
//        }
//        mChart.invalidate();
//        break;
//      }
//      case R.id.actionToggleCubic: {
//        ArrayList<LineDataSet> sets = (ArrayList<LineDataSet>) mChart.getData()
//          .getDataSets();
//
//        for (LineDataSet set : sets) {
//          if (set.isDrawCubicEnabled())
//            set.setDrawCubic(false);
//          else
//            set.setDrawCubic(true);
//        }
//        mChart.invalidate();
//        break;
//      }
//      case R.id.actionToggleStartzero: {
//        mChart.getAxisLeft().setStartAtZero(!mChart.getAxisLeft().isStartAtZeroEnabled());
//        mChart.getAxisRight().setStartAtZero(!mChart.getAxisRight().isStartAtZeroEnabled());
//        mChart.invalidate();
//        break;
//      }
//      case R.id.actionTogglePinch: {
//        if (mChart.isPinchZoomEnabled())
//          mChart.setPinchZoom(false);
//        else
//          mChart.setPinchZoom(true);
//
//        mChart.invalidate();
//        break;
//      }
//      case R.id.actionToggleAutoScaleMinMax: {
//        mChart.setAutoScaleMinMaxEnabled(!mChart.isAutoScaleMinMaxEnabled());
//        mChart.notifyDataSetChanged();
//        break;
//      }
//      case R.id.animateX: {
//        mChart.animateX(3000);
//        break;
//      }
//      case R.id.animateY: {
//        mChart.animateY(3000);
//        break;
//      }
//      case R.id.animateXY: {
//        mChart.animateXY(3000, 3000);
//        break;
//      }
//      case R.id.actionToggleFilter: {
//
//        // the angle of filtering is 35°
//        Approximator a = new Approximator(ApproximatorType.DOUGLAS_PEUCKER, 35);
//
//        if (!mChart.isFilteringEnabled()) {
//          mChart.enableFiltering(a);
//        } else {
//          mChart.disableFiltering();
//        }
//        mChart.invalidate();
//        break;
//      }
//      case R.id.actionSave: {
//        if (mChart.saveToPath("title" + System.currentTimeMillis(), "")) {
//          Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
//            Toast.LENGTH_SHORT).show();
//        } else
//          Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
//            .show();
//
//        // mChart.saveToGallery("title"+System.currentTimeMillis())
//        break;
//      }
//    }
//    return true;
//  }
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

    ArrayList<String> xVals = new ArrayList<String>();
    for (int i = 0; i < count; i++) {
      xVals.add((i) + "");
    }

    ArrayList<Entry> vals1 = new ArrayList<Entry>();

    for (int i = 0; i < count; i++) {
      float mult = (range + 1);
      float val = (float) (Math.random() * mult);// + (float)
      // ((mult * 0.1) / 10);
      vals1.add(new Entry(val, i));
    }

    // create a dataset and give it a type
    LineDataSet set1 = new LineDataSet(vals1, "DataSet 1");
    set1.setDrawCubic(false);
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

    // create a data object with the datasets
    LineData data = new LineData(xVals, set1);
    data.setValueTextSize(3f);
    data.setDrawValues(true);

    // set data
    mChart.setData(data);
  }
}