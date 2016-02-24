package com.dots.focus.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dots.focus.ui.CustomDialogActivity;
import com.dots.focus.ui.MainActivity;
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

        JSONObject data;
        String title = null;
        String alert = null;
        Boolean dialog = null;
        Long id = null;
        int lock_period = 0;
        int lock_max_period = ParseUser.getCurrentUser().getInt("lock_max_period");

        try {
            data = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            title = data.getString("title");
            alert = data.getString("alert");
            dialog = data.getBoolean("dialog");
            id = data.getLong("id");
            lock_period = data.getInt("lock_period");
            if (lock_period > lock_max_period)
                lock_period = lock_max_period;

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

            if (dialog) {
                // use lock_period here to lock myself

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
