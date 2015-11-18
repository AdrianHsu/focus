package com.dots.focus.util;


import android.util.Log;

import com.dots.focus.model.DayBlock;
import com.dots.focus.model.HourBlock;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
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

    public static ParseObject getCurrentDay(long time){
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
        else if (localDay != currentDay.getLong("time")) {
            newDay(localDay);
        }
        if (currentDay == null) Log.d(TAG, "return null day QAQ...");
        else Log.d(TAG, "day id: " + currentDay.getObjectId());
        return currentDay;
    }
    public static ParseObject getCurrentHour(long time){
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
        rightNow.setTimeInMillis(1000 * ((time + anHour * getTimeOffset()) / 1000)); // shift to local time
        rightNow.set(rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH),
                rightNow.get(Calendar.DAY_OF_MONTH), 0, 0); // discard hours
        rightNow.setTimeInMillis(rightNow.getTimeInMillis() - anHour * getTimeOffset()); // local day in  standard time
        return rightNow.getTimeInMillis();
    }

    private static void newDay(long dayInLong){
        currentDay = new DayBlock(dayInLong);
        currentDay.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                List<String> hourBlocks = currentDay.getHourBlocks();
                for (int i = 0; i < 24; ++i) {
                    if (!hourBlocks.get(i).equals("")) {
                        ParseQuery<HourBlock> query = ParseQuery.getQuery(HourBlock.class);
                        query.getInBackground(hourBlocks.get(i), new GetCallback<HourBlock>() {
                            @Override
                            public void done(HourBlock hourBlock, ParseException e) {
                                if (hourBlock != null && e == null)
                                    hourBlock.setDayBlock(currentDay.getObjectId());
                            }
                        });
                    }
                }
            }
        });

    }
    private static void newHour(final long hourInLong, final int h){
        currentHour = new HourBlock(hourInLong, h);
        currentHour.put("User", ParseUser.getCurrentUser());
        List<Integer> appLength = new ArrayList<>();
        int size = FetchAppUtil.getSize();
        for(int i = 0; i < size; ++i)
            appLength.add(0);
        currentHour.put("appLength", appLength);
        currentHour.put("time", hourInLong);
        currentHour.put("offset", getTimeOffset());
        currentHour.put("end", false);
        String id = getCurrentDay(hourInLong).getObjectId();
        Log.d(TAG, "id: " + id);
        if (id != null)
            currentHour.put("prev", id);
        currentHour.put("appUsage", new ArrayList<String>());

        currentHour.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                getCurrentDay(hourInLong).put("hourBlocks",
                    getCurrentDay(hourInLong).getList("hourBlocks").set(h,
                        currentHour.getObjectId()));
            }
        });

        Log.d(TAG, "hour id: " + currentHour.getObjectId());
    }
}
