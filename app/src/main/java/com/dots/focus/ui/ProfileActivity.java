package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dots.focus.R;
import com.dots.focus.adapter.OverviewCardViewAdapter;
import com.dots.focus.adapter.ProfileCardViewAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends BaseActivity {

  private UltimateRecyclerView mRecyclerView;
  private ProfileCardViewAdapter mProfileCardViewAdapter;
  private GridLayoutManager layoutManager;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  final int GRID_COLUMN = 2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_profile);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse);
    setSupportActionBar(toolbar);
//    ParseUser user = ParseUser.getCurrentUser();
//    String name = user.getString("name");
//    collapsingToolbarLayout.setTitle(name);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    collapsingToolbarLayout.setTitle("許秉鈞");

    mRecyclerView = (UltimateRecyclerView) findViewById(R.id.profile_recycler_view);

    final List<String> stringList = new ArrayList<>();

    stringList.add("男性");
    stringList.add("台北, 台灣");
    stringList.add("大學生");
    stringList.add("中文  (繁體)");
    stringList.add("1995");
    stringList.add("137");
    stringList.add("13.5");
    mProfileCardViewAdapter = new ProfileCardViewAdapter(stringList);
    layoutManager = new GridLayoutManager(this, GRID_COLUMN);

    mRecyclerView.setLayoutManager(layoutManager);
    mRecyclerView.setAdapter(mProfileCardViewAdapter);


  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }
}