package com.dots.focus.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.dots.focus.R;
import com.dots.focus.adapter.MainTabPagerAdapter;

/**
 * Created by AdrianHsu on 15/9/23.
 */
public class MainActivity extends BaseActivity {

  private ViewPager mPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    MainTabPagerAdapter adapter = new MainTabPagerAdapter(getSupportFragmentManager());
    mPager = (ViewPager)findViewById(R.id.viewpager);
    mPager.setAdapter(adapter);
    TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
    tabLayout.setupWithViewPager(mPager);
    setTabLayoutIcon(tabLayout);
  }
  private void setTabLayoutIcon(TabLayout tabLayout) {
    tabLayout.getTabAt(0).setIcon(R.drawable.tab_dashboard);
    tabLayout.getTabAt(1).setIcon(R.drawable.tab_add_friend);
    tabLayout.getTabAt(2).setIcon(R.drawable.tab_messages);
    tabLayout.getTabAt(3).setIcon(R.drawable.tab_overview);
    tabLayout.getTabAt(4).setIcon(R.drawable.tab_more);
  }

  @Override
  public void onBackPressed() {
    if (mPager.getCurrentItem() == 0) {
      // If the user is currently looking at the first step, allow the system to handle the
      // Back button. This calls finish() on this activity and pops the back stack.
      super.onBackPressed();
    } else {
      // Otherwise, select the dashboard step.
      mPager.setCurrentItem(0);
    }
  }
}