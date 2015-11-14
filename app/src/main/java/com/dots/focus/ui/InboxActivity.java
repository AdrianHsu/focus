package com.dots.focus.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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


    super.createToolbarFragment(savedInstanceState);

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.inbox_recyclerview);
    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    InboxController.convertAdapter(this);
    recyclerView.setAdapter(InboxController.mQuickAdapter);
    InboxController.initData();
  }
  public void onExtendClick(View v) {
    Intent intent = new Intent(this, InboxEditActivity.class);
    this.startActivity(intent);
  }

}
