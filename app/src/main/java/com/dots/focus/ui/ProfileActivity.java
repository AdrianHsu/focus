package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.dots.focus.R;
import com.dots.focus.adapter.ProfileRecyclerViewAdapter;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends BaseActivity {

  private UltimateRecyclerView mRecyclerView;
  private ProfileRecyclerViewAdapter mProfileRecyclerViewAdapter;
  private GridLayoutManager layoutManager;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  public static boolean gender;
  public static String occupation;
  public static int yearOfBirth;

  private static final String TAG = "Profile";

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
//    getCoverPhoto();

    String gender = "男性";
    if(user.getBoolean("Gender") == false)
      gender = "女性";
    String location = installation.getString("timeZone");
    occupation = user.getString("Occupation");
    String localeIdentifier = installation.getString("localeIdentifier");
    yearOfBirth = user.getInt("Birth");


    JSONArray jsonArray = user.getJSONArray("Friends");
    int num = jsonArray.length();

    String numOfFriend = String.valueOf(num);
    long[] data = TrackAccessibilityUtil.getSavedTimeAndRank();
    long total = data[0];
    String totalTimeSaved = String.valueOf(total);

    stringList.add(gender);
    stringList.add(location);
    stringList.add(occupation);
    stringList.add(localeIdentifier);
    stringList.add(String.valueOf(yearOfBirth));
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
  @Override
  public void onBackPressed(){

    ParseUser user = ParseUser.getCurrentUser();
    user.put("Gender", gender);
    user.put("Occupation", occupation);
    user.put("Birth", yearOfBirth);
    user.saveEventually();
    super.onBackPressed();
  }
}