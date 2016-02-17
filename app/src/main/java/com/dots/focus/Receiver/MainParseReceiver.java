package com.dots.focus.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dots.focus.ui.CustomDialogActivity;
import com.dots.focus.ui.MainActivity;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AdrianHsu on 2016/2/12.
 */
public class MainParseReceiver extends ParsePushBroadcastReceiver {

  private static final String TAG = "Messages";

  @Override
  protected void onPushReceive(Context context, Intent intent) {
    super.onPushReceive(context, intent);
    Log.d(TAG, "onPushReceive");

    JSONObject data;
    String title = null;
    String alert = null;
    Boolean dialog = null;
    Long id = null;

    try {
      data = new JSONObject(intent.getExtras().getString("com.parse.Data"));

      title = data.getString("title");
      alert = data.getString("alert");
      dialog = data.getBoolean("dialog");
      id = data.getLong("id");

      if (title != null)
        Log.d(TAG, "title: " + title);
      if (alert != null)
        Log.d(TAG, "alert: " + alert);
      if(dialog != null)
        Log.d(TAG, "dialog: " + String.valueOf(alert));

    } catch (JSONException e) { Log.d(TAG, e.getMessage()); }

    if(dialog != null) {

      if(dialog == true) {

        Intent mIntent = new Intent(context, CustomDialogActivity.class);
        mIntent.putExtra("title", title);
        mIntent.putExtra("alert", alert);
        mIntent.putExtra("id", id);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
      }
    }
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
