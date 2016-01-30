package com.dots.focus.util;


import android.util.Log;

import com.dots.focus.model.DayBlock;
import com.dots.focus.model.HourBlock;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class TrackAccessibilityUtil {
    public static int anHour = 3600000;
    private static DayBlock currentDay = null;
    private static HourBlock currentHour = null;
    private static String TAG = "TrackAccessibilityUtil";

    public static int getTimeOffset() {
        return TimeZone.getDefault().getOffset(System.currentTimeMillis()) / anHour;
    }
/*
    public static int[] getCategory() {
        int[] data = new int[] {0, 0, 0, 0};
        List<Integer> appLength = getCurrentHour(System.currentTimeMillis()).getList("appLength");
        for (int i = 0; i < appLength.size(); ++i) {
            Log.d(TAG, "FetchAppUtil.getApp: " + FetchAppUtil.getApp(i).getPackageName());
            String temp = FetchAppUtil.getApp(i).getCategory();
            switch (temp) {
                case "Social":
                    data[0] += appLength.get(i);
                    break;
                case "Productivity":
                    data[1] += appLength.get(i);
                    break;
                case "Communication":
                    data[2] += appLength.get(i);
                    break;
                default:
                    data[3] += appLength.get(i);
                    break;
            }
        }
        return data;
    }
*/
    public static DayBlock getCurrentDay(long time){
        long localDay = getLocalDay(time);

        if (currentDay == null) {
            ParseQuery<DayBlock> query = ParseQuery.getQuery(DayBlock.class);
            query.whereEqualTo("time", localDay);
            query.fromLocalDatastore();
            try {
                currentDay = query.getFirst();
            } catch (ParseException e) {
                Log.d(TAG, "getCurrentDay in local database went wrong.");
            }
            if (currentDay == null)
                newDay(localDay);
        }
        else if (localDay != currentDay.getLong("time"))
            newDay(localDay);

        if (currentDay == null) Log.d(TAG, "return null day QAQ...");
        else Log.d(TAG, "day id: " + currentDay.getObjectId());
        return currentDay;
    }

    public static HourBlock getCurrentHour(long time){
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTimeInMillis(time);

        time = anHour * (time / anHour);
        int theHour = (rightNow.get(Calendar.HOUR_OF_DAY) + getTimeOffset()) % 24;
        if (currentHour == null) {
            ParseQuery<HourBlock> query = ParseQuery.getQuery(HourBlock.class);
            query.whereEqualTo("time", time);
            query.fromLocalDatastore();
            try {
                currentHour = query.getFirst();
            } catch (ParseException e) {
                Log.d(TAG, "getCurrentHour in local database went wrong.");
            }
            if (currentHour == null)
                newHour(time, theHour);
        }
        else if (time != currentHour.getLong("time")) {
            newHour(time, theHour);
        }
        if (currentHour == null)  Log.d(TAG, "return null hour QAQ...");
        return currentHour;
    }

    //helper functions
    private static long getLocalDay(long time) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTimeInMillis(24 * anHour * ((time + anHour * getTimeOffset()) / (24 * anHour)));
         // shift to local time
        //rightNow.set(rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH),
        //        rightNow.get(Calendar.DAY_OF_MONTH), 0, 0); // discard hours
        rightNow.setTimeInMillis(rightNow.getTimeInMillis() - anHour * getTimeOffset()); // local day in  standard time
        Log.d(TAG, "localDay, Year: " + rightNow.get(Calendar.YEAR)
                        + ", Month: " + rightNow.get(Calendar.MONTH) +
                        ", Day: " + rightNow.get(Calendar.DAY_OF_MONTH) +
                        ", Hour: " + rightNow.get(Calendar.HOUR_OF_DAY)
        );

        return rightNow.getTimeInMillis();
    }

    private static void newDay(final long dayInLong) {
        if (currentDay != null)
            currentDay.saveEventually();
        currentDay = new DayBlock(dayInLong);
        currentDay.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d(TAG, "currentDay pin error: " + e.getMessage());
                }
                List<String> hourBlocks = getCurrentDay(dayInLong).getHourBlocks();
                if (hourBlocks == null) {
                    Log.d(TAG, "hourBlocks is null.");
                    return;
                }
                if (hourBlocks.size() < 24) {
                    Log.d(TAG, "hourBlocks.size(): " + hourBlocks.size());
                    return;
                }
                for (int i = 0; i < 24; ++i) {
                    if (!hourBlocks.get(i).equals("")) {
                        ParseQuery<HourBlock> query = ParseQuery.getQuery(HourBlock.class);
                        query.getInBackground(hourBlocks.get(i), new GetCallback<HourBlock>() {
                            @Override
                            public void done(HourBlock hourBlock, ParseException e) {
                                if (hourBlock != null && e == null)
                                    hourBlock.setDayBlock(getCurrentDay(dayInLong).getObjectId());
                            }
                        });
                    }
                }
            }
        });

    }
    public static void newHour(final long hourInLong, final int h) {
        storeHourInDay(hourInLong, h);

        currentHour = new HourBlock(hourInLong, h, ParseUser.getCurrentUser().getInt("AppIndex"));

        currentHour.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                // if (getCurrentDay(hourInLong) != null)  Log.d(TAG, );
                if (e != null)  {
                    Log.d(TAG, "currentHour pin error: " + e.getMessage());
                }
            }
        });

        Log.d(TAG, "hour id: " + currentHour.getObjectId());
    }
    private static void storeHourInDay(final long hourInLong, final int h) {
        if (currentHour == null)    return;
        currentHour.saveEventually();

        if (currentDay == null)  return;
        List<String> temp = currentDay.getHourBlocks();
        temp.set(h, currentHour.getObjectId());
        currentDay.setHourBlocks(temp);

        List<Integer>   dayAppLength = currentDay.getAppLength(),
                        hourAppLength = currentHour.getAppLength();
        for (int i = 0; i < 24; ++i)
            dayAppLength.set(i, hourAppLength.get(i) + dayAppLength.get(i));
        currentDay.setAppLength(dayAppLength);

        long localDay = getLocalDay(hourInLong);
        if (localDay != currentDay.getLong("time"))
            newDay(localDay);
    }
    public static int[][] getFirstThreeAppToday() {
        int[][] x = new int[4][2];
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 2; ++j)
                x[i][j] = 0;

        long now = System.currentTimeMillis();
        List<Integer> appLength = getCurrentDay(now).getAppLength(),
                      appLength2 = getCurrentHour(now).getAppLength();
        int size = appLength.size();
        if (size > appLength2.size())   size = appLength2.size();
        for (int i = 0; i < size; ++i)
            appLength.set(i, appLength.get(i) + appLength2.get(i));

        for (int i = 0, s = appLength.size(); i < s; ++i) {
            int length = appLength.get(i);
            boolean flag = true;
            for (int j = 0; j < 3; ++j) {
                if (length > x[j][1]) {
                    x[3][1] += x[2][1];
                    for (int k = 2; k != j; --k) {
                        x[k][0] = x[k - 1][0];
                        x[k][1] = x[k - 1][1];
                    }
                    x[j][0] = i;
                    x[j][1] = length;
                    flag = false;
                    break;
                }
            }
            if (flag)   x[3][1] += length;
        }

        return x;
    }
}
