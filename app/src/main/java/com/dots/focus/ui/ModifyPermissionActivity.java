package com.dots.focus.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.facebook.internal.BoltsMeasurementEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;

/**
 * Created by AdrianHsu on 2016/2/15.
 */
public class ModifyPermissionActivity extends BaseActivity {

  private Toolbar toolbar;
  private String name;
  private Long id;
  private ImageView profileImage;
  private TextView profileNameTv;
  private TextView friendStateTv;
  private Button cancelBtn;
  private Button sendBtn;
  private CheckBox getNotifBtn;
  private CheckBox timeLockedBtn;
  private CheckBox timeLockBtn;
  private Boolean originalTimeLocked;
  private Boolean originalTimeLock;
  private static final String TAG = "Permission";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_modify_permission);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getResources().getString(R.string.modify_permission_text));
    profileImage = (ImageView) findViewById(R.id.profile_image);
    profileNameTv = (TextView) findViewById(R.id.profile_name);
    friendStateTv = (TextView) findViewById(R.id.content);
    cancelBtn = (Button)findViewById(R.id.cancel);
    sendBtn = (Button)findViewById(R.id.send);

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      name = extras.getString("user_name");
      id = extras.getLong("user_id");
    }

    String url = "https://graph.facebook.com/" + String.valueOf(id) +
                            "/picture?process=large";
    Picasso.with(this).load(url).into(profileImage);
    profileNameTv.setText(name);

    JSONObject friend = FetchFriendUtil.getFriendById(id);
    initFriendState(friend);
    friendStateTv.setText(getFriendRelation(friend));
    timeLockedBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (timeLockBtn.isChecked() == originalTimeLocked
                                && timeLockedBtn.isChecked() == originalTimeLock)
          sendBtn.setEnabled(false);
        else
          sendBtn.setEnabled(true);

      }
    });
    timeLockBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(timeLockBtn.isChecked() == originalTimeLocked
        && timeLockedBtn.isChecked() == originalTimeLock)
          sendBtn.setEnabled(false);
        else
          sendBtn.setEnabled(true);
      }
    });

    sendBtn.setEnabled(false);
    cancelBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onBackPressed();

      }
    });
    sendBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        FetchFriendUtil.modifyPopUp(id, getNotifBtn.isChecked());

        if(timeLockedBtn.isChecked())
          TimePoliceUtil.timePoliceInvite(id, name, 10);
        else {
          TimePoliceUtil.timePoliceCancel(id);
        }
        if(timeLockBtn.isEnabled()) {
          if(!timeLockBtn.isChecked()) {
            TimePoliceUtil.timePoliceDelete(id);
          }
        }
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
      originalTimeLock = friend.getBoolean("timeLock");
      getNotifBtn.setChecked(friend.getBoolean("pop-up"));
      if(TimePoliceUtil.policeNum < 2) {
        timeLockedBtn.setChecked(originalTimeLocked);

      } else if (TimePoliceUtil.policeNum == 2) {
        if(originalTimeLocked) // 此人已經是您的時間警察
          timeLockedBtn.setEnabled(true);
        else
          timeLockedBtn.setEnabled(false);
        if(timeLockedBtn.isEnabled()) {
          timeLockedBtn.setChecked(originalTimeLocked);
        }
      }
//      timeLockedBtn.setChecked(friend.getBoolean("timeLocked"));
      timeLockBtn.setEnabled(originalTimeLock);
      if(timeLockBtn.isEnabled())
        timeLockBtn.setChecked(originalTimeLock);

    } catch(JSONException e) {
      e.printStackTrace();
    }
  }
  private String getFriendRelation(JSONObject friend) {

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

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }

}
