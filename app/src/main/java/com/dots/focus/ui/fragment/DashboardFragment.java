package com.dots.focus.ui.fragment;

import android.content.Context;
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
public class DashboardFragment extends Fragment {

  private Context context;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    context = getActivity();
    TextView tv = new TextView(context);
    tv.setGravity(Gravity.CENTER);
    tv.setText("Text in Tab #" + 0);
    return tv;
  }

}
