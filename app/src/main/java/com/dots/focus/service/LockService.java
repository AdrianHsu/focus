package com.dots.focus.service;

/**
 * Created by AdrianHsu on 2016/2/16.
 */
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.dots.focus.ui.LockView;

public class LockService extends Service
{
  public static final String LOCK_ACTION = "lock";
  public static final String UNLOCK_ACTION = "unlock";
  private Context mContext;
  private WindowManager mWinMng;
  private LockView screenView;

  @Override
  public IBinder onBind(Intent intent) {
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
    super.onDestroy();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    String action = intent.getAction();

    if(TextUtils.equals(action, LOCK_ACTION)) {
      Bundle extras = intent.getExtras();
      String title = extras.getString("title");
      String alert = extras.getString("alert");
      long id = extras.getLong("id");
      long time2 = extras.getLong("time2");
      int lock_period = extras.getInt("lock_period");
      if (title != null && alert != null && id != 0 && time2 != 0 && lock_period != 0)
        addView(title, alert, id, time2, lock_period);
    }
    else if (TextUtils.equals(action, UNLOCK_ACTION))
    {
      removeView();
      stopSelf();
    }
    return super.onStartCommand(intent, flags, startId);
  }

  public void addView(String title, String alert, long id, long time2, int lock_period)
  {
    if(screenView == null)
    {
      screenView = new LockView(mContext, title, alert, id, time2, lock_period);

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
