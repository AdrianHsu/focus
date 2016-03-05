package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dots.focus.R;
import com.dots.focus.adapter.AppLeaderBoardRecyclerViewAdapter;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.github.mikephil.charting.data.Entry;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppLeaderBoardChartActivity extends OverviewChartActivity {

    private Context mContext;
    private UltimateRecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AppLeaderBoardRecyclerViewAdapter appLeaderBoardRecyclerViewAdapter;
    private TextView weekSwitchTv;
    private Button daySwitchLeftBtn;
    private Button daySwitchRightBtn;
    private int CURRENT_WEEK = 0;
    private long time;
    private ArrayList<Entry> mIndexList;

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_leader_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_app_leader_board_overview));

        weekSwitchTv = (TextView) findViewById(R.id.day_switch_textview);
        String week = TrackAccessibilityUtil.weekPeriodString(0);
        weekSwitchTv.setText(week);

    daySwitchLeftBtn = (Button) findViewById(R.id.day_switch_left_btn);
    daySwitchLeftBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        CURRENT_WEEK++;
        weekSwitchTv = (TextView) findViewById(R.id.day_switch_textview);
        String week = TrackAccessibilityUtil.weekPeriodString(CURRENT_WEEK);
        weekSwitchTv.setText(week);
        time = TrackAccessibilityUtil.getPrevXWeek(CURRENT_WEEK);
        mIndexList.clear();
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mIndexList.addAll(getIndex(time));
        mRecyclerView.getAdapter().notifyDataSetChanged();
        daySwitchRightBtn.setEnabled(true);
//        daySwitchLeftBtn.setEnabled(false);
      }
    });
    daySwitchRightBtn = (Button) findViewById(R.id.day_switch_right_btn);
    daySwitchRightBtn.setEnabled(false);
    daySwitchRightBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        CURRENT_WEEK--;
        weekSwitchTv = (TextView) findViewById(R.id.day_switch_textview);
        String week = TrackAccessibilityUtil.weekPeriodString(CURRENT_WEEK);
        weekSwitchTv.setText(week);
        time = TrackAccessibilityUtil.getPrevXWeek(CURRENT_WEEK);
        mIndexList.clear();
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mIndexList.addAll(getIndex(time));
        mRecyclerView.getAdapter().notifyDataSetChanged();

        if (CURRENT_WEEK == 0)
          daySwitchRightBtn.setEnabled(false);
        daySwitchLeftBtn.setEnabled(true);
      }
    });

        time = TrackAccessibilityUtil.getPrevXWeek(0);
        mIndexList = new ArrayList<>();
        mIndexList.addAll(getIndex(time));

        // indexList can be used
        mRecyclerView = (UltimateRecyclerView) findViewById(R.id.hour_app_usage_recycler_view);
        appLeaderBoardRecyclerViewAdapter = new AppLeaderBoardRecyclerViewAdapter(mIndexList, this);
        linearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(appLeaderBoardRecyclerViewAdapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
    private ArrayList<Entry> getIndex(long time) {
      List<List<Integer>> appLengths = TrackAccessibilityUtil.weekAppUsage(time, this);
      List<Integer> appLength = appLengths.get(7);

      ArrayList<Entry> indexList = new ArrayList<>(appLength.size());
      for (int i = 0, size = appLength.size(); i < size; ++i)
        indexList.add(new Entry(appLength.get(i), i));

      Collections.sort(indexList, new Comparator<Entry>() {
        @Override
        public int compare(Entry e1, Entry e2) {
          return (int)(e2.getVal() - e1.getVal());
        }
      });
      return indexList;
    }
}