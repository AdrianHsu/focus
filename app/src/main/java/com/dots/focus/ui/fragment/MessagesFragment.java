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
import com.dots.focus.config.KickState;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;


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

  private static final int KICK_REQUEST_ITEM = KickState.REQUEST_DOWNLOADED.getValue();
  private static final int KICK_HISTORY_ITEM = KickState.KICK_DOWNLOADED.getValue();
  private static final int KICK_RESPONSE_ITEM = KickState.RESPONSE_DOWNLOADED.getValue();

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
    sort();
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
            sort();

            mRecyclerView.getAdapter().notifyDataSetChanged();
            Log.v(TAG, "adapter size: " + mRecyclerView.getAdapter().getItemCount());
            mRecyclerView.setRefreshing(false);
          }
        }, 1000);
      }
    });

    return v;
  }
  private static class ObjectComparator implements Comparator<JSONObject> {
    @Override
    public int compare(JSONObject object1, JSONObject object2) {
      long time1 = 0, time2 = 0;
      try {
        if (object1.has("time3")) time1 = object1.getLong("time3");
        else if (object1.has("time2")) time1 = object1.getLong("time2");
        else if (object1.has("time1")) time1 = object1.getLong("time1");
      } catch (JSONException e1) { Log.d(TAG, e1.getMessage()); }

      try {
        if (object2.has("time3")) time1 = object2.getLong("time3");
        else if (object2.has("time2")) time1 = object2.getLong("time2");
        else if (object2.has("time1")) time1 = object2.getLong("time1");
      } catch (JSONException e1) { Log.d(TAG, e1.getMessage()); }

      return (int) ((time2 - time1) / 1000);
    }
  }

  private void sort() {
    Collections.sort(messages, new ObjectComparator());
  }

}
