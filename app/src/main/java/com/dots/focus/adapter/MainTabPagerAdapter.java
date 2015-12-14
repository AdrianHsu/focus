package com.dots.focus.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dots.focus.ui.fragment.DashboardFragment;
import com.dots.focus.ui.fragment.MoreFragment;
import com.dots.focus.ui.fragment.OverviewFragment;

/**
 * Created by AdrianHsu on 2015/12/12.
 */
public class MainTabPagerAdapter extends FragmentStatePagerAdapter {

  private final int PAGE_COUNT = 5;
  public int ARG_PAGE = 0;

  public MainTabPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override
  public Fragment getItem(int position) {
    switch (position) {
      case 0:
        ARG_PAGE = 0;
        return new DashboardFragment();
      case 1:
        ARG_PAGE = 1;
        return new MoreFragment();
      case 2:
        ARG_PAGE = 2;
        return new MoreFragment();
      case 3:
        ARG_PAGE = 3;
        return new OverviewFragment();
      case 4:
        ARG_PAGE = 4;
        return new MoreFragment();
    }
    return null;
  }

  @Override
  public int getCount() {
    return PAGE_COUNT;
  }
}