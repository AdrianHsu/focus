package com.dots.focus.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dots.focus.service.TrackAccessibilityService;
import com.dots.focus.ui.MainActivity;
import com.dots.focus.util.TrackAccessibilityUtil;

import java.util.Calendar;

public class HourReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getExtras().getString("msg");
        if (msg != null && msg.equals("an_hour_is_up"))
            resetHourBlock(context);
    }

    private void resetHourBlock(Context context) {
        Log.d("HourReceiver", "reseting HourBlock...");
        long time = System.currentTimeMillis();
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTimeInMillis(time);

        time = TrackAccessibilityUtil.anHour * (time / TrackAccessibilityUtil.anHour);

        Intent intent = new Intent();
        intent.setAction("HourReceiver_broadcast_an_hour");
        intent.putExtra("time", time);
        context.sendBroadcast(intent);
    }
}
