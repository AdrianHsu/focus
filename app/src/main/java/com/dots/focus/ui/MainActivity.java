package com.dots.focus.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.dots.focus.R;
import com.dots.focus.adapter.MainTabPagerAdapter;
import com.dots.focus.receiver.HourReceiver;

import java.util.Calendar;

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

    setHourAlarm();
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

  public void setHourAlarm() {
    HourReceiver.setMain(this);

    Long time = 3600000 * (System.currentTimeMillis() / 3600000 + 1);

    Intent intent = new Intent(this, HourReceiver.class);
    // Intent intent = new Intent();
    intent.putExtra("msg", "an_hour_is_up");

    PendingIntent pi = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);

    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    am.set(AlarmManager.RTC_WAKEUP, time, pi);
  }
}