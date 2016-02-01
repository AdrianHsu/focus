package com.dots.focus.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dots.focus.R;
import com.dots.focus.adapter.WeeklyPiggyBankRecyclerViewAdapter;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeeklyPiggyBankFragment extends Fragment {

  private Context mContext;
  private UltimateRecyclerView mRecyclerView;
  private WeeklyPiggyBankRecyclerViewAdapter simpleRecyclerViewAdapter = null;
  private LinearLayoutManager linearLayoutManager;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    mContext = getActivity();
    View v = inflater.inflate(R.layout.fragment_piggy_bank_weekly, container, false);

    if (savedInstanceState == null) {

      mRecyclerView = (UltimateRecyclerView) v.findViewById(R.id.weekly_piggy_bank_recycler_view);

      final List<String> stringList = new ArrayList<>();
      long time = TrackAccessibilityUtil.getPrevXWeek(0);
      int[] timeBox = TrackAccessibilityUtil.timeBox(time);


      for(int i = 0; i < 7; i++)
        stringList.add("12/07 (星期日) -00:15:30");

      simpleRecyclerViewAdapter = new WeeklyPiggyBankRecyclerViewAdapter(stringList);
      linearLayoutManager = new LinearLayoutManager(mContext);

      mRecyclerView.setLayoutManager(linearLayoutManager);
      mRecyclerView.setAdapter(simpleRecyclerViewAdapter);

    }
    return v;
  }
}