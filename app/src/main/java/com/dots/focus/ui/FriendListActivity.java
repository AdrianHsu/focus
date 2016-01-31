package com.dots.focus.ui;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.dots.focus.R;
import com.dots.focus.adapter.AddFriendRecyclerViewAdapter;
import com.dots.focus.adapter.FriendRecyclerViewAdapter;
import com.dots.focus.service.GetFriendConfirmService;
import com.dots.focus.service.GetFriendInviteService;
import com.dots.focus.util.FetchFriendUtil;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by AdrianHsu on 15/9/23.
 */
public class FriendListActivity extends BaseActivity {

  private Toolbar toolbar;
  private UltimateRecyclerView mRecyclerView;
  private FriendRecyclerViewAdapter friendRecyclerViewAdapter = null;
  private LinearLayoutManager linearLayoutManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_friend_list);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("好友清單");

    mRecyclerView = (UltimateRecyclerView) findViewById(R.id.friend_recycler_view);
    FetchFriendUtil.refresh();
    final ArrayList<JSONObject> friendProfileList = new ArrayList<>();
    friendProfileList.addAll(FetchFriendUtil.mConfirmedFriendList);

    friendRecyclerViewAdapter = new FriendRecyclerViewAdapter(friendProfileList, this);
    linearLayoutManager = new LinearLayoutManager(this);

    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(friendRecyclerViewAdapter);

    mRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {

            FetchFriendUtil.refresh();
            GetFriendInviteService.refresh();
            GetFriendConfirmService.refresh();

            friendProfileList.clear();
            mRecyclerView.getAdapter().notifyDataSetChanged();

            friendProfileList.addAll(FetchFriendUtil.mConfirmedFriendList);
            mRecyclerView.getAdapter().notifyDataSetChanged();
            mRecyclerView.setRefreshing(false);
          }
        }, 1000);
      }
    });
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }

}
