package com.dots.focus.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dots.focus.R;
import com.dots.focus.controller.TimelineController;

/**
 * Created by Adrian Hsu on 2015/10/10.
 */
public class TimelineActivity extends BaseActivity {

  static final String TAG = "TimelineActivity";

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        super.createToolbarFragment();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.timelineRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        TimelineController.convertAdapter(this);
        recyclerView.setAdapter(TimelineController.mQuickAdapter);
        TimelineController.initData();
    }

}
