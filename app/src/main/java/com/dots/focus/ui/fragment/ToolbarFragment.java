package com.dots.focus.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dots.focus.R;


/**
 * Created by AdrianHsu on 2015/10/9.
 */
public class ToolbarFragment extends Fragment {

  private Toolbar toolbar;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_toolbar, parent, false);

    toolbar = (Toolbar) view.findViewById(R.id.tool_bar);
//    getActivity().setSupportActionBar(toolbar); //ActionBarActivity required
    return view;
  }
}
