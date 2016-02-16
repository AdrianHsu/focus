package com.dots.focus.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.dots.focus.R;
import com.dots.focus.service.FocusModeService;

/**
 * Created by AdrianHsu on 2016/2/16.
 */
public class FocusModeView extends RelativeLayout
{
  private Context mContext;
  private View rootView;

  private Button btnUnlock;

  public FocusModeView(Context context)
  {
    super(context);
    mContext = context;
    LayoutInflater inflater = (LayoutInflater) mContext
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    rootView = inflater.inflate(R.layout.view_focus_mode, this);
    btnUnlock = (Button) rootView.findViewById(R.id.btn_unlock_screen);

    if(btnUnlock != null)
      System.out.println("found the button");
    btnUnlock.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(mContext, FocusModeService.class);
        i.setAction(FocusModeService.UNLOCK_ACTION);
        mContext.startService(i);
      }
    });
  }


}
