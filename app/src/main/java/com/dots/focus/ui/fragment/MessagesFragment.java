package com.dots.focus.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dots.focus.R;
import com.dots.focus.adapter.MessagesRecyclerViewAdapter;
import com.dots.focus.config.LimitType;
import com.dots.focus.service.GetKickRequestService;
import com.dots.focus.service.GetKickResponseService;
import com.dots.focus.service.GetKickedService;
import com.dots.focus.util.KickUtil;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;


public class MessagesFragment extends Fragment {

  private UltimateRecyclerView mRecyclerView;
  private View mStickyView;
  private Context context;
  private MessagesRecyclerViewAdapter messagesRecyclerViewAdapter = null;
  private LinearLayoutManager linearLayoutManager;
  private static String TAG = "MessagesFragment";
  private ArrayList<JSONObject> messages;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    context = getActivity();
    View v = inflater.inflate(R.layout.fragment_messages, container, false);

    mRecyclerView = (UltimateRecyclerView) v.findViewById(R.id.messages_recycler_view);

    mStickyView = v.findViewById(R.id.messages_sticky_view);

    ImageView profileImageView = (ImageView) mStickyView.findViewById(R.id.profile_image);
    TextView profileTextView = (TextView) mStickyView.findViewById(R.id.profile_name);

    ParseUser user = ParseUser.getCurrentUser();
    String name = user.getString("user_name");
    profileTextView.setText(name);
    String url ="https://graph.facebook.com/" + String.valueOf(user.getLong
                            ("user_id") ) + "/picture?type=large";
    Picasso.with(context).load(url).into(profileImageView);

    GetKickRequestService.queryKickRequest();
    GetKickedService.queryKicked();
    GetKickResponseService.queryKickResponse();
    messages = new ArrayList<>();
    messages.addAll(GetKickRequestService.friendKickRequestList);
    messages.addAll(GetKickedService.kickedList);
    messages.addAll(GetKickResponseService.kickResponseList);
    Log.v(TAG, "friendKickRequestList.size() == " + GetKickRequestService.friendKickRequestList
      .size());
    messagesRecyclerViewAdapter = new MessagesRecyclerViewAdapter(messages, context);
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
            mRecyclerView.getAdapter().notifyDataSetChanged();
            Log.v(TAG, "(before)friendKickRequestList.size() == " + GetKickRequestService
                                    .friendKickRequestList
                                    .size());
            GetKickRequestService.queryKickRequest();
            GetKickedService.queryKicked();
            GetKickResponseService.queryKickResponse();
            Log.v(TAG, "(after)friendKickRequestList.size() == " + GetKickRequestService
                                    .friendKickRequestList
                                    .size());
            messages.addAll(GetKickRequestService.friendKickRequestList);
            messages.addAll(GetKickedService.kickedList);
            messages.addAll(GetKickResponseService.kickResponseList);

            mRecyclerView.getAdapter().notifyDataSetChanged();
            Log.v("Messages", "adapter size: " + mRecyclerView.getAdapter().getItemCount());
            mRecyclerView.setRefreshing(false);
          }
        }, 1000);
      }
    });

    return v;
  }
}
