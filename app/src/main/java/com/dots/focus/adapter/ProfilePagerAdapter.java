package com.dots.focus.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dots.focus.ui.fragment.ProfilePostFragment;

/**
 * Created by AdrianHsu on 2015/10/10.
 */
public class ProfilePagerAdapter extends FragmentPagerAdapter {
  private final String[] TITLES = { "MY WALL", "ABOUT ME" };

  public ProfilePagerAdapter(FragmentManager fm) {
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
    return ProfilePostFragment.newInstance(position);
  }
}
