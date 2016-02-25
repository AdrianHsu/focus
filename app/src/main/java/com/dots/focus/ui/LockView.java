package com.dots.focus.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.focus.R;
import com.dots.focus.service.LockService;
import com.dots.focus.service.TrackAccessibilityService;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by AdrianHsu on 2016/2/16.
 */
public class LockView extends RelativeLayout
{
  private Context mContext;
  private View rootView;

  private Button btnUnlock;
  private Button btnTel;
  private Button btnMes;
  private ImageView profileImage;
  private TextView alertTv;
  private TextView titleTv;
  private TextView leftLockTimeTv;
  private Timer timer;
  private int remainSecond = 0;

  public static String callApp = "com.asus.contacts";
  public static String messageApp = "com.asus.message";

  private long time2;
  private int lock_period;
  private String title;
  private String alert;
  private long id;

  public LockView(Context context, String _title, String _alert, long _id, long _time2, int
          _lock_period) {
    super(context);
    mContext = context;
    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    rootView = inflater.inflate(R.layout.view_lock, this);
    btnUnlock = (Button) rootView.findViewById(R.id.btn_unlock_screen);
    btnTel = (Button) rootView.findViewById(R.id.btn_tel);
    btnMes = (Button) rootView.findViewById(R.id.btn_mes);
    profileImage = (ImageView) rootView.findViewById(R.id.profile_image);
    alertTv = (TextView) rootView.findViewById(R.id.alert);
    titleTv = (TextView) rootView.findViewById(R.id.title);
    leftLockTimeTv = (TextView) rootView.findViewById(R.id.lock_time_left);

    title = _title;
    alert = _alert;
    id = _id;
    time2 = _time2;
    lock_period = _lock_period;

    titleTv.setText(title);
    alertTv.setText(alert);
    String url ="https://graph.facebook.com/" + String.valueOf(id)+
                            "/picture?type=large";
    Picasso.with(mContext).load(url).into(profileImage);


    btnUnlock.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(mContext, LockService.class);
        i.setAction(LockService.UNLOCK_ACTION);
        mContext.startService(i);
      }
    });

    btnTel.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(mContext, LockService.class);
        i.setAction(LockService.UNLOCK_ACTION);
        mContext.startService(i);

        Toast.makeText(mContext, "click on Phone", Toast.LENGTH_SHORT).show();

        TrackAccessibilityService.inLockMode = true;
        Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(callApp);
        mContext.startActivity(launchIntent);

      }
    });
    btnMes.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(mContext, LockService.class);
        i.setAction(LockService.UNLOCK_ACTION);
        mContext.startService(i);
        Toast.makeText(mContext, "click on Messages", Toast.LENGTH_SHORT).show();

        TrackAccessibilityService.inLockMode = true;
        Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(messageApp);
        mContext.startActivity(launchIntent);

      }
    });
    setTimer(time2, lock_period);
  }
  private void setTimer(long time2, int lock_period) {
    remainSecond = (int) (time2 / 1000 + lock_period - System.currentTimeMillis() / 1000);
    timer = new Timer();
    timer.schedule(new EverySecond(), 0, 1000);
  }
  private void terminateTheLock() {
    // destroy the view and the service...
    Intent i = new Intent(mContext, LockService.class);
    i.setAction(LockService.UNLOCK_ACTION);
    mContext.startService(i);
  }
  class EverySecond extends TimerTask {
    public void run() {
      if (--remainSecond == 0) {
        timer.cancel();
        terminateTheLock();
      } else {

        leftLockTimeTv.setText(timeToString(remainSecond));
      }
    }
  }

  private String timeToString(int seconds) {
    int day = (int) TimeUnit.SECONDS.toDays(seconds);
    long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
    long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
    long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
    return String.format("%02d:%02d:%02d", hours, minute, second);
  }

}
