package com.dots.focus.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dots.focus.R;
import com.dots.focus.adapter.WeeklyPiggyBankRecyclerViewAdapter;
import com.dots.focus.util.TrackAccessibilityUtil;
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

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    mContext = getActivity();
    View v = inflater.inflate(R.layout.fragment_piggy_bank_weekly, container, false);

    mRecyclerView = (UltimateRecyclerView) v.findViewById(R.id.weekly_piggy_bank_recycler_view);

    final List<String> stringList = new ArrayList<>();
    long time = TrackAccessibilityUtil.getPrevXWeek(0);
    int[] timeBox = TrackAccessibilityUtil.timeBox(time);
    String[] weekString = TrackAccessibilityUtil.weekString(time);

    TextView total = (TextView) v.findViewById(R.id.total_weekly_time_textview);

    for(int i = 0; i < 7; i++) {

      if(timeBox[i] == -1)
        stringList.add(weekString[i] + " +" + "00:00:00");
      else {
        int t = (2 * (TrackAccessibilityUtil.anHour / 1000)) - timeBox[i];
        if(t >= 0)
          stringList.add(weekString[i] + " +" + timeToString(t));
        else
          stringList.add(weekString[i] + " -" + timeToString(t * (-1)));
      }
    }

    if(timeBox[7] >= 0)
      total.setText("+" + timeToString(timeBox[7]));
    else
      total.setText("-" + timeToString(timeBox[7] * (-1)));

    simpleRecyclerViewAdapter = new WeeklyPiggyBankRecyclerViewAdapter(stringList);
    linearLayoutManager = new LinearLayoutManager(mContext);

    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(simpleRecyclerViewAdapter);

    return v;
  }
  private String timeToString(int seconds) {
    int day = (int) TimeUnit.SECONDS.toDays(seconds);
    long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
    long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
    long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
    return String.format("%02d:%02d:%02d", hours, minute, second);
  }
}