package com.dots.focus.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dots.focus.R;
import com.dots.focus.service.LockService;
import com.dots.focus.service.TrackAccessibilityService;

import java.util.Timer;
import java.util.TimerTask;

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

  public static String callApp = "com.asus.contacts";
  public static String messageApp = "com.asus.message";

  private long time2;
  private int lock_period;
  private String title;
  private String alert;
  private long id;

  public LockView(Context context, long time2, int lock_period, String title, String alert,
                  long id) {
    super(context);
    mContext = context;
    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    rootView = inflater.inflate(R.layout.view_lock, this);
    btnUnlock = (Button) rootView.findViewById(R.id.btn_unlock_screen);
    btnTel = (Button) rootView.findViewById(R.id.btn_tel);
    btnMes = (Button) rootView.findViewById(R.id.btn_mes);

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

    Timer timer = new Timer(true);
    timer.schedule(new MyTimerTask(), 1000, 1000);

  }
  public class MyTimerTask extends TimerTask
  {
    public void run()
    {
      
    }
  };


}
