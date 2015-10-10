package com.dots.focus.ui;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dots.focus.R;
import com.dots.focus.controller.InboxController;


/**
 * Created by AdrianHsu on 2015/10/9.
 */
public class InboxActivity extends BaseActivity {

  static final String TAG = "InboxActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inbox);


    super.createToolbarFragment();

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    InboxController.convertAdapter(this);
    recyclerView.setAdapter(InboxController.mQuickAdapter);
    InboxController.initData();
  }
}
