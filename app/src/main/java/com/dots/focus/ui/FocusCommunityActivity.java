package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.dots.focus.R;
import com.dots.focus.adapter.CommunityPostCardViewAdapter;
import com.dots.focus.adapter.ProfileCardViewAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FocusCommunityActivity extends BaseActivity {

  private UltimateRecyclerView mRecyclerView;
  private CommunityPostCardViewAdapter mCommunityPostCardViewAdapter;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  private LinearLayoutManager linearLayoutManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_focus_community);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse);
    setSupportActionBar(toolbar);
//    ParseUser user = ParseUser.getCurrentUser();
//    String name = user.getString("name");
//    collapsingToolbarLayout.setTitle(name);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    collapsingToolbarLayout.setTitle("FOCUS 發燒友");
    collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
    collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

    mRecyclerView = (UltimateRecyclerView) findViewById(R.id.focus_community_recycler_view);

    final List<String> stringList = new ArrayList<>();

    for(int i = 0; i < 100; i++)
      stringList.add("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " + i);

    mCommunityPostCardViewAdapter = new CommunityPostCardViewAdapter(stringList, this);
    linearLayoutManager = new LinearLayoutManager(this);

    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(mCommunityPostCardViewAdapter);

  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        break;
      case R.id.sortByTime:
        break;
      case R.id.sortByPopularity:
        break;
    }
    return true;
  }
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.focus_community_filter, menu);
    return true;
  }

}