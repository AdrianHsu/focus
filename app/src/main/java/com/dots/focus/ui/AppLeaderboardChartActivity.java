package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dots.focus.R;
import com.dots.focus.adapter.AppLeaderBoardRecyclerViewAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AppLeaderBoardChartActivity extends OverviewChartActivity {

  private UltimateRecyclerView mRecyclerView;
  private LinearLayoutManager linearLayoutManager;
  private AppLeaderBoardRecyclerViewAdapter appLeaderBoardRecyclerViewAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_app_leader_board);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("常用應用程式排行榜");

    final ArrayList<JSONObject> appUsageList = new ArrayList<>();

    for (int i = 0; i < 100; i++) {
      JSONObject appUsage = new JSONObject();
      try {
        appUsage.put("appName", "Facebook");
        appUsage.put("duration", 600); // 600sec
//      app.put(icon); // put icon
      } catch (JSONException e) {
        e.printStackTrace();
      }

      appUsageList.add(appUsage);
    }
    mRecyclerView = (UltimateRecyclerView) findViewById(R.id.hour_app_usage_recycler_view);
    appLeaderBoardRecyclerViewAdapter = new AppLeaderBoardRecyclerViewAdapter(appUsageList, this);
    linearLayoutManager = new LinearLayoutManager(this);

    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(appLeaderBoardRecyclerViewAdapter);

  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }
}