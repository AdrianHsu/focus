package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

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

        Calendar calendar = Calendar.getInstance();
        long time = System.currentTimeMillis() + TrackAccessibilityUtil.getTimeOffset() *
                TrackAccessibilityUtil.anHour,
                oneDay = 86400000;
        time = oneDay * (time / oneDay);
        calendar.setTimeInMillis(time);
        Log.d("TrackAccessibilityUtil", "calendar.get(Calendar.DAY_OF_WEEK): " + calendar.get
                (Calendar.DAY_OF_WEEK));
        calendar.setTimeInMillis(time - TrackAccessibilityUtil.getTimeOffset() * TrackAccessibilityUtil.anHour);
        //- 7 * oneDay * week - (calendar.get(Calendar.DAY_OF_WEEK)  - 1) * oneDay

        List<List<Integer>> appLengths = TrackAccessibilityUtil.weekAppUsage(calendar
                .getTimeInMillis());
        List<Integer> appLength = appLengths.get(7);

        ArrayList<Entry> indexList = new ArrayList<>(appLength.size());
        for (int i = 0, size = appLength.size(); i < size; ++i)
            indexList.add(new Entry(appLength.get(i), i));


        for (int i = 0, size = indexList.size(); i < size; ++i) {
            for (int j = i; j != 0; --j) {
                if (indexList.get(j).getVal() > indexList.get(j - 1).getVal()) {
                    Entry e1 = indexList.get(j),
                          e2 = indexList.get(j - 1);
                    indexList.set(j, e2);
                    indexList.set(j - 1, e1);
                }
                else    break;
            }
        }
      /*
        Collections.sort(indexList, new Comparator<Entry>() {
                    @Override
                    public int compare(Entry e1, Entry e2) {
                        if (e1.getVal() > e2.getVal())  return 1;
                        return 0;
                    }
                });
      */
        // indexList can be used

        mRecyclerView = (UltimateRecyclerView) findViewById(R.id.hour_app_usage_recycler_view);
        appLeaderBoardRecyclerViewAdapter = new AppLeaderBoardRecyclerViewAdapter(indexList, this);
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