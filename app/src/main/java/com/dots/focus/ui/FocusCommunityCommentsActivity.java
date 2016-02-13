package com.dots.focus.ui;



/**
 * Created by AdrianHsu on 2015/12/13.
 */

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.dots.focus.R;
import com.dots.focus.adapter.CommunityCommentAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FocusCommunityCommentsActivity extends BaseActivity {

  private EditText editText1;
  private ImageView sendBtn;
  final List<JSONObject> jsonObjectList = new ArrayList<>();

  private UltimateRecyclerView mRecyclerView;
  private CommunityCommentAdapter mCommunityCommentAdapter;
  private LinearLayoutManager linearLayoutManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_focus_community_comment);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
//    ParseUser user = ParseUser.getCurrentUser();
//    String name = user.getString("name");
//    collapsingToolbarLayout.setTitle(name);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.title_focus_community));

    mRecyclerView = (UltimateRecyclerView) findViewById(R.id.focus_community_recycler_view);
    linearLayoutManager = new LinearLayoutManager(this);

    for(int i = 0; i < 3; i++) {
      JSONObject jsonObject = new JSONObject();
      try {
        jsonObject.put("content", "測試訊息" + i);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      jsonObjectList.add(jsonObject);
    }

    mCommunityCommentAdapter = new CommunityCommentAdapter(jsonObjectList, this);

    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(mCommunityCommentAdapter);


    editText1 = (EditText) findViewById(R.id.editText1);
    editText1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mRecyclerView.scrollVerticallyToPosition(jsonObjectList.size() - 1);
      }
    });
    editText1.setOnKeyListener(new View.OnKeyListener() {
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

  private void sendMessages() {
    // Perform action on key press
    JSONObject tmp = new JSONObject();
    try {
      tmp.put("content", editText1.getText().toString());
    } catch (JSONException e) {
      e.printStackTrace();
    }
    jsonObjectList.add(tmp);
    mRecyclerView.getAdapter().notifyItemInserted(jsonObjectList.size() - 1);
    mRecyclerView.scrollVerticallyToPosition(jsonObjectList.size() - 1);

    editText1.setText("");
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }
}