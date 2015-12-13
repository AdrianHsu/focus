package com.dots.focus.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dots.focus.R;

/**
 * Created by AdrianHsu on 2015/12/12.
 */
public class DashboardFragment extends Fragment {

  private Context mContext;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    mContext = getActivity();
    View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

    return v;
  }

}
