package com.dots.focus.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dots.focus.R;
import com.dots.focus.util.FetchFriendUtil;
import com.dots.focus.util.TimePoliceUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AdrianHsu on 2016/2/15.
 */
public class ReplyPermissionActivity extends FriendPermissionActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reply_permission);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.modify_permission_text));
    profileImage = (ImageView) findViewById(R.id.profile_image);
    profileNameTv = (TextView) findViewById(R.id.profile_name);
    friendStateTv = (TextView) findViewById(R.id.content);
    cancelBtn = (Button)findViewById(R.id.reject);
    sendBtn = (Button)findViewById(R.id.send);

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      name = extras.getString("user_name");
      id = extras.getLong("user_id");
      objectId = extras.getString("objectId");
    }

    String url = "https://graph.facebook.com/" + String.valueOf(id) +
                            "/picture?process=large";
    Picasso.with(this).load(url).into(profileImage);
    profileNameTv.setText(name);

    JSONObject friend = FetchFriendUtil.getFriendById(id);
    initFriendState(friend);

    friendStateTv.setText(getFriendRelation(friend));
//    timeLockedBtn.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        if (timeLockBtn.isChecked() == originalTimeLocked
//                                && timeLockedBtn.isChecked() == originalTimeLock)
//          sendBtn.setEnabled(false);
//        else
//          sendBtn.setEnabled(true);
//
//      }
//    });
//    timeLockBtn.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        if (timeLockBtn.isChecked() == originalTimeLocked
//                                && timeLockedBtn.isChecked() == originalTimeLock)
//          sendBtn.setEnabled(false);
//        else
//          sendBtn.setEnabled(true);
//      }
//    });
      cancelBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        FetchFriendUtil.modifyPopUp(id, getNotifBtn.isChecked());
        TimePoliceUtil.timePoliceReply(false, objectId);
        onBackPressed();
      }
    });
    sendBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        FetchFriendUtil.modifyPopUp(id, getNotifBtn.isChecked());
        TimePoliceUtil.timePoliceReply(timeLockBtn.isChecked(), objectId);
        onBackPressed();
      }
    });
  }

  private void initFriendState(JSONObject friend) {

    getNotifBtn = (CheckBox) findViewById(R.id.getNotifBtn);
    timeLockedBtn = (CheckBox) findViewById(R.id.timeLockedBtn);
    timeLockBtn = (CheckBox)findViewById(R.id.timeLockBtn);

    try {
      originalTimeLocked = friend.getBoolean("timeLocked");
//      originalTimeLock = friend.getBoolean("timeLock");
      getNotifBtn.setChecked(friend.getBoolean("pop-up"));
      timeLockedBtn.setEnabled(false);
      timeLockedBtn.setChecked(originalTimeLocked);
      timeLockBtn.setEnabled(false);
      timeLockBtn.setChecked(true);

    } catch(JSONException e) {
      e.printStackTrace();
    }
  }
}
