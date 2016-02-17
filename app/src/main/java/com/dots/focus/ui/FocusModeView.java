package com.dots.focus.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dots.focus.R;
import com.dots.focus.service.FocusModeService;
import com.dots.focus.service.TrackAccessibilityService;

/**
 * Created by AdrianHsu on 2016/2/16.
 */
public class FocusModeView extends RelativeLayout
{
  private Context mContext;
  private View rootView;

  private Button btnUnlock;
  private Button btnTel;
  private Button btnMes;

  public static String callApp = "com.asus.contacts";
  public static String messageApp = "com.asus.message";

  public FocusModeView(Context context) {
    super(context);
    mContext = context;
    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    rootView = inflater.inflate(R.layout.view_focus_mode, this);
    btnUnlock = (Button) rootView.findViewById(R.id.btn_unlock_screen);
    btnTel = (Button) rootView.findViewById(R.id.btn_tel);
    btnMes = (Button) rootView.findViewById(R.id.btn_mes);

    btnUnlock.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(mContext, FocusModeService.class);
        i.setAction(FocusModeService.UNLOCK_ACTION);
        mContext.startService(i);
      }
    });

    btnTel.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(mContext, FocusModeService.class);
        i.setAction(FocusModeService.UNLOCK_ACTION);
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
        Intent i = new Intent(mContext, FocusModeService.class);
        i.setAction(FocusModeService.UNLOCK_ACTION);
        mContext.startService(i);
        Toast.makeText(mContext, "click on Messages", Toast.LENGTH_SHORT).show();

        TrackAccessibilityService.inLockMode = true;
        Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(messageApp);
        mContext.startActivity(launchIntent);

      }
    });



  }


}
