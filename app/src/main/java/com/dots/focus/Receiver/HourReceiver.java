package com.dots.focus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dots.focus.service.TrackAccessibilityService;
import com.dots.focus.ui.MainActivity;
import com.dots.focus.util.TrackAccessibilityUtil;

import java.util.Calendar;

public class HourReceiver extends BroadcastReceiver {
    private static MainActivity main = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getExtras().getString("msg");
        if (msg != null && msg.equals("an_hour_is_up")) {
            resetHourBlock(context);

            main.setHourAlarm();
        }
    }

    public static void setMain(MainActivity m) {
        main = m;
    }

    private void resetHourBlock(Context context) {
        Log.d("HourReceiver", "reseting HourBlock...");
        long time = System.currentTimeMillis();
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTimeInMillis(time);

        time = TrackAccessibilityUtil.anHour * (time / TrackAccessibilityUtil.anHour);
        int theHour = (rightNow.get(Calendar.HOUR_OF_DAY) + TrackAccessibilityUtil.getTimeOffset()) % 24;

        Intent intent = new Intent();
        intent.setAction("HourReceiver_broadcast_an_hour");
        intent.putExtra("time", time);
        context.sendBroadcast(intent);

        TrackAccessibilityUtil.newHour(time, theHour);
    }
}
