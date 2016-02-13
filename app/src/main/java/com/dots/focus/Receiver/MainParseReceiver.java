package com.dots.focus.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dots.focus.ui.CustomDialogActivity;
import com.dots.focus.ui.MainActivity;
import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by AdrianHsu on 2016/2/12.
 */
public class MainParseReceiver extends ParsePushBroadcastReceiver {

  private static final String TAG = "Messages";

  @Override
  protected void onPushReceive(Context context, Intent intent) {
    super.onPushReceive(context, intent);
    Log.d(TAG, "onPushReceive");

    Intent mIntent = new Intent(context,CustomDialogActivity.class);
    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(mIntent);
  }

  @Override
  protected void onPushDismiss(Context context, Intent intent) {
    super.onPushDismiss(context, intent);
    Log.d(TAG, "onPushDismiss");
  }

  @Override
  protected void onPushOpen(Context context, Intent intent) {
    super.onPushOpen(context, intent);
    Log.d(TAG, "onPushOpen");
    Intent mIntent = new Intent(context, MainActivity.class);
    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(mIntent);
  }
}
