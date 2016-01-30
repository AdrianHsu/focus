package com.dots.focus.ui;

/**
 * Created by AdrianHsu on 2016/1/21.
 */

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.dots.focus.R;
import com.dots.focus.adapter.DiscussRecyclerViewAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

public class KickMessagesActivity extends BaseActivity {

  private EditText editText1;
  private ImageView sendBtn;

  private UltimateRecyclerView mRecyclerView;
  private DiscussRecyclerViewAdapter discussRecyclerViewAdapter = null;
  private LinearLayoutManager linearLayoutManager;
  final ArrayList<JSONObject> messages = new ArrayList<>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_discuss);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("戳朋友一下");
    toolbar.bringToFront();
    mRecyclerView = (UltimateRecyclerView) findViewById(R.id.discuss_recycler_view);
    for(int i = 0; i < 3; i++) {
      JSONObject message = new JSONObject();
      try {
        message.put("text", "這是測試文字：" + String.valueOf(i));
      } catch (JSONException e) {
        e.printStackTrace();
      }

      messages.add(message);
    }

    discussRecyclerViewAdapter = new DiscussRecyclerViewAdapter( messages, this);
    linearLayoutManager = new LinearLayoutManager(this);

    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(discussRecyclerViewAdapter);
    mRecyclerView.scrollVerticallyToPosition(messages.size() - 1);

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
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }

  private void sendMessages() {
    // Perform action on key press
    JSONObject tmp = new JSONObject();
    try {
      tmp.put("text", editText1.getText().toString());
    } catch (JSONException e) {
      e.printStackTrace();
    }
    messages.add(tmp);
    mRecyclerView.getAdapter().notifyItemInserted(messages.size() - 1);
    mRecyclerView.scrollVerticallyToPosition(messages.size() - 1);

    editText1.setText("");
  }
}