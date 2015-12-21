package com.dots.focus.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dots.focus.R;
import com.dots.focus.adapter.MoreRecyclerViewAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MoreFragment extends Fragment {

  private UltimateRecyclerView mRecyclerView;
  private View mStickyView;
  private Context context;
  private MoreRecyclerViewAdapter simpleRecyclerViewAdapter = null;
  private LinearLayoutManager linearLayoutManager;
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    context = getActivity();
    View v = inflater.inflate(R.layout.fragment_more, container, false);

    mStickyView = v.findViewById(R.id.sticky_view);
    mStickyView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(view.getContext(), "sticky view clicked"
          , Toast
          .LENGTH_SHORT)
          .show();
      }
    });

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
    simpleRecyclerViewAdapter = new MoreRecyclerViewAdapter(stringList);
    linearLayoutManager = new LinearLayoutManager(context);

    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(simpleRecyclerViewAdapter);
    return v;
  }

}
