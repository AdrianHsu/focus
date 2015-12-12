package com.dots.focus.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dots.focus.R;
import com.dots.focus.adapter.SimpleAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.marshalchen.ultimaterecyclerview.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AdrianHsu on 2015/12/12.
 */
public class MoreFragment extends Fragment {

  private UltimateRecyclerView mRecyclerView;
  private Context context;
  SimpleAdapter simpleRecyclerViewAdapter = null;
  LinearLayoutManager linearLayoutManager;
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    context = getActivity();
    View v = inflater.inflate(R.layout.fragment_more, container, false);

    mRecyclerView = (UltimateRecyclerView) v.findViewById(R.id.more_recycler_view);

    final List<String> stringList = new ArrayList<>();

    stringList.add("目標設定");
    stringList.add("耍廢條件設定");
    stringList.add("鎖屏功能設定");
    stringList.add("通知設定");
    stringList.add("FOCUS發燒友");
    stringList.add("家長監護（PREMIUM）");
    stringList.add("進階設定");
    stringList.add("登出");
    simpleRecyclerViewAdapter = new SimpleAdapter(stringList);
    linearLayoutManager = new LinearLayoutManager(context);

    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(simpleRecyclerViewAdapter);
    StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration
      (simpleRecyclerViewAdapter);

    mRecyclerView.addItemDecoration(headersDecor);
    return v;
  }

}