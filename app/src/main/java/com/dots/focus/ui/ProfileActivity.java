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
import com.dots.focus.adapter.ProfileRecyclerViewAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends BaseActivity {

  private UltimateRecyclerView mRecyclerView;
  private ProfileRecyclerViewAdapter mProfileRecyclerViewAdapter;
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
    ParseUser user = ParseUser.getCurrentUser();
    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
    String name = user.getString("user_name");
    collapsingToolbarLayout.setTitle(name);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    mRecyclerView = (UltimateRecyclerView) findViewById(R.id.profile_recycler_view);

    final List<String> stringList = new ArrayList<>();


    String gender = "Male";
    if(user.getBoolean("Gender") == false)
      gender = "Female";
    String location = installation.getString("timeZone");
    String occupation = user.getString("Occupation");
    String localeIdentifier = installation.getString("localeIdentifier");
    String birth = String.valueOf(user.getInt("Birth"));
    String numOfFriend = "137";
    String totalTimeSaved = "13.5";

    stringList.add(gender);
    stringList.add(location);
    stringList.add(occupation);
    stringList.add(localeIdentifier);
    stringList.add(birth);
    stringList.add(numOfFriend);
    stringList.add(totalTimeSaved);

    mProfileRecyclerViewAdapter = new ProfileRecyclerViewAdapter(stringList, this);
    layoutManager = new GridLayoutManager(this, GRID_COLUMN);

    mRecyclerView.setLayoutManager(layoutManager);
    mRecyclerView.setAdapter(mProfileRecyclerViewAdapter);


  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }
}