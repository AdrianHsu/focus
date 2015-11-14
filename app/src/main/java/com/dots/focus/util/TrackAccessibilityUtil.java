package com.dots.focus.util;


import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class TrackAccessibilityUtil {
    public static int anHour = 3600000;
    private static ParseObject currentDay = null;
    private static ParseObject currentHour = null;
    private static List<ParseObject> dayList = new ArrayList<>();
    private static List<ParseObject> hourList = new ArrayList<>();
    private static String TAG = "TrackAccessibilityUtil";

    public static long getTimeInMilli() {
        return TimeZone.getDefault().getOffset(System.currentTimeMillis());
    }
    public static ParseObject getCurrentDay(long time){
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTimeInMillis(1000 * (time / 1000));
        rightNow.set(rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH), rightNow.get(Calendar.DAY_OF_MONTH), 0, 0);

        if(currentDay == null){
            ParseQuery<ParseObject> query = ParseQuery.getQuery("DayBlock");
            query.whereEqualTo("time", rightNow.getTimeInMillis());
            query.fromLocalDatastore();
            try{
                currentDay = query.getFirst();
            }catch(ParseException e){
                Log.d(TAG, "getCurrentDay in local database went wrong.");
            }
            if (currentDay == null)
                newDay(rightNow.getTimeInMillis());
        }
        else if (rightNow.getTimeInMillis() != currentDay.getLong("time")) {
            dayList.add(currentDay);
            newDay(rightNow.getTimeInMillis());
        }


        return currentDay;
    }
    public static ParseObject getCurrentHour(long time){
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTimeInMillis(1000 * (time / 1000));
        Log.d(TAG, "Year: " + rightNow.get(Calendar.YEAR) + " Month: " + rightNow.get(Calendar.MONTH)
            + " Day: " + rightNow.get(Calendar.DAY_OF_MONTH) + " Hour: " + rightNow.get(Calendar.HOUR_OF_DAY)
            + " Minute: "+ rightNow.get(Calendar.MINUTE) + " OFFSET: " + rightNow.get(Calendar.DST_OFFSET)
        );

        time = anHour * (time / anHour);
        if (currentHour == null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("HourBlock");
            query.whereEqualTo("time", time);
            query.fromLocalDatastore();
            try{
                currentHour = query.getFirst();
            }catch(ParseException e){
                Log.d(TAG, "getCurrentHour in local database went wrong.");
            }
            if (currentHour == null)
                newHour(time);
        }
        else if (time != currentHour.getLong("time")){
            hourList.add(currentHour);
            newHour(time);
        }


        return currentHour;
    }
    private static void newDay(long dayInLong){
        currentDay = new ParseObject("DayBlock");
        currentDay.put("User", ParseUser.getCurrentUser());
        List<Integer> appLength = new ArrayList<>();
        int size = OverviewUtil.getSize();
        for(int i = 0; i < size; ++i)
            appLength.add(0);
        currentDay.put("appLength", appLength);
        currentDay.put("time", dayInLong);
        currentDay.put("end", false);
        currentDay.put("hourBlocks", new ArrayList<ParseObject>());
    }
    private static void newHour(long hourInLong){
        currentHour = new ParseObject("DayBlock");
        currentHour.put("User", ParseUser.getCurrentUser());
        List<Integer> appLength = new ArrayList<>();
        int size = OverviewUtil.getSize();
        for(int i = 0; i < size; ++i)
            appLength.add(0);
        currentHour.put("appLength", appLength);
        currentHour.put("time", hourInLong);
        currentHour.put("end", false);
        currentHour.put("prev", getCurrentDay(hourInLong));
        currentHour.put("appUsage", new ArrayList<ParseObject>());
    }

}
