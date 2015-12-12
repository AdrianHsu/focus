package com.dots.focus.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by AdrianHsu on 2015/12/12.
 */
public class MainTabFragment extends Fragment {
  public static final String ARG_PAGE = "ARG_PAGE";

  public MainTabFragment() {}

  public static MainTabFragment newInstance(int argPage) {
    MainTabFragment fragment = new MainTabFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_PAGE, argPage);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Bundle args = getArguments();
    int tabPosition = args.getInt(ARG_PAGE);
    TextView tv = new TextView(getActivity());
    tv.setGravity(Gravity.CENTER);
    tv.setText("Text in Tab #" + tabPosition);
    return tv;
  }
}
