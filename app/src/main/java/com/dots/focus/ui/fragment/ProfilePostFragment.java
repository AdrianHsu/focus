package com.dots.focus.ui.fragment;

/**
 * Created by AdrianHsu on 2015/10/9.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.dots.focus.controller.ProfileController;


public class ProfilePostFragment extends Fragment {

  private static final String ARG_POSITION = "position";

  private int position;

  public static ProfilePostFragment newInstance(int position) {
    ProfilePostFragment f = new ProfilePostFragment();
    Bundle b = new Bundle();
    b.putInt(ARG_POSITION, position);
    f.setArguments(b);
    return f;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    position = getArguments().getInt(ARG_POSITION);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    FrameLayout fl = new FrameLayout(getActivity());
    fl.setLayoutParams(params);

    final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
      getResources()
      .getDisplayMetrics());

    if(position == 0) {

      // use java code to dynamically create recyclerview fragment_profile_post
//    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.timeline_recyclerview);
      RecyclerView v = new RecyclerView(getActivity());

      params.setMargins(margin, margin, margin, margin);
      v.setLayoutParams(params);
      v.setLayoutParams(params);
      v.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

      ProfileController.convertAdapter(getActivity());
      v.setAdapter(ProfileController.mQuickAdapter);
      ProfileController.initData();

      // this method solved the dragLayout scrolling issue
      // attach top listener
      v.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
          if (recyclerView.getChildCount() > 0) {
            if (recyclerView.getChildAt(0).getTop() >= 0) {
              ProfileDragFragment.dragLayout.setTouchMode(true);
            } else {
              ProfileDragFragment.dragLayout.setTouchMode(false);
            }
          }else{
            ProfileDragFragment.dragLayout.setTouchMode(true);
          }
        }
      });
      fl.addView(v);
    }
    else if (position == 1){
      TextView v = new TextView(getActivity());
      params.setMargins(margin, margin, margin, margin);
      v.setLayoutParams(params);
      v.setLayoutParams(params);
      v.setGravity(Gravity.CENTER);
      v.setText("CARD " + (position + 1));
      fl.addView(v);
    }

    return fl;
  }

}