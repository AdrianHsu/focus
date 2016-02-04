package com.dots.focus.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dots.focus.R;
import com.dots.focus.adapter.AddFriendRecyclerViewAdapter;
import com.dots.focus.service.GetFriendConfirmService;
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
  private AddFriendRecyclerViewAdapter addFriendRecyclerViewAdapter = null;
  private LinearLayoutManager linearLayoutManager;
  private static String TAG = "AddFriendFragment";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    context = getActivity();
    context.startService(new Intent(context, GetFriendInviteService.class));
    context.startService(new Intent(context, GetFriendConfirmService.class));

    View v = inflater.inflate(R.layout.fragment_add_friend, container, false);

    mRecyclerView = (UltimateRecyclerView) v.findViewById(R.id.add_friend_recycler_view);

    FetchFriendUtil.refresh();
    GetFriendInviteService.refresh();
    GetFriendConfirmService.refresh();
//    FetchFriendUtil.waitFriendConfirm();

    final ArrayList<JSONObject> friendProfileList = new ArrayList<>();

    friendProfileList.addAll(FetchFriendUtil.mFriendList);
    friendProfileList.addAll(FetchFriendUtil.mInvitingFriendList);
//    friendProfileList.addAll(FetchFriendUtil.mConfirmedFriendList);
    friendProfileList.addAll(GetFriendInviteService.friendWaitingReplyList);
    friendProfileList.addAll(GetFriendConfirmService.friendRepliedList);


    addFriendRecyclerViewAdapter = new AddFriendRecyclerViewAdapter(friendProfileList, context);
    linearLayoutManager = new LinearLayoutManager(context);

    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(addFriendRecyclerViewAdapter);

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

            friendProfileList.addAll(FetchFriendUtil.mFriendList);
            Log.v(TAG, "mFriendList.size() == " + FetchFriendUtil.mFriendList.size());
//            friendProfileList.addAll(FetchFriendUtil.mConfirmedFriendList);
//            Log.v(TAG, "mConfirmedFriendList.size() == " + FetchFriendUtil.mConfirmedFriendList.size
//              ());
            friendProfileList.addAll(FetchFriendUtil.mInvitingFriendList);
            Log.v(TAG, "mInvitingFriendList.size() == " + FetchFriendUtil.mInvitingFriendList.size
                    ());

            friendProfileList.addAll(GetFriendInviteService.friendWaitingReplyList);
            Log.v(TAG, "friendWaitingReplyList.size() == " + GetFriendInviteService
              .friendWaitingReplyList.size());

            friendProfileList.addAll(GetFriendConfirmService.friendRepliedList);
            Log.v(TAG, "friendRepliedList.size() == " + GetFriendConfirmService.friendRepliedList
              .size());
            mRecyclerView.getAdapter().notifyDataSetChanged();

            mRecyclerView.setRefreshing(false);
          }
        }, 1000);
      }
    });
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
