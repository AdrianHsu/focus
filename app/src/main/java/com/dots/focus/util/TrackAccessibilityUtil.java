package com.dots.focus.util;

import android.content.Intent;
import android.util.Log;

import com.dots.focus.model.AppInfo;
import com.dots.focus.service.GetAppsService;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Harvey Yang on 2015/9/30.
 */
public class TrackAccessibilityUtil {
    public static int anHour = 3600000;
    private static ParseObject currentDay = null;
    private static ParseObject currentHour = null;
    private static List<ParseObject> dayList = new ArrayList<>();
    private static List<ParseObject> hourList = new ArrayList<>();

    public static long getTimeInMilli(){
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
            }catch(ParseException e){}
            if (currentDay == null)
                newDay(rightNow.getTimeInMillis());
        }
        else if(rightNow.getTimeInMillis() != currentDay.getLong("time"))
            newDay(rightNow.getTimeInMillis());

        return currentDay;
    }
    public static ParseObject getCurrentHour(long time){
        time = anHour * (time / anHour);
        if(currentHour == null){
            ParseQuery<ParseObject> query = ParseQuery.getQuery("HourBlock");
            query.whereEqualTo("time", time);
            query.fromLocalDatastore();
            try{
                currentHour = query.getFirst();
            }catch(ParseException e){}
            if (currentHour == null)
                newHour(time);
        }
        else if(time != currentHour.getLong("time"))
            newHour(time);

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
    }

}
