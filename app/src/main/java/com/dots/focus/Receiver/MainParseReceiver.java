package com.dots.focus.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.dots.focus.service.FocusModeService;
import com.dots.focus.service.LockService;
import com.dots.focus.ui.CustomDialogActivity;
import com.dots.focus.ui.MainActivity;
import com.dots.focus.util.FetchFriendUtil;
import com.dots.focus.util.SettingsUtil;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class MainParseReceiver extends ParsePushBroadcastReceiver {

    private static final String TAG = "Messages";

  @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        Log.d(TAG, "onPushReceive");

        JSONObject data = null;
        boolean is_kicked = false;
        try {
            data = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            if (data.has("type") || data.getString("type").equals("focus_kicked"))
                is_kicked = true;
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
        if (!is_kicked)
            return;

        String title = null;
        String alert = null;
        Boolean dialog = null;
        long id = 0;
        long time2 = 0;
        int lock_period = 0;
        int lock_max_period = ParseUser.getCurrentUser().getInt("lock_max_period");
        boolean canLock = false;

        try {
            title = data.getString("title");
            alert = data.getString("alert");
            dialog = data.getBoolean("dialog");
            id = data.getLong("id");
            time2 = data.getLong("time2");
            lock_period = data.getInt("lock_period");
            if (lock_period > lock_max_period)
                lock_period = lock_max_period;
            try {
                JSONObject friend = FetchFriendUtil.getFriendInfo(id);
                if (friend != null)
                    canLock = friend.optBoolean("timeLocked");
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            } finally {
                if (!canLock)
                    lock_period = 0;
            }

            if (title != null)
                Log.d(TAG, "title: " + title);
            if (alert != null)
                Log.d(TAG, "alert: " + alert);
//            if (dialog != null)
            Log.d(TAG, "dialog: " + String.valueOf(alert));

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

        if (dialog != null) {

            if (dialog && SettingsUtil.getBooleen("friendLock")) {
                // use lock_period here to lock myself
              Log.v(TAG, "lock_period: " + lock_period + "canLock:" + canLock);
              if (title != null && alert != null && id != 0 && time2 != 0 && lock_period != 0) {
                Intent i = new Intent(context, LockService.class);
                i.setAction(FocusModeService.LOCK_ACTION);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("title", title);
                i.putExtra("alert", alert);
                i.putExtra("id", id);
                i.putExtra("time2", time2);
                i.putExtra("lock_period", lock_period);

                context.startService(i);
                Toast.makeText(context, "被時間警察鎖屏" + lock_period + "秒！", Toast.LENGTH_LONG).show();
//                context.finish();

              } else {

                Intent mIntent = new Intent(context, CustomDialogActivity.class);
                mIntent.putExtra("title", title);
                mIntent.putExtra("alert", alert);
                mIntent.putExtra("id", id);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mIntent);
              }
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
