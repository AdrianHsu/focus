package com.dots.focus.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dots.focus.R;
import com.dots.focus.adapter.TimePiggyBankTabPagerAdapter;
import com.parse.ParseUser;

/**
 * Created by AdrianHsu on 15/9/23.
 */
public class TimePiggyBankActivity extends OverviewChartActivity {

  private ViewPager mPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_time_piggy_bank);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    ParseUser user = ParseUser.getCurrentUser();
    String name = user.getString("user_name");
    getSupportActionBar().setTitle(name + "'s 時間存錢筒");

    TimePiggyBankTabPagerAdapter adapter = new TimePiggyBankTabPagerAdapter
                            (getSupportFragmentManager());
    mPager = (ViewPager)findViewById(R.id.viewpager);
    mPager.setAdapter(adapter);
    TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
    tabLayout.setupWithViewPager(mPager);
    setTabLayoutIcon(tabLayout);

  }
  private void setTabLayoutIcon(TabLayout tabLayout) {
    tabLayout.getTabAt(0).setText("本週");
    tabLayout.getTabAt(1).setText("累計");
    tabLayout.getTabAt(2).setText("全球");
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
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }
}