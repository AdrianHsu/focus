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
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    MainTabPagerAdapter adapter = new MainTabPagerAdapter(getSupportFragmentManager());
    ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
    viewPager.setAdapter(adapter);
    TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
    tabLayout.setupWithViewPager(viewPager);

  }
}