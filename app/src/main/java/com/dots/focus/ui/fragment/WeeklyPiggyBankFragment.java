package com.dots.focus.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dots.focus.R;
import com.dots.focus.adapter.WeeklyPiggyBankRecyclerViewAdapter;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.github.mikephil.charting.data.Entry;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WeeklyPiggyBankFragment extends Fragment {

  private Context mContext;
  private UltimateRecyclerView mRecyclerView;
  private WeeklyPiggyBankRecyclerViewAdapter simpleRecyclerViewAdapter = null;
  private LinearLayoutManager linearLayoutManager;
  public static boolean []positiveColor;
  private TextView weekSwitchTv;
  private TextView totalTimeTv;
  private Button daySwitchLeftBtn;
  private Button daySwitchRightBtn;
  private int CURRENT_WEEK = 0;
  private List<String> stringList;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    mContext = getActivity();
    final View v = inflater.inflate(R.layout.fragment_piggy_bank_weekly, container, false);

    weekSwitchTv = (TextView) v.findViewById(R.id.day_switch_textview);
    String week = TrackAccessibilityUtil.weekPeriodString(0);
    weekSwitchTv.setText(week);


    daySwitchLeftBtn = (Button) v.findViewById(R.id.day_switch_left_btn);
    daySwitchLeftBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        CURRENT_WEEK++;
        weekSwitchTv = (TextView) v.findViewById(R.id.day_switch_textview);
        String week = TrackAccessibilityUtil.weekPeriodString(CURRENT_WEEK);
        weekSwitchTv.setText(week);
        stringList.clear();
        mRecyclerView.getAdapter().notifyDataSetChanged();
        stringList.addAll(setData());
        mRecyclerView.getAdapter().notifyDataSetChanged();

        daySwitchRightBtn.setEnabled(true);
//        daySwitchLeftBtn.setEnabled(false);
      }
    });
    daySwitchRightBtn = (Button) v.findViewById(R.id.day_switch_right_btn);
    daySwitchRightBtn.setEnabled(false);
    daySwitchRightBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        CURRENT_WEEK--;
        weekSwitchTv = (TextView) v.findViewById(R.id.day_switch_textview);
        String week = TrackAccessibilityUtil.weekPeriodString(CURRENT_WEEK);
        weekSwitchTv.setText(week);
        stringList.clear();
        mRecyclerView.getAdapter().notifyDataSetChanged();
        stringList.addAll(setData());
        mRecyclerView.getAdapter().notifyDataSetChanged();

        if (CURRENT_WEEK == 0)
          daySwitchRightBtn.setEnabled(false);
        daySwitchLeftBtn.setEnabled(true);
      }
    });

    mRecyclerView = (UltimateRecyclerView) v.findViewById(R.id.weekly_piggy_bank_recycler_view);
    totalTimeTv = (TextView) v.findViewById(R.id.total_weekly_time_textview);

    stringList = new ArrayList<String>();
    stringList.addAll(setData());

    simpleRecyclerViewAdapter = new WeeklyPiggyBankRecyclerViewAdapter(stringList);
    linearLayoutManager = new LinearLayoutManager(mContext);

    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(simpleRecyclerViewAdapter);


    return v;
  }
  private List<String> setData() {
    positiveColor = new boolean[7];
    long time = TrackAccessibilityUtil.getPrevXWeek(CURRENT_WEEK);
    int[] timeBox = TrackAccessibilityUtil.timeBox(time);
    String[] weekString = TrackAccessibilityUtil.weekString(time);
    List<String> mStringList = new ArrayList<>();
    int total = 0;

    for(int i = 0; i < 7; i++) {

      if(timeBox[i] == -1)
        mStringList.add(weekString[i] + " +" + "00:00:00");
      else {
        int t = (2 * (TrackAccessibilityUtil.anHour / 1000)) - timeBox[i];
        if(t >= 0) {
          mStringList.add(weekString[i] + " +" + timeToString(t));
          positiveColor[i] = true;
        }
        else {
          positiveColor[i] = false;
          mStringList.add(weekString[i] + " -" + timeToString(t * (-1)));
        }
        total += t;
      }
    }

    if(total >= 0) {
      totalTimeTv.setText("+" + timeToString(total));
      totalTimeTv.setTextColor(ContextCompat.getColor(mContext, R.color
                              .yellow));
    }
    else {
      totalTimeTv.setText("-" + timeToString(total * (-1)));
      totalTimeTv.setTextColor(ContextCompat.getColor(mContext, R.color
                              .red));
    }
    return mStringList;
  }


  private String timeToString(int seconds) {
    int day = (int) TimeUnit.SECONDS.toDays(seconds);
    long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
    long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
    long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
    return String.format("%02d:%02d:%02d", hours, minute, second);
  }
}