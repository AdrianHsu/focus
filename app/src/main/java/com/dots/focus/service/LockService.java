package com.dots.focus.service;

/**
 * Created by AdrianHsu on 2016/2/16.
 */
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.dots.focus.ui.LockView;

import java.util.Timer;
import java.util.TimerTask;

public class LockService extends Service
{
  public static final String LOCK_ACTION = "lock";
  public static final String UNLOCK_ACTION = "unlock";
  private static Context mContext;
  private WindowManager mWinMng;
  private LockView screenView;
  private static Timer timer = new Timer();

  private String title;
  private String alert;
  private long id;
  private int lock_period;

  @Override
  public IBinder onBind(Intent intent) {

    Bundle extras = intent.getExtras();

    title = extras.getString("title");
    alert = extras.getString("alert");
    id = extras.getLong("id");
    lock_period = extras.getInt("lock_period");

    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    mContext = getApplicationContext();

    mWinMng = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
  }

  @Override
  public void onDestroy() {
    Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
    super.onDestroy();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    String action = intent.getAction();
    timer.scheduleAtFixedRate(new mainTask(), 0, lock_period);

    if(TextUtils.equals(action, LOCK_ACTION))
      addView();
    else if(TextUtils.equals(action, UNLOCK_ACTION))
    {
      removeView();
      stopSelf();
    }
    return super.onStartCommand(intent, flags, startId);
  }
  private class mainTask extends TimerTask
  {
    public void run()
    {
      toastHandler.sendEmptyMessage(0);
    }
  }
  private static final Handler toastHandler = new Handler()
  {
    @Override
    public void handleMessage(Message msg)
    {
      Toast.makeText(mContext, "test", Toast.LENGTH_SHORT).show();
    }
  };
  public void addView()
  {
    if(screenView == null)
    {
      screenView = new LockView(mContext);

      LayoutParams param = new LayoutParams();
      param.type = LayoutParams.TYPE_SYSTEM_ALERT;
      param.format = PixelFormat.RGBA_8888;
      // mParam.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
      // | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
      param.width = LayoutParams.MATCH_PARENT;
      param.height = LayoutParams.MATCH_PARENT;

      mWinMng.addView(screenView, param);
    }
  }

  public void removeView()
  {
    if(screenView != null)
    {
      mWinMng.removeView(screenView);
      screenView = null;
    }
  }

}
