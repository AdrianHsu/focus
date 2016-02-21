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
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TotalPiggyBankFragment extends Fragment {

  private Context mContext;
  private TextView totalTv;
  private TextView prTv;
  private TextView totalUserTv;
  private TextView rankTv;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    mContext = getActivity();
    View v = inflater.inflate(R.layout.fragment_piggy_bank_total, container, false);

    totalTv = (TextView) v.findViewById(R.id.total_saved_textview);
    prTv = (TextView) v.findViewById(R.id.pr);
    totalUserTv = (TextView) v.findViewById(R.id.num_total_user);
    rankTv = (TextView) v.findViewById(R.id.rank);

//    long time = TrackAccessibilityUtil.getPrevXWeek(0); // 0 dont care for total
//    int[] timeBox = TrackAccessibilityUtil.timeBox(time);
//    int totalTime = timeBox[7];
    long[] data = TrackAccessibilityUtil.getSavedTimeAndRank();

    long totalTime = data[0];
    if(totalTime >= 0)
      totalTv.setText("+" + timeToString(totalTime));
    else
      totalTv.setText("-" + timeToString(totalTime * (-1)));

    prTv.setText(data[1] + "%");
    totalUserTv.setText(String.valueOf(data[4]));
    rankTv.setText(String.valueOf(data[2]));

    return v;
  }
  private String timeToString(long seconds) {
    int day = (int) TimeUnit.SECONDS.toDays(seconds);
    long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
    long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
    long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
    return String.format("%02d:%02d:%02d", hours, minute, second);
  }
}