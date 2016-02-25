package com.dots.focus.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dots.focus.R;
import com.rey.material.widget.Slider;

import java.util.concurrent.TimeUnit;

/**
 * Created by AdrianHsu on 2016/2/19.
 */
public class LockTimeView extends RelativeLayout {

  private Context mContext;
  private View rootView;
  private Slider slider;
  public static int val;
  private TextView timeTv;

  public LockTimeView(Context context, int lockMaxTime) {

    super(context);
    mContext = context;
    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    rootView = inflater.inflate(R.layout.view_lock_time, this);
    slider = (Slider) rootView.findViewById(R.id.slider1);
    timeTv = (TextView) rootView.findViewById(R.id.timeTv);

    slider.setValueRange(1, lockMaxTime, true);
    val = (lockMaxTime / 2);
    timeTv.setText(timeToString(val));
    slider.setValue(val, true);
    slider.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
      @Override
      public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
        val = newValue;
        timeTv.setText(timeToString(val));
      }
    });
  }
  private String timeToString(int seconds) {
    int day = (int) TimeUnit.SECONDS.toDays(seconds);
    long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
    long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
    long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
    return String.format("%02d:%02d:%02d", hours, minute, second);
  }
}
