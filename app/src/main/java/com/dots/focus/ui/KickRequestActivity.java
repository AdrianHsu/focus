package com.dots.focus.ui;

/**
 * Created by AdrianHsu on 2016/1/21.
 */

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dots.focus.R;
import com.dots.focus.adapter.DiscussSelfRecyclerViewAdapter;
import com.dots.focus.util.FetchFriendUtil;
import com.dots.focus.util.KickUtil;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.rey.material.app.Dialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class KickRequestActivity extends BaseActivity {

  private EditText editText1;
  private ImageView sendBtn;
  private ImageView profileImage;
  private TextView profileNameTv;
  private TextView friendStateTv;
  private TextView expireTv;
  private Switch lockSwitch;
  private TextView SwitchLockTv;
  private TextView lockTimeTv;

  private String name;
  private String objectId;
  private long id;
  private int period;
  private long time;
  private String content;
  private int lockPickedTime;
  private int lockMaxTime = 0;
  private static final String TAG = "KickRequest";
  private Boolean timeLocked = false;
  private Boolean timeLock = false;

  private UltimateRecyclerView mRecyclerView;
  private DiscussSelfRecyclerViewAdapter discussRecyclerViewAdapter = null;
  private LinearLayoutManager linearLayoutManager;
  private final ArrayList<JSONObject> messages = new ArrayList<>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_kick_request);

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

    lockSwitch = (Switch) findViewById(R.id.switch_lock);
    lockTimeTv = (TextView) findViewById(R.id.lock_time);

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      name = extras.getString("user_name");
      objectId = extras.getString("objectId");
      id = extras.getLong("user_id");
      period = extras.getInt("period");
      time = extras.getLong("time");
      content = extras.getString("content");
    }

    friendStateTv = (TextView) findViewById(R.id.friend_state);
    JSONObject friend = FetchFriendUtil.getFriendById(id);
    friendStateTv.setText(getFriendRelation(friend));

    String url = "https://graph.facebook.com/" + String.valueOf(id) +
                            "/picture?process=large";
    Picasso.with(this).load(url).into(profileImage);
    profileNameTv.setText(name);

    JSONObject jsonObject = FetchFriendUtil.getFriendById(id);

    try {
      if (jsonObject != null) {
        lockMaxTime = jsonObject.getInt("lock_max_period");
      }
    } catch(JSONException e) {
      Log.v(TAG, e.getMessage());
    }
    Log.v(TAG, "Friend's lockMaxTime: " + lockMaxTime);

    lockSwitch.setEnabled(timeLocked);
    if(timeLocked)
      lockSwitch.setText("權限不足");

    lockSwitch.setChecked(false);
    lockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
          //dialog
          createDialog();
        } else {
          lockPickedTime = 0;
          lockTimeTv.setText("");
        }
      }
    });

    final long expire_time = System.currentTimeMillis() - KickUtil.expire_period;
    final Boolean expire = (time < expire_time);

    expireTv = (TextView) findViewById(R.id.expire);
    if(!expire) {
      expireTv.setText("ONLINE");
      expireTv.setTextColor(getResources().getColor(R.color.red));
    } else {
      expireTv.setText("EXPIRED");
      expireTv.setTextColor(getResources().getColor(R.color.semi_black));
      lockSwitch.setTextColor(getResources().getColor(R.color.semi_black));
      lockSwitch.setEnabled(false);
      sendBtn.setEnabled(false);

      editText1.setEnabled(false);
      editText1.setText("已經過期、無法傳送訊息。");
    }

    JSONObject mRequest = new JSONObject();
    try {
      mRequest.put("content", content);
      String timeString = TrackAccessibilityUtil.getDateByMilli(time);
      mRequest.put("time", timeString);
    } catch(JSONException e) {
      Log.v(TAG, e.getMessage());
    }
    messages.add(mRequest);
    mRecyclerView = (UltimateRecyclerView) findViewById(R.id.discuss_recycler_view);
    discussRecyclerViewAdapter = new DiscussSelfRecyclerViewAdapter( messages, this);
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
      String timeString = TrackAccessibilityUtil.getDateByMilli(System.currentTimeMillis());

      tmp.put("time", timeString);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    messages.add(tmp);
    mRecyclerView.getAdapter().notifyItemInserted(messages.size() - 1);
    mRecyclerView.scrollVerticallyToPosition(messages.size() - 1);
    KickUtil.kick(text, objectId);

    sendBtn.setEnabled(false);
    editText1.setEnabled(false);
    editText1.setText("訊息已傳送！");
    Toast.makeText(this, "訊息已傳送！", Toast.LENGTH_SHORT);

  }
  protected String getFriendRelation(JSONObject friend) {

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
  private String timeToString(int seconds) {
    int day = (int) TimeUnit.SECONDS.toDays(seconds);
    long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
    long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
    long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
    return String.format("%02d:%02d:%02d", hours, minute, second);
  }
  private void createDialog() {

    LockTimeView view = new LockTimeView(this, lockMaxTime);
    final Dialog mDialog = new Dialog(this);
    mDialog.title("鎖朋友多久呢？（秒鐘）")
                            .positiveAction(getResources().getString(R.string.done))
                            .negativeAction(getResources().getString(R.string.cancel))
                            .contentView(view)
                            .maxHeight(600)
                            .cancelable(true)
                            .show();
    mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override
      public void onCancel(DialogInterface dialogInterface) {
        //
        lockPickedTime = 0;
        lockTimeTv.setText("");
        mDialog.cancel();
        lockSwitch.setChecked(false);
      }
    });
    mDialog.setCanceledOnTouchOutside(false);
    mDialog.negativeActionClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        lockPickedTime = 0;
        lockTimeTv.setText("");
        mDialog.cancel();
        lockSwitch.setChecked(false);
      }
    });
    mDialog.positiveActionClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        lockPickedTime = LockTimeView.val;
        if(lockPickedTime != 0) {
          lockTimeTv.setText(timeToString(lockPickedTime));
          mDialog.dismiss();
        } else {
          lockTimeTv.setText("");
          mDialog.cancel();
          lockSwitch.setChecked(false);
        }
      }
    });
  }
}