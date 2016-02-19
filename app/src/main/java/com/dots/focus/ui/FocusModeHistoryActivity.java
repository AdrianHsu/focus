package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.dots.focus.R;
import com.dots.focus.adapter.CommunityPostCardViewAdapter;
import com.dots.focus.adapter.FocusModeHistoryAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FocusModeHistoryActivity extends BaseActivity {

  private UltimateRecyclerView mRecyclerView;
  private FocusModeHistoryAdapter focusModeHistoryAdapter;
  private LinearLayoutManager linearLayoutManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_focus_mode_history);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(getResources().getString(R.string.title_focus_mode));

      mRecyclerView = (UltimateRecyclerView) findViewById(R.id.focus_mode_history_recycler_view);
      final List<JSONObject> jsonObjectList = new ArrayList<>();

      for(int i = 0; i < 100; i++) {
        JSONObject jsonObject = new JSONObject();
        try {
          jsonObject.put("content", "讀完微積分第二章" + i);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        jsonObjectList.add(jsonObject);
      }
      focusModeHistoryAdapter = new FocusModeHistoryAdapter(jsonObjectList, this);
      linearLayoutManager = new LinearLayoutManager(this);

      mRecyclerView.setLayoutManager(linearLayoutManager);
      mRecyclerView.setAdapter(focusModeHistoryAdapter);

  }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {

        case android.R.id.home:
          onBackPressed();
          break;
        case R.id.action_focus_mode:
          Intent intent = new Intent(this, FocusModeActivity.class);
          startActivity(intent);
          break;
      }
      return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.focus_mode, menu);
      return true;
    }

}