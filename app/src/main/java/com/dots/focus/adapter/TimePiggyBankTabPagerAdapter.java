package com.dots.focus.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dots.focus.ui.fragment.AddFriendFragment;
import com.dots.focus.ui.fragment.DashboardFragment;
import com.dots.focus.ui.fragment.GlobalPiggyBankFragment;
import com.dots.focus.ui.fragment.MessagesFragment;
import com.dots.focus.ui.fragment.MoreFragment;
import com.dots.focus.ui.fragment.OverviewFragment;
import com.dots.focus.ui.fragment.TotalPiggyBankFragment;
import com.dots.focus.ui.fragment.WeeklyPiggyBankFragment;

/**
 * Created by AdrianHsu on 2015/12/12.
 */
public class TimePiggyBankTabPagerAdapter extends FragmentStatePagerAdapter {

  private final int PAGE_COUNT = 3;
  public int ARG_PAGE = 0;

  public TimePiggyBankTabPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override
  public Fragment getItem(int position) {
    switch (position) {
      case 0:
        ARG_PAGE = 0;
        return new WeeklyPiggyBankFragment();
      case 1:
        ARG_PAGE = 1;
        return new TotalPiggyBankFragment();
      case 2:
        ARG_PAGE = 2;
        return new GlobalPiggyBankFragment();
    }
    return null;
  }

  @Override
  public int getCount() {
    return PAGE_COUNT;
  }
}