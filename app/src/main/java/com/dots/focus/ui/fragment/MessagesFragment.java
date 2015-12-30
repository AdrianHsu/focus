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
import com.dots.focus.adapter.MessagesRecyclerViewAdapter;
import com.dots.focus.service.GetCurrentAppsService;
import com.dots.focus.service.GetFriendConfirmService;
import com.dots.focus.service.GetFriendInviteService;
import com.dots.focus.util.FetchFriendUtil;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;


public class MessagesFragment extends Fragment {

  private UltimateRecyclerView mRecyclerView;
  private Context context;
  private MessagesRecyclerViewAdapter messagesRecyclerViewAdapter = null;
  private LinearLayoutManager linearLayoutManager;
  private static String TAG = "MessagesFragment";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    context = getActivity();
    View v = inflater.inflate(R.layout.fragment_messages, container, false);

    mRecyclerView = (UltimateRecyclerView) v.findViewById(R.id.messages_recycler_view);

    final ArrayList<JSONObject> messages = new ArrayList<>();

    GetCurrentAppsService.checkCurrentApps();
    messages.addAll(GetCurrentAppsService.friendCurrentAppList);

    messagesRecyclerViewAdapter = new MessagesRecyclerViewAdapter( messages, context);
    linearLayoutManager = new LinearLayoutManager(context);

    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(messagesRecyclerViewAdapter);

    mRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {

            messages.clear();
            GetCurrentAppsService.checkCurrentApps();
            messages.addAll(GetCurrentAppsService.friendCurrentAppList);
            mRecyclerView.setRefreshing(false);
          }
        }, 1000);
      }
    });

    return v;
  }


}
