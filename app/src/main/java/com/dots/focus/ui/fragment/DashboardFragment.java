package com.dots.focus.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dots.focus.R;

public class DashboardFragment extends Fragment {

  private Context mContext;
  private DashboardDonutFragment mSampleFitFragment;
  public static View topThreeCardView;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    mContext = getActivity();
    View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
    topThreeCardView = v.findViewById(R.id.top_three_card);

    if (savedInstanceState == null){
      mSampleFitFragment = new DashboardDonutFragment();

      //add child fragment
      getChildFragmentManager()
        .beginTransaction()
        .add(R.id.frameDashboardDonut, mSampleFitFragment, "tag")
        .commit();

    }

    return v;
  }
}