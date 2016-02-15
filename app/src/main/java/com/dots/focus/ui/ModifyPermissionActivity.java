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
public class ModifyPermissionActivity extends BaseActivity {

  private Toolbar toolbar;
  private String name;
  private Long id;
  private ImageView profileImage;
  private TextView profileNameTv;
  private TextView friendStateTv;
  private Button cancelBtn;
  private Button sendBtn;
  private RadioButton getNotifBtn;
  private CheckBox timeLockedBtn;
  private CheckBox timeLockBtn;

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
          TimePoliceUtil.timePoliceInvite(id, name);

        if(timeLockBtn.isEnabled()) {
          if(!timeLockBtn.isChecked()) {
           // 放棄監控委託人的權限
          }
        }
      }
    });
  }

  private void initFriendState(JSONObject friend) {

    getNotifBtn = (RadioButton) findViewById(R.id.getNotifBtn);
    timeLockedBtn = (CheckBox) findViewById(R.id.timeLockedBtn);
    timeLockBtn = (CheckBox)findViewById(R.id.timeLockBtn);

    try {
      getNotifBtn.setChecked(friend.getBoolean("pop-up"));
//      if(< 2位時間警察) {
//        timeLockedBtn.setChecked(friend.getBoolean("timeLocked"));
//
//      } else if (== 2位時間警察) {
//        if(timeLocked == true) // 此人已經是您的時間警察
//          timeLockedBtn.setEnabled(true);
//        else
//          timeLockedBtn.setEnabled(false);
//        if(timeLockedBtn.isEnabled()) {
//          timeLockedBtn.setChecked(friend.getBoolean("timeLocked"));
//        }
//      }
      timeLockedBtn.setChecked(friend.getBoolean("timeLocked"));
      timeLockBtn.setEnabled(friend.getBoolean("timeLock"));
      if(timeLockBtn.isEnabled())
        timeLockBtn.setChecked(true);

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
      e.printStackTrace();
    }
    if(timeLocked && timeLock)
      return "是您的時間警察與委託人";
    else if (timeLocked)
      return "是您的時間警察";
    else if (timeLock)
      return "是您的委託人";
    else
      return "是您的好友";
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    onBackPressed();
    return true;
  }

}
