package com.dots.focus.ui.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.dots.focus.R;
import com.dots.focus.adapter.DashboardPagerAdapter;

import github.chenupt.dragtoplayout.DragTopLayout;


/**
 * Created by AdrianHsu on 2015/10/9.
 */
public class DragTopLayoutFragment extends Fragment {

  private DragTopLayout dragLayout;
  private ViewPager viewPager;
  private PagerSlidingTabStrip pagerSlidingTabStrip;
  private DashboardPagerAdapter adapter;

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

    // init pager
    adapter = new DashboardPagerAdapter(getFragmentManager());
    viewPager.setAdapter(adapter);

    final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
      getResources()
      .getDisplayMetrics());
    viewPager.setPageMargin(pageMargin);

    pagerSlidingTabStrip.setViewPager(viewPager);
    Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(),
      "fonts/"+"AvenirNext_Regular.ttf");
    pagerSlidingTabStrip.setTypeface(myTypeface, 0);

    return view;
  }
}
