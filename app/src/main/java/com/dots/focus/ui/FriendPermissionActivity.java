package com.dots.focus.ui;


import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.dots.focus.R;
import com.dots.focus.util.FetchFriendUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AdrianHsu on 15/9/23.
 */
public class FriendPermissionActivity extends BaseActivity {

  protected Toolbar toolbar;
  protected String name;
  protected Long id;
  protected String objectId;
  protected ImageView profileImage;
  protected TextView profileNameTv;
  protected TextView friendStateTv;
  protected Button cancelBtn;
  protected Button sendBtn;
  protected CheckBox getNotifBtn;
  protected CheckBox timeLockedBtn;
  protected CheckBox timeLockBtn;
  protected Boolean originalTimeLocked;
  protected Boolean originalTimeLock;
  protected static final String TAG = "Permission";
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
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    FetchFriendUtil.modifyPopUp(id, getNotifBtn.isChecked());
    onBackPressed();
    return true;
  }

}
