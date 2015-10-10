package com.dots.focus.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dots.focus.ui.fragment.DashboardChartFragment;

/**
 * Created by AdrianHsu on 2015/10/10.
 */
public class DashboardPagerAdapter extends FragmentPagerAdapter {
  private final String[] TITLES = { "USAGE", "KICK" };

  public DashboardPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return TITLES[position];
  }

  @Override
  public int getCount() {
    return TITLES.length;
  }

  @Override
  public Fragment getItem(int position) {
    return DashboardChartFragment.newInstance(position);
  }
}
