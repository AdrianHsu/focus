package com.dots.focus.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dots.focus.ui.fragment.MainTabFragment;

/**
 * Created by AdrianHsu on 2015/12/12.
 */
public class MainTabPagerAdapter extends FragmentStatePagerAdapter {

  public MainTabPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override
  public Fragment getItem(int position) {
    return MainTabFragment.newInstance(position);
  }

  @Override
  public int getCount() {
    return 5;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return "Tab " + position;
  }

}