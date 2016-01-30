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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dots.focus.R;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import org.w3c.dom.Text;

public class DashboardDonutFragment extends SampleFragment {

  final private float mTrackBackWidth = 20f;
  final private float mTrackWidth = 20f;
  final private boolean mClockwise = true;
  final private boolean mRounded = true;
  final private boolean mPie = false;
  final private int mTotalAngle = 360;
  final private int mRotateAngle = 0;
  private int mBackIndex;
  private int mSeries1Index;
  private int mSeries2Index;
  private int mSeries3Index;
  private int mSeries4Index;
  private int mStyleIndex;

  public DashboardDonutFragment() {}

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_dashboard_donut, container, false);
  }

  @Override
  protected void createTracks() {
    setDemoFinished(false);
    final float seriesMax = 50f;
    final DecoView decoView = getDecoView();
    final View view = getView();
    if (decoView == null || view == null) {
      return;
    }
    decoView.deleteAll();
    decoView.configureAngles(mTotalAngle, mRotateAngle);

    SeriesItem arcBackTrack = new SeriesItem.Builder(Color.argb(255, 228, 228, 228))
      .setRange(0, seriesMax, seriesMax)
      .setInitialVisibility(false)
      .setLineWidth(getDimension(mTrackBackWidth))
      .setChartStyle(mPie ? SeriesItem.ChartStyle.STYLE_PIE : SeriesItem.ChartStyle.STYLE_DONUT)
      .build();

    mBackIndex = decoView.addSeries(arcBackTrack);

    float inset = 0;
    if (mTrackBackWidth != mTrackWidth) {
      inset = getDimension((mTrackBackWidth - mTrackWidth) / 2);
    }
    SeriesItem seriesItem1 = new SeriesItem.Builder(COLOR_NEUTRAL)
      .setRange(0, seriesMax, 0)
      .setInitialVisibility(false)
      .setLineWidth(getDimension(mTrackWidth))
      .setInset(new PointF(-inset, -inset))
      .setSpinClockwise(mClockwise)
      .setCapRounded(mRounded)
      .setChartStyle(mPie ? SeriesItem.ChartStyle.STYLE_PIE : SeriesItem.ChartStyle.STYLE_DONUT)
      .build();

    mSeries1Index = decoView.addSeries(seriesItem1);

    SeriesItem seriesItem2 = new SeriesItem.Builder(COLOR_YELLOW)
      .setRange(0, seriesMax, 0)
      .setInitialVisibility(false)
      .setCapRounded(true)
      .setLineWidth(getDimension(mTrackWidth))
      .setInset(new PointF(inset, inset))
      .setCapRounded(mRounded)
      .build();

    mSeries2Index = decoView.addSeries(seriesItem2);

    SeriesItem seriesItem3 = new SeriesItem.Builder(COLOR_PINK)
      .setRange(0, seriesMax, 0)
      .setInitialVisibility(false)
      .setCapRounded(true)
      .setLineWidth(getDimension(mTrackWidth))
      .setInset(new PointF(inset, inset))
      .setCapRounded(mRounded)
      .build();

    mSeries3Index = decoView.addSeries(seriesItem3);

    SeriesItem seriesItem4 = new SeriesItem.Builder(COLOR_BLUE)
                            .setRange(0, seriesMax, 0)
                            .setInitialVisibility(false)
                            .setCapRounded(true)
                            .setLineWidth(getDimension(mTrackWidth))
                            .setInset(new PointF(inset, inset))
                            .setCapRounded(mRounded)
                            .build();

    mSeries4Index = decoView.addSeries(seriesItem4);

    final TextView textPercent = (TextView) view.findViewById(R.id.textPercentage);
    if (textPercent != null) {
      textPercent.setText("02:25:53");
//      addProgressListener(seriesItem1, textPercent, "%.0f%%");

    }

    final TextView textToGo = (TextView) view.findViewById(R.id.textRemaining);
    textToGo.setText("剩餘00:34:07");
//    addProgressRemainingListener(seriesItem1, textToGo, "%.0f min to goal", seriesMax);

    final TextView textTodayUsage = (TextView)view.findViewById(R.id.textTodayUsage);


    View layout = getView().findViewById(R.id.layoutActivities);
    layout.setVisibility(View.INVISIBLE);

    final TextView textActivity1 = (TextView) getView().findViewById(R.id.textActivity1);
//    addProgressListener(seriesItem1, textActivity1, "%.0f Km");
    textActivity1.setText("");

    final TextView textActivity2 = (TextView) getView().findViewById(R.id.textActivity2);
    textActivity2.setText("");
//    addProgressListener(seriesItem2, textActivity2, "%.0f Km");

    final TextView textActivity3 = (TextView) getView().findViewById(R.id.textActivity3);
    textActivity3.setText("");
//    addProgressListener(seriesItem3, textActivity3, "%.0f Km");
    final TextView textActivity4 = (TextView) getView().findViewById(R.id.textActivity4);
    textActivity4.setText("");
//    addProgressListener(seriesItem4, textActivity4, "%.0f Km");
  }

  @Override
  protected void setupEvents() {
    final DecoView decoView = getDecoView();
    final View view = getView();
    if (decoView == null || decoView.isEmpty() || view == null) {
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



    decoView.addEvent(new DecoEvent.Builder(48).setIndex(mSeries1Index).setDelay(3300).build());
    decoView.addEvent(new DecoEvent.Builder(45).setIndex(mSeries2Index).setDelay(3900).build());
    decoView.addEvent(new DecoEvent.Builder(40).setIndex(mSeries3Index).setDelay(4500).build());
    decoView.addEvent(new DecoEvent.Builder(30).setIndex(mSeries4Index).setDelay(5000).build());
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
}