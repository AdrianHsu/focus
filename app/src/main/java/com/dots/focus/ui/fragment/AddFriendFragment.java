package com.dots.focus.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dots.focus.R;
import com.dots.focus.adapter.AddFriendRecyclerViewAdapter;
import com.dots.focus.service.GetFriendInviteService;
import com.dots.focus.util.FetchFriendUtil;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.List;


public class AddFriendFragment extends Fragment {

  private UltimateRecyclerView mRecyclerView;
  private Context context;
  private AddFriendRecyclerViewAdapter simpleRecyclerViewAdapter = null;
  private LinearLayoutManager linearLayoutManager;
  private static String TAG = "AddFriendFragment";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    context = getActivity();
    View v = inflater.inflate(R.layout.fragment_add_friend, container, false);

    mRecyclerView = (UltimateRecyclerView) v.findViewById(R.id.add_friend_recycler_view);



    final List<String> stringList = new ArrayList<>();

    
    stringList.add("某某id");

    simpleRecyclerViewAdapter = new AddFriendRecyclerViewAdapter(stringList);
    linearLayoutManager = new LinearLayoutManager(context);

    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(simpleRecyclerViewAdapter);
    return v;
  }

  @Override
  public void onStart() {
    super.onStart();
    Log.d(TAG, "onStart");
    FetchFriendUtil.getFriendsInfo();

    Intent intent = new Intent(context, GetFriendInviteService.class);
    context.startService(intent);
  }

  @Override
  public void onStop() {
    Log.d(TAG, "onStop");
    Intent intent = new Intent(context, GetFriendInviteService.class);
    context.stopService(intent);

    super.onStop();
  }

}
