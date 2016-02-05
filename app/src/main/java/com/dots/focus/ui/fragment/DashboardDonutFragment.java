/*
 * Copyright (C) 2015 Brent Marriott
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dots.focus.ui.fragment;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.concurrent.TimeUnit;

import com.dots.focus.R;
import com.dots.focus.model.AppInfo;
import com.dots.focus.util.FetchAppUtil;
import com.dots.focus.util.FetchFriendUtil;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import org.json.JSONObject;
import org.w3c.dom.Text;

public class DashboardDonutFragment extends SampleFragment {

  final private float mTrackBackWidth = 20f;
  final private float mTrackWidth = 20f;
  final private boolean mClockwise = true;
  final private boolean mRounded = true;
  final private boolean mPie = false;
  final private int mTotalAngle = 360;
  final private int mRotateAngle = 0;
  final private float seriesMax = 100f;
  private float [] accumulateOutput = new float[4];
  private int mBackIndex;
  private int mSeries1Index; // others
  private int mSeries2Index;
  private int mSeries3Index;
  private int mSeries4Index; // top 1
  private SeriesItem seriesItem1; // others
  private SeriesItem seriesItem2;
  private SeriesItem seriesItem3;
  private SeriesItem seriesItem4; // top 1
  private SeriesItem arcBackTrack;
  private int mStyleIndex;

  private static final String TAG = "donut";

  public DashboardDonutFragment(){}


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_dashboard_donut, container, false);
  }

  @Override
  protected void createTracks() {

    int [][] data = TrackAccessibilityUtil.getDayFirstThreeApp(0);

    initData(data);
    setDemoFinished(false);
    final DecoView decoView = getDecoView();
    final View view = getView();
    if (decoView == null || view == null) {
      return;
    }
    decoView.deleteAll();
    decoView.configureAngles(mTotalAngle, mRotateAngle);

    float inset = 0;
    if (mTrackBackWidth != mTrackWidth) {
      inset = getDimension((mTrackBackWidth - mTrackWidth) / 2);
    }
    initSeriesIndex(inset, decoView);
    initTotalUsage(view, data);
    initTopThreeProgress(data);
//    View layout = getView().findViewById(R.id.layoutActivities);
//    layout.setVisibility(View.INVISIBLE);

  }

  @Override
  protected void setupEvents() {
    final DecoView decoView = getDecoView();
    final View view = getView();
    if (decoView == null || decoView.isEmpty() || view == null) {
      Log.d(TAG, "decoView == null || decoView.isEmpty() || view == null");
      return;
    }

    mUpdateListeners = true;
    final TextView textPercent = (TextView) view.findViewById(R.id.textPercentage);
    final TextView textToGo = (TextView) view.findViewById(R.id.textRemaining);
    final TextView textTodayUsage = (TextView) view.findViewById(R.id.textTodayUsage);
    final View layout = view.findViewById(R.id.layoutActivities);
    final View[] linkedViews = {textPercent, textToGo, textTodayUsage, layout};
    final int fadeDuration = 2000;

    if (mPie) {
      decoView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
        .setIndex(mBackIndex)
        .setDuration(2000)
        .build());
    } else {
      decoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT_FILL)
        .setIndex(mBackIndex)
        .setDuration(3000)
        .build());

      decoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
        .setIndex(mSeries1Index)
        .setFadeDuration(fadeDuration)
        .setDuration(2000)
        .setDelay(1000)
        .build());
    }

    decoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
      .setIndex(mSeries2Index)
      .setLinkedViews(linkedViews)
      .setDuration(2000)
      .setDelay(1100)
      .build());
    decoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
      .setIndex(mSeries3Index)
      .setLinkedViews(linkedViews)
      .setDuration(2000)
      .setDelay(1200)
      .build());
    decoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
      .setIndex(mSeries4Index)
      .setLinkedViews(linkedViews)
      .setDuration(2000)
      .setDelay(1300)
      .build());

    //mSeries1Index is others app
    //mSeries4Index is top 1 app
    decoView.addEvent(new DecoEvent.Builder(accumulateOutput[3]).setIndex(mSeries1Index).setDelay
                            (3300).build
                            ());
    decoView.addEvent(new DecoEvent.Builder(accumulateOutput[2]).setIndex(mSeries2Index).setDelay
                            (3900).build
                            ());
    decoView.addEvent(new DecoEvent.Builder(accumulateOutput[1]).setIndex(mSeries3Index).setDelay
                            (4500).build
                            ());
    decoView.addEvent(new DecoEvent.Builder(accumulateOutput[0]).setIndex(mSeries4Index).setDelay
                            (5000).build
                            ());
//    decoView.addEvent(new DecoEvent.Builder(50).setIndex(mSeries1Index).setDuration(1500).setDelay(9000).build());
//    decoView.addEvent(new DecoEvent.Builder(0).setIndex(mSeries1Index).setDuration(500).setDelay(10500)
//      .setListener(new DecoEvent.ExecuteEventListener() {
//        @Override
//        public void onEventStart(DecoEvent event) {
//          mUpdateListeners = false;
//        }
//
//        @Override
//        public void onEventEnd(DecoEvent event) {
//
//        }
//      })
//      .setInterpolator(new AccelerateInterpolator()).build());
//
//    decoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_EXPLODE)
//      .setLinkedViews(linkedViews)
//      .setIndex(mSeries1Index)
//      .setDelay(11000)
//      .setDuration(3000)
//      .setDisplayText("GOAL!")
//      .setListener(new DecoEvent.ExecuteEventListener() {
//        @Override
//        public void onEventStart(DecoEvent event) {
//
//        }
//
//        @Override
//        public void onEventEnd(DecoEvent event) {
////          mStyleIndex++;
//          if (mStyleIndex >= mTrackBackWidth.length) {
//            mStyleIndex = 0;
//            setDemoFinished(true);
//            return;
//          }
//
//          createTracks();
//          setupEvents();
//        }
//      })
//      .build());
  }
  private void initData(int [][] data) {

//    int [][] data = new int [4][2];
//    data[0][1] = 555; // second
//    data[1][1] = 444;
//    data[2][1] = 333;
//    data[3][1] = 111;
//    data[0][0] = 0; // app index
//    data[1][0] = 0;
//    data[2][0] = 0;
//    data[3][0] = 0;

    float total = 0;
    for(int i = 0; i < 4; i++) {
      total += data[i][1];
    }
    float [] separableOutput = new float [4];

    for(int i = 0; i < 4; i ++) {
      separableOutput[i] = (data[i][1]/total) * seriesMax;
    }
    accumulateOutput[3] = separableOutput[0] + separableOutput[1] + separableOutput[2] +
                            separableOutput[3];
    accumulateOutput[2] = separableOutput[0] + separableOutput[1] + separableOutput[2];
    accumulateOutput[1] = separableOutput[0] + separableOutput[1];
    accumulateOutput[0] = separableOutput[0];
  }
  private void initSeriesIndex(float inset, DecoView decoView) {

    arcBackTrack = new SeriesItem.Builder(Color.argb(255, 228, 228, 228))
                            .setRange(0, seriesMax, seriesMax)
                            .setInitialVisibility(false)
                            .setLineWidth(getDimension(mTrackBackWidth))
                            .setChartStyle(mPie ? SeriesItem.ChartStyle.STYLE_PIE : SeriesItem.ChartStyle.STYLE_DONUT)
                            .build();

    mBackIndex = decoView.addSeries(arcBackTrack);

    seriesItem1 = new SeriesItem.Builder(COLOR_NEUTRAL)
                            .setRange(0, seriesMax, 0)
                            .setInitialVisibility(false)
                            .setLineWidth(getDimension(mTrackWidth))
                            .setInset(new PointF(-inset, -inset))
                            .setSpinClockwise(mClockwise)
                            .setCapRounded(mRounded)
                            .setChartStyle(mPie ? SeriesItem.ChartStyle.STYLE_PIE : SeriesItem.ChartStyle.STYLE_DONUT)
                            .build();

    mSeries1Index = decoView.addSeries(seriesItem1);

    seriesItem2 = new SeriesItem.Builder(COLOR_YELLOW)
                            .setRange(0, seriesMax, 0)
                            .setInitialVisibility(false)
                            .setCapRounded(true)
                            .setLineWidth(getDimension(mTrackWidth))
                            .setInset(new PointF(inset, inset))
                            .setCapRounded(mRounded)
                            .build();

    mSeries2Index = decoView.addSeries(seriesItem2);

    seriesItem3 = new SeriesItem.Builder(COLOR_PINK)
                            .setRange(0, seriesMax, 0)
                            .setInitialVisibility(false)
                            .setCapRounded(true)
                            .setLineWidth(getDimension(mTrackWidth))
                            .setInset(new PointF(inset, inset))
                            .setCapRounded(mRounded)
                            .build();

    mSeries3Index = decoView.addSeries(seriesItem3);

    seriesItem4 = new SeriesItem.Builder(COLOR_BLUE)
                            .setRange(0, seriesMax, 0)
                            .setInitialVisibility(false)
                            .setCapRounded(true)
                            .setLineWidth(getDimension(mTrackWidth))
                            .setInset(new PointF(inset, inset))
                            .setCapRounded(mRounded)
                            .build();

    mSeries4Index = decoView.addSeries(seriesItem4);
  }
  private void initTotalUsage(View view, int[][] data) {
    final TextView textPercent = (TextView) view.findViewById(R.id.textPercentage);
    if (textPercent != null) {
      int time = 0;
      for(int i = 0 ; i < 4; i++)
        time += data[i][1];

      textPercent.setText(timeToString((time)));
//      addProgressListener(seriesItem1, textPercent, "%.0f%%");
    }

    final TextView textToGo = (TextView) view.findViewById(R.id.textRemaining);
    textToGo.setText("剩餘00:34:07");
//    addProgressRemainingListener(seriesItem1, textToGo, "%.0f min to goal", seriesMax);

  }
  private void initTopThreeProgress(int[][] data) {

    View v = DashboardFragment.topThreeCardView;
    View [] itemViewArray = new View [4];
    itemViewArray[0] = v.findViewById(R.id.first_adapter);
    itemViewArray[1] = v.findViewById(R.id.second_adapter);
    itemViewArray[2] = v.findViewById(R.id.third_adapter);
    itemViewArray[3] = v.findViewById(R.id.others_adapter);

    for(int i = 0; i < 4; i++) {
      AppInfo mAppInfo = FetchAppUtil.getApp(data[i][0]);
      TextView appNameTv = (TextView) itemViewArray[i].findViewById(R.id.app_name);
      TextView appTimeTv = (TextView) itemViewArray[i].findViewById(R.id.app_time);
      ImageView appIconIv = (ImageView) itemViewArray[i].findViewById(R.id.imageview);
      Drawable mIcon = mAppInfo.getIcon();

      if(mIcon != null) {
        if(i != 3) {
          appNameTv.setText(mAppInfo.getName());
          appIconIv.setImageDrawable(mIcon);
        } else {
          appNameTv.setText("其他應用軟體");
        }
      }
      switch(i) {
        case 0:
//          addProgressListener(seriesItem1, appTimeTv, "%.0f");
          appTimeTv.setText(timeToString(data[0][1]));
          appTimeTv.setTextColor(COLOR_BLUE);
          break;
        case 1:
//          addProgressListener(seriesItem2, appTimeTv, "%.0f");
          appTimeTv.setText(timeToString(data[1][1]));
          appTimeTv.setTextColor(COLOR_PINK);
          break;
        case 2:
//          addProgressListener(seriesItem3, appTimeTv, "%.0f");
          appTimeTv.setText(timeToString(data[2][1]));
          appTimeTv.setTextColor(COLOR_YELLOW);
          break;
        case 3:
//          addProgressListener(seriesItem4, appTimeTv, "%.0f");
          appTimeTv.setText(timeToString(data[3][1]));
          appTimeTv.setTextColor(COLOR_NEUTRAL);
          break;
      }
    }
  }
  private String timeToString(int seconds) {
    int day = (int)TimeUnit.SECONDS.toDays(seconds);
    long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
    long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
    long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
    return String.format("%02d:%02d:%02d", hours, minute, second);
  }
}