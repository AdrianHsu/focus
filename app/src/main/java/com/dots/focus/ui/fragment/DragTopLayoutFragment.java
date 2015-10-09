package com.dots.focus.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.dots.focus.R;

import github.chenupt.dragtoplayout.DragTopLayout;

/**
 * Created by AdrianHsu on 2015/10/9.
 */
public class DragTopLayoutFragment extends Fragment {

  private DragTopLayout dragLayout;
  private ViewPager viewPager;
  private PagerSlidingTabStrip pagerSlidingTabStrip;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_drag_top_layout, parent, false);

    dragLayout = (DragTopLayout) view.findViewById(R.id.drag_layout);
    viewPager = (ViewPager) view.findViewById(R.id.view_pager);
    pagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);

//    setSupportActionBar(toolbar);
//    // init pager
//    PagerModelManager factory = new PagerModelManager();
//    factory.addCommonFragment(getFragments(), getTitles());
//    adapter = new ModelPagerAdapter(getSupportFragmentManager(), factory);
//    viewPager.setAdapter(adapter);
//    pagerSlidingTabStrip.setViewPager(viewPager);
    return view;
  }
}
