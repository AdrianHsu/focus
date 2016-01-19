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
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TotalPiggyBankFragment extends Fragment {

  Context mContext;
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    mContext = getActivity();
    View v = inflater.inflate(R.layout.fragment_piggy_bank_total, container, false);

    if (savedInstanceState == null) {
    }
    return v;
  }
}