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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dots.focus.R;
import com.dots.focus.adapter.MessagesRecyclerViewAdapter;
import com.dots.focus.service.GetFriendInviteService;
import com.dots.focus.service.GetKickRequestService;
import com.dots.focus.service.GetKickResponseService;
import com.dots.focus.service.GetKickedService;
import com.dots.focus.ui.CannedMessagesSettingsActivity;
import com.dots.focus.ui.ProfileActivity;
import com.dots.focus.util.SettingsUtil;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;


public class MessagesFragment extends Fragment {

  private UltimateRecyclerView mRecyclerView;
  private View mStickyView;
  private Context context;
  private Button resetBtn;
  public static TextView myKickRequestTv;

  public MessagesRecyclerViewAdapter messagesRecyclerViewAdapter = null;
  private LinearLayoutManager linearLayoutManager;
  private static String TAG = "MessagesFragment";
  private ArrayList<JSONObject> messages;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    context = getActivity();
//    context.startService(new Intent(context, GetKickRequestService.class));
//    context.startService(new Intent(context, GetKickedService.class));
//    context.startService(new Intent(context, GetKickResponseService.class));

    View v = inflater.inflate(R.layout.fragment_messages, container, false);

    mRecyclerView = (UltimateRecyclerView) v.findViewById(R.id.messages_recycler_view);
    mStickyView = v.findViewById(R.id.messages_sticky_view);

    ImageView profileImageView = (ImageView) mStickyView.findViewById(R.id.profile_image);
    TextView profileTextView = (TextView) mStickyView.findViewById(R.id.profile_name);

    myKickRequestTv = (TextView) mStickyView.findViewById(R.id.my_kick_request);
    String myKickRequest = SettingsUtil.getString("kickRequest");
    myKickRequestTv.setText(myKickRequest);

    resetBtn = (Button) mStickyView.findViewById(R.id.resetBtn);
    resetBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(context, CannedMessagesSettingsActivity.class);
        context.startActivity(intent);
      }
    });

    ParseUser user = ParseUser.getCurrentUser();
    String name = user.getString("user_name");
    profileTextView.setText(name);
    String url ="https://graph.facebook.com/" + String.valueOf(user.getLong
                            ("user_id") ) + "/picture?type=large";
    Picasso.with(context).load(url).into(profileImageView);

    GetKickRequestService.checkLocal();
    GetKickedService.checkLocal();
    GetKickResponseService.checkLocal();

    GetKickRequestService.queryKickRequest();
    GetKickedService.queryKicked();
    GetKickResponseService.queryKickResponse();
    messages = new ArrayList<>();
    messages.addAll(GetKickRequestService.friendKickRequestList);
    messages.addAll(GetKickedService.kickedList);
    messages.addAll(GetKickedService.respondList);
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

            GetKickRequestService.checkLocal();
            GetKickedService.checkLocal();
            GetKickResponseService.checkLocal();

            GetKickRequestService.queryKickRequest();
            GetKickedService.queryKicked();
            GetKickResponseService.queryKickResponse();

            messages.addAll(GetKickRequestService.friendKickRequestList);
            messages.addAll(GetKickedService.kickedList);
            messages.addAll(GetKickedService.respondList);
            Log.v(TAG, "respondList size: " + GetKickedService.respondList.size());
            messages.addAll(GetKickResponseService.kickResponseList);

            mRecyclerView.getAdapter().notifyDataSetChanged();
            Log.v(TAG, "adapter size: " + mRecyclerView.getAdapter().getItemCount());
            mRecyclerView.setRefreshing(false);
          }
        }, 1000);
      }
    });

    return v;
  }

}
