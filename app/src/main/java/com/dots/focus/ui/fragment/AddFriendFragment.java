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

import org.json.JSONException;
import org.json.JSONObject;

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

    FetchFriendUtil.getFriendsInfo(context);
    ArrayList<JSONObject> friendProfileList = FetchFriendUtil.mFriendList;
    simpleRecyclerViewAdapter = new AddFriendRecyclerViewAdapter(friendProfileList, context);
    linearLayoutManager = new LinearLayoutManager(context);

    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(simpleRecyclerViewAdapter);
    return v;
  }

  @Override
  public void onDestroy() {

    Log.d(TAG, "called onDestroy()");
    Intent intent = new Intent(context, GetFriendInviteService.class);
    context.stopService(intent);
    super.onDestroy();

  }

  @Override
  public void onStart() {
    Log.d(TAG, "onStart");

    Intent intent = new Intent(context, GetFriendInviteService.class);
    context.startService(intent);

    super.onStart();
  }

  @Override
  public void onStop() {
    Log.d(TAG, "onStop");

    super.onStop();
  }

}
