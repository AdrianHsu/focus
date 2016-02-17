package com.dots.focus.ui;

/**
 * Created by AdrianHsu on 2016/1/21.
 */

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dots.focus.R;
import com.dots.focus.adapter.DiscussHistoryRecyclerViewAdapter;
import com.dots.focus.util.FetchFriendUtil;
import com.dots.focus.util.KickUtil;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class KickHistoryActivity extends BaseActivity {

  private EditText editText1;
  private ImageView sendBtn;
  private ImageView profileImage;
  private TextView profileNameTv;
  private TextView friendStateTv;

  private String name;
  private String objectId;
  private long id;
  private int period;
  private long time1;
  private String content1;
  private long time2;
  private String content2;
  private static final String TAG = "KickHistory";

  private UltimateRecyclerView mRecyclerView;
  private DiscussHistoryRecyclerViewAdapter discussHistoryRecyclerViewAdapter = null;
  private LinearLayoutManager linearLayoutManager;
  private final ArrayList<JSONObject> messages = new ArrayList<>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_kick_history);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.title_kick_messages));
    toolbar.bringToFront();

    editText1 = (EditText) findViewById(R.id.editText1);
    editText1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mRecyclerView.scrollVerticallyToPosition(messages.size() - 1);
      }
    });
    editText1.setOnKeyListener(new OnKeyListener() {
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        // If the event is a key-down event on the "enter" button

        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

          sendMessages();
          return true;
        }
        return false;
      }
    });
    sendBtn = (ImageView) findViewById(R.id.send_btn);
    sendBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        sendMessages();
      }
    });

    profileImage = (ImageView) findViewById(R.id.profile_image);
    profileNameTv = (TextView) findViewById(R.id.profile_name);

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      name = extras.getString("user_name");
      objectId = extras.getString("objectId");
      id = extras.getLong("user_id");
      period = extras.getInt("period");
      time1 = extras.getLong("time1");
      content1 = extras.getString("content1");
      time2 = extras.getLong("time2");
      content2 = extras.getString("content2");
    }
    friendStateTv = (TextView) findViewById(R.id.friend_state);
    JSONObject friend = FetchFriendUtil.getFriendById(id);
    friendStateTv.setText(getFriendRelation(friend));

    String url = "https://graph.facebook.com/" + String.valueOf(id) +
                            "/picture?process=large";
    Picasso.with(this).load(url).into(profileImage);
    profileNameTv.setText(name);

    JSONObject mRequest1 = new JSONObject();
    try {
      mRequest1.put("content", content1);
      String timeString = TrackAccessibilityUtil.getDateByMilli(time1);
      mRequest1.put("time", timeString);
    } catch(JSONException e) {
      Log.v(TAG, e.getMessage());
    }
    JSONObject mRequest2 = new JSONObject();
    try {
      mRequest2.put("content", content2);
      String timeString = TrackAccessibilityUtil.getDateByMilli(time2);
      mRequest2.put("time", timeString);
    } catch(JSONException e) {
      Log.v(TAG, e.getMessage());
    }
    messages.add(mRequest1);
    messages.add(mRequest2);
    mRecyclerView = (UltimateRecyclerView) findViewById(R.id.discuss_recycler_view);
    discussHistoryRecyclerViewAdapter = new DiscussHistoryRecyclerViewAdapter( messages, this);
    linearLayoutManager = new LinearLayoutManager(this);

    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(discussHistoryRecyclerViewAdapter);
    mRecyclerView.scrollVerticallyToPosition(messages.size() - 1);
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }

  private void sendMessages() {
    // Perform action on key press
    JSONObject tmp = new JSONObject();
    String text = editText1.getText().toString();
    try {
      tmp.put("content", text);
      tmp.put("time", System.currentTimeMillis());
    } catch (JSONException e) {
      e.printStackTrace();
    }
    messages.add(tmp);
    mRecyclerView.getAdapter().notifyItemInserted(messages.size() - 1);
    mRecyclerView.scrollVerticallyToPosition(messages.size() - 1);
    KickUtil.kickResponse(text, objectId);

    editText1.setText("");
    sendBtn.setEnabled(false);
    onBackPressed();
  }
  protected String getFriendRelation(JSONObject friend) {

    Boolean timeLocked = false;
    Boolean timeLock = false;
    try {
      timeLocked = friend.getBoolean("timeLocked");
      timeLock = friend.getBoolean("timeLock");
    } catch (JSONException e) {
      Log.d(TAG, e.getMessage());
    }
    if(timeLocked && timeLock)
      return getResources().getString(R.string.relation_both);
    else if (timeLocked)
      return getResources().getString(R.string.relation_is_your_tp);
    else if (timeLock)
      return getResources().getString(R.string.relation_you_are_tp);
    else
      return getResources().getString(R.string.relation_just_friend);
  }
}