package com.dots.focus.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dots.focus.R;
import com.dots.focus.adapter.OverviewCardViewAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.List;


public class OverviewFragment extends Fragment {

  private UltimateRecyclerView mRecyclerView;
  private Context mContext;
  private OverviewCardViewAdapter mOverviewCardViewAdapter;
  private GridLayoutManager layoutManager;
  final int GRID_COLUMN = 2;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    mContext = getActivity();
    View v = inflater.inflate(R.layout.fragment_overview, container, false);

    mRecyclerView = (UltimateRecyclerView) v.findViewById(R.id.overview_recycler_view);

    final List<String> stringList = new ArrayList<>();

    stringList.add(getResources().getString(R.string.title_my_radar_chart_overview));
    stringList.add(getResources().getString(R.string.title_weekly_app_usage_overview));
    stringList.add(getResources().getString(R.string.title_daily_app_usage_overview));
    stringList.add(getResources().getString(R.string.title_addiction_index_overview));
    stringList.add(getResources().getString(R.string.title_top_three_app_usage_overview));
    stringList.add(getResources().getString(R.string.title_app_leader_board_overview));
//    stringList.add(getResources().getString(R.string.title_ads_overview));
//    stringList.add(getResources().getString(R.string.title_my_daily_report_overview));
    stringList.add(getResources().getString(R.string.title_my_piggy_bank_overview));
    mOverviewCardViewAdapter = new OverviewCardViewAdapter(stringList);
    layoutManager = new GridLayoutManager(mContext, GRID_COLUMN);

    mRecyclerView.setLayoutManager(layoutManager);
    mRecyclerView.setAdapter(mOverviewCardViewAdapter);

    return v;
  }

}
