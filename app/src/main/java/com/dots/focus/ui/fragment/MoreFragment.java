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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.focus.R;
import com.dots.focus.adapter.MoreRecyclerViewAdapter;
import com.dots.focus.ui.GoalSettingsActivity;
import com.dots.focus.ui.ProfileActivity;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoreFragment extends Fragment {

  private UltimateRecyclerView mRecyclerView;
  private View mStickyView;
  private Context context;
  private MoreRecyclerViewAdapter simpleRecyclerViewAdapter = null;
  private LinearLayoutManager linearLayoutManager;
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    context = getActivity();
    View v = inflater.inflate(R.layout.fragment_more, container, false);

    mStickyView = v.findViewById(R.id.sticky_view);

    ImageView profileImageView = (ImageView) mStickyView.findViewById(R.id.profile_image);
    TextView profileTextView = (TextView) mStickyView.findViewById(R.id.profile_name);

    ParseUser user = ParseUser.getCurrentUser();
    String name = user.getString("user_name");
    profileTextView.setText(name);
    String url ="https://graph.facebook.com/" + String.valueOf( user.getLong
      ("user_id") )+
      "/picture?type=large";
    Picasso.with(context).load(url).into(profileImageView);


    mStickyView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(view.getContext(), "sticky view clicked"
          , Toast
          .LENGTH_SHORT)
          .show();
        Intent intent = new Intent(context, ProfileActivity.class);
        context.startActivity(intent);
      }
    });

    mRecyclerView = (UltimateRecyclerView) v.findViewById(R.id.more_recycler_view);

    final List<String> stringList = new ArrayList<>();

    stringList.add(getResources().getString(R.string.title_goal_setting));
    stringList.add(getResources().getString(R.string.title_idle_setting));
    stringList.add(getResources().getString(R.string.title_lock_setting));
    stringList.add(getResources().getString(R.string.title_notif_setting));
    stringList.add(getResources().getString(R.string.title_focus_community));
    stringList.add(getResources().getString(R.string.title_parental_control));
    stringList.add(getResources().getString(R.string.title_advanced_setting));
    stringList.add(getResources().getString(R.string.title_logout));
    simpleRecyclerViewAdapter = new MoreRecyclerViewAdapter(stringList);
    linearLayoutManager = new LinearLayoutManager(context);

    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(simpleRecyclerViewAdapter);
    return v;
  }
}
