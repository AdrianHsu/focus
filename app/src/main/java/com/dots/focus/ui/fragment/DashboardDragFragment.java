package com.dots.focus.ui.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.astuetz.PagerSlidingTabStrip;
import com.dots.focus.R;
import com.dots.focus.adapter.DashboardPagerAdapter;
import com.dots.focus.model.DayBlock;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import github.chenupt.dragtoplayout.DragTopLayout;


public class DashboardDragFragment extends Fragment {

  private DragTopLayout dragLayout;
  private ViewPager viewPager;
  private PagerSlidingTabStrip pagerSlidingTabStrip;
  private DashboardPagerAdapter adapter;

  private Button test1_button;
  private Button test2_button;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                           Bundle savedInstanceState) {
    test1_button = (Button) parent.findViewById(R.id.test1_button);
    test2_button = (Button) parent.findViewById(R.id.test2_button);

    test1_button.setOnClickListener(new View.OnClickListener(){
      public void onClick(View v) {
        TrackAccessibilityUtil.getCurrentDay(System.currentTimeMillis()).saveEventually();
        TrackAccessibilityUtil.getCurrentHour(System.currentTimeMillis()).saveEventually();
      }

    });

    test2_button.setOnClickListener(new View.OnClickListener(){
      public void onClick(View v) {
        TrackAccessibilityUtil.getCurrentDay(System.currentTimeMillis()).saveEventually();
        TrackAccessibilityUtil.getCurrentHour(System.currentTimeMillis()).saveEventually();
      }

    });

    View view = inflater.inflate(R.layout.fragment_dashboard_drag_layout, parent, false);

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
