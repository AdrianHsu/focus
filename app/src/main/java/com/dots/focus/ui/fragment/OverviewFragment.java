package com.dots.focus.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dots.focus.R;
import com.dots.focus.adapter.MoreRecyclerViewAdapter;
import com.dots.focus.adapter.OverviewCardViewAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by AdrianHsu on 2015/12/12.
 */
public class OverviewFragment extends Fragment {

  private UltimateRecyclerView mRecyclerView;
  private Context mContext;
  private OverviewCardViewAdapter mOverviewCardViewAdapter;
  private GridLayoutManager layoutManager;
  final int GRID_COLUMN = 2;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    mContext = getActivity();
    View v = inflater.inflate(R.layout.fragment_overview, container, false);

    mRecyclerView = (UltimateRecyclerView) v.findViewById(R.id.overview_recycler_view);

    final List<String> stringList = new ArrayList<>();

    stringList.add("每週總時數趨勢");
    stringList.add("每日使用時段趨勢");
    stringList.add("上癮程度趨勢");
    stringList.add("前三應用程式比較");
    stringList.add("應用程式排行榜");
    stringList.add("今日報表");
    stringList.add("雷達圖");
    stringList.add("我的存錢筒");
    mOverviewCardViewAdapter = new OverviewCardViewAdapter(stringList);
    layoutManager = new GridLayoutManager(mContext, GRID_COLUMN);

    mRecyclerView.setLayoutManager(layoutManager);
    mRecyclerView.setAdapter(mOverviewCardViewAdapter);

    return v;
  }

}
