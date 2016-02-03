package com.dots.focus.ui;

/**
 * Created by AdrianHsu on 2016/1/21.
 */

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.dots.focus.R;
import com.dots.focus.adapter.DiscussRecyclerViewAdapter;
import com.dots.focus.util.KickUtil;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class KickMessagesActivity extends BaseActivity {

  private EditText editText1;
  private ImageView sendBtn;
  private ImageView profileImage;
  private TextView profileNameTv;

  private String name;
  private String objectId;
  private long id;
  private int period;
  private long time;
  private String content;
  private static final String TAG = "KickMessages";

  private UltimateRecyclerView mRecyclerView;
  private DiscussRecyclerViewAdapter discussRecyclerViewAdapter = null;
  private LinearLayoutManager linearLayoutManager;
  private final ArrayList<JSONObject> messages = new ArrayList<>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_kick_messages);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("戳朋友一下");
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
      name = extras.getString("name");
      objectId = extras.getString("objectId");
      id = extras.getLong("user_id");
      period = extras.getInt("period");
      time = extras.getLong("time");
      content = extras.getString("content");
    }

    String url = "https://graph.facebook.com/" + String.valueOf(id) +
                            "/picture?process=large";
    Picasso.with(this).load(url).into(profileImage);
    profileNameTv.setText(name);

    JSONObject mRequest = new JSONObject();
    try {
      mRequest.put("content", content);
      mRequest.put("time", timeToString((int)time));
    } catch(JSONException e) {
      e.printStackTrace();
    }
    messages.add(mRequest);
    mRecyclerView = (UltimateRecyclerView) findViewById(R.id.discuss_recycler_view);
    discussRecyclerViewAdapter = new DiscussRecyclerViewAdapter( messages, this);
    linearLayoutManager = new LinearLayoutManager(this);

    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(discussRecyclerViewAdapter);
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
      tmp.put("time", timeToString((int)time));
    } catch (JSONException e) {
      e.printStackTrace();
    }
    messages.add(tmp);
    mRecyclerView.getAdapter().notifyItemInserted(messages.size() - 1);
    mRecyclerView.scrollVerticallyToPosition(messages.size() - 1);
    KickUtil.kick(text, objectId);

    editText1.setText("");
    sendBtn.setEnabled(false);
    onBackPressed();
  }
  private String timeToString(int seconds) {
    int day = (int) TimeUnit.SECONDS.toDays(seconds);
    long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
    long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
    long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
    return String.format("%02d:%02d:%02d", hours, minute, second);
  }
}