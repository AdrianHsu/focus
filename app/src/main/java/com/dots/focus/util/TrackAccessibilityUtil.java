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
    public static long aDay = 86400000;
    private static DayBlock currentDay = null;
    private static HourBlock currentHour = null;
    private static String TAG = "TrackAccessibilityUtil";

    public static int getTimeOffset() {
        return TimeZone.getDefault().getOffset(System.currentTimeMillis()) / anHour;
    }

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
        else if (localDay != currentDay.getTime())
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
        else if (time != currentHour.getTime()) {
            newHour(time, theHour);
        }
        if (currentHour == null)  Log.d(TAG, "return null hour QAQ...");
        return currentHour;
    }

    //helper functions
    private static long getLocalDay(long time) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTimeInMillis(aDay * ((time + anHour * getTimeOffset()) / aDay));
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
        currentDay.pinInBackground();
    }
    public static void newHour(final long hourInLong, final int h) {
        storeHourInDay(hourInLong, h);

        currentHour = new HourBlock(hourInLong, h, ParseUser.getCurrentUser().getInt("AppIndex"));
        currentHour.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    Log.d(TAG, "currentHour pin error: " + e.getMessage());
            }
        });

        Log.d(TAG, "hour id: " + currentHour.getObjectId());
        currentHour.pinInBackground();
    }
    private static void storeHourInDay(final long hourInLong, final int h) {
        if (currentHour == null)    return;
        if (currentDay != null)
            currentHour.setDayBlock(currentDay.getObjectId());
        currentHour.saveEventually();

        if (currentDay == null)  return;
        List<String> temp = currentDay.getHourBlocks();
        temp.set(h, currentHour.getObjectId());
        currentDay.setHourBlocks(temp);
        currentDay.saveEventually();

        long localDay = getLocalDay(hourInLong);
        if (localDay != currentDay.getLong("time"))
            newDay(localDay);
    }
    public static int[][] getDayFirstThreeApp(int day) {
        int[][] x = new int[4][2];
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 2; ++j)
                x[i][j] = 0;
        int offset = getTimeOffset();
        long time = aDay * ((System.currentTimeMillis() + offset * anHour) / aDay - day) - offset *
                anHour;
        Log.d(TAG, "time: " + time + ", currentDay: " + getCurrentDay(System.currentTimeMillis())
                .getTime());

        DayBlock dayBlock = null;
        ParseQuery<DayBlock> query = ParseQuery.getQuery(DayBlock.class);
        query.whereEqualTo("time", time);
        query.fromLocalDatastore(); // assume don't delete data from LocalDatastore
        try {
            dayBlock = query.getFirst();
        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }
        if (dayBlock != null) {
            List<Integer> appLength = dayBlock.getAppLength();
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
                if (flag) x[3][1] += length;
            }
            Log.d(TAG, "data:");
            for (int i = 0; i < 4; ++i)
                Log.d(TAG, i + ", " + x[i][0] + ", " + x[i][1]);
        }

        return x;
    }

    public static int[] weekUsage(long time0) {
        int[] x = new int[7];
        ArrayList<Long> times = new ArrayList<>();
        times.ensureCapacity(7);
        List<DayBlock> dayBlocks = new ArrayList<>();
        for (int i = 0; i < 7; ++i) {
            x[i] = 0;
            times.add(time0 + i * aDay);
        }

        ParseQuery<DayBlock> query = ParseQuery.getQuery(DayBlock.class);
        query.whereContainedIn("time", times);
        query.fromLocalDatastore(); // assume don't delete data from LocalDatastore
        try {
            dayBlocks = query.find();
        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }

        for (int i = 0, size = dayBlocks.size(); i < size; ++i) {
            int day = (int) ((dayBlocks.get(i).getLong("time") - time0) / aDay);
            List<Integer> appLength = dayBlocks.get(i).getAppLength();

            for (int j = 0, n = appLength.size(); j < n; ++j)
                x[day] += appLength.get(j);
        }
        return x;
    }

    public static int[] dayUsage(long time0) {
        int[] x = new int[24];
        ArrayList<Long> times = new ArrayList<>();
        times.ensureCapacity(24);
        for (int i = 0; i < 24; ++i) {
            x[i] = 0;
            times.add(time0 + i * anHour);
        }
        List<HourBlock> hourBlocks = new ArrayList<>();

        ParseQuery<HourBlock> query = ParseQuery.getQuery(HourBlock.class);
        query.whereContainedIn("time", times);
        query.fromLocalDatastore(); // assume don't delete data from LocalDatastore
        try {
            hourBlocks = query.find();
        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }

        for (int i = 0, size = hourBlocks.size(); i < size; ++i) {
            int hour = (int) ((hourBlocks.get(i).getLong("time") - time0) / anHour);
            List<Integer> appLength = hourBlocks.get(i).getAppLength();

            for (int j = 0, n = appLength.size(); j < n; ++j)
                x[hour] += appLength.get(j);
        }

        return x;
    }

    public static List<List<Integer>> hourAppLength(long time0) {
        ArrayList<List<Integer>> x = new ArrayList<>();

        ArrayList<Long> times = new ArrayList<>();
        times.ensureCapacity(24);
        for (int i = 0; i < 24; ++i) {
//            x.set(i, new ArrayList<Integer>()); //edit by Adrian
            x.add(i, new ArrayList<Integer>());
            times.add(time0 + i * anHour);
        }
        List<HourBlock> hourBlocks = new ArrayList<>();

        ParseQuery<HourBlock> query = ParseQuery.getQuery(HourBlock.class);
        query.whereContainedIn("time", times);
        query.fromLocalDatastore(); // assume don't delete data from LocalDatastore
        try {
            hourBlocks = query.find();
        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }

        for (int i = 0, size = hourBlocks.size(); i < size; ++i) {
            int hour = (int) ((hourBlocks.get(i).getLong("time") - time0) / anHour);
            List<Integer> appLength = hourBlocks.get(i).getAppLength();
            x.set(hour, appLength);
        }

        return x;
    }
    public static int[] getFirstThree(List<Integer> appLength) {
        int[] x = new int[3], value = new int[3];
        for (int i = 0; i < 3; ++i) {
            x[i] = -1;   value[i] = 0;
        }
        for (int i = 0, size = appLength.size(); i < size; ++i) {
            int length = appLength.get(i);
            for (int j = 0; j < 3; ++j) {
                if (length > value[j]) {
                    for (int k = 2; k != j; --k) {
                        x[k] = x[k - 1];
                        value[k] = value[k - 1];
                    }
                    x[j] = i;
                    value[j] = length;
                  break;
                }
            }
        }
        return x;
    }
    public static List<List<Integer>> weekAppUsage(long time0) {
        List<List<Integer>> appLengths = new ArrayList<>(8);
        int temp = FetchAppUtil.getSize();

        for (int i = 0; i < 7; ++i) {
            List<Integer> tempLength = new ArrayList<>(temp);
            for (int j = 0; j < temp; ++j)
                tempLength.add(0);
            appLengths.add(tempLength);
        }
        List<Integer> appLength = new ArrayList<>(temp);
        for (int i = 0; i < temp; ++i)
            appLength.add(0);

        ArrayList<Long> times = new ArrayList<>();
        times.ensureCapacity(7);
        List<DayBlock> dayBlocks = new ArrayList<>();
        for (int i = 0; i < 7; ++i)
            times.add(time0 + i * aDay);

        ParseQuery<DayBlock> query = ParseQuery.getQuery(DayBlock.class);
        query.whereContainedIn("time", times);
        query.fromLocalDatastore(); // assume don't delete data from LocalDatastore
        try {
            dayBlocks = query.find();
        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }
        for (int i = 0, size = dayBlocks.size(); i < size; ++i) {
            int day = (int)((dayBlocks.get(i).getTime() - time0) / aDay);
            List<Integer> appLength2 = dayBlocks.get(i).getAppLength();
            for (int j = 0, n = appLength2.size(); j < n && j < temp; ++j)
                appLength.set(j, appLength.get(j) + appLength2.get(j));
            appLengths.set(day, appLength2);
        }
        appLengths.add(appLength);

        return appLengths;

    }
    public static int[] timeBox(long time) { // time: start of the week
        int[] x = new int[8];
        for (int i = 0; i < 7; ++i)
          x[i] = -1;
        x[7] = 0;

        List<DayBlock> dayBlocks = new ArrayList<>();
        ParseQuery<DayBlock> query = ParseQuery.getQuery(DayBlock.class);
        query.fromLocalDatastore(); // assume don't delete data from LocalDatastore
        try {
            dayBlocks = query.find();
        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }

        int maxDay = 0, minDay = 0;

        for (int i = 0, size = dayBlocks.size(); i < size; ++i) {
            int count = 0, day = (int)((dayBlocks.get(i).getTime() - time) / 86400000);
            List<Integer> appLength = dayBlocks.get(i).getAppLength();
            for (int j = 0, n = appLength.size(); j < n; ++j)
                count += appLength.get(j);
            if (day < 7 && day > -1)    x[day] = count;
            x[7] += count;

            if (i == 0) {
                maxDay = day;   minDay = day;
            }
            else {
                if (day > maxDay)   maxDay = day;
                if (day < minDay)   minDay = day;
            }
        }
//        x[7] -= 2 * anHour * (maxDay - minDay + 1);
        x[7] = 2 * (anHour / 1000) * (maxDay - minDay + 1) - x[7];

        return x;
    }

    public static int[] getCategory() {
        int[] data = new int[] {0, 0, 0, 0, 0, 0, 0};
        List<DayBlock> dayBlocks = new ArrayList<>();
        ParseQuery<DayBlock> query = ParseQuery.getQuery(DayBlock.class);
        query.fromLocalDatastore(); // assume don't delete data from LocalDatastore
        try {
            dayBlocks = query.find();
        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }

        int size = FetchAppUtil.getSize();
        List<Integer> appLength = new ArrayList<>(size);
        for (int i = 0; i < size; ++i)
            appLength.add(0);

        for (int i = 0, n = dayBlocks.size(); i < n; ++i) {
            List<Integer> appLength1 = dayBlocks.get(i).getAppLength();
            for (int j = 0, temp = appLength1.size(); j < temp && j < size; ++j)
                appLength.set(j, appLength.get(j) + appLength1.get(j));
        }

        for (int i = 0; i < size; ++i) {
            switch (FetchAppUtil.getApp(i).getCategory()) {
                case "Social":
                case "Communication":
                    data[0] += appLength.get(i);
                    break;
                case "Productivity":
                case "Finance":
                case "Business":
                    data[1] += appLength.get(i);
                    break;

                case "Education":
                case "News & Magazines":
                case "Books & Reference":
                    data[2] += appLength.get(i);
                    break;

                case "Weather":
                case "Lifestyle":
                case "Transportation":
                case "Family":
                case "Travel & Local":
                case "Health & Fitness":
                case "Sports":
                case "Shopping":
                case "Medical":
                    data[3] += appLength.get(i);
                    break;

                case "Game":
                case "Comics":
                    data[4] += appLength.get(i);
                    break;

                case "Media & Video":
                case "Music & Audio":
                case "Entertainment":
                case "Photography":
                    data[5] += appLength.get(i);
                    break;

                case "Android Wear":
                case "Libraries & Demo":
                case "Live Wallpaper":
                case "Personalization":
                case "Tools":
                case "Widgets":
                    data[6] += appLength.get(i);
                    break;

                default:
                    Log.d(TAG, "No such category: " + FetchAppUtil.getApp(i).getCategory());
                    break;
            }
        }
        return data;
    }

    public static long getPrevXWeek(int week) { // 0: current week
      Calendar calendar = Calendar.getInstance();
        long time = System.currentTimeMillis() + getTimeOffset() * anHour;
        time = aDay * (time / aDay);
        calendar.setTimeInMillis(time);
        Log.d("TrackAccessibilityUtil", "calendar.get(Calendar.DAY_OF_WEEK): " + calendar.get
                (Calendar.DAY_OF_WEEK));
        return (time - getTimeOffset() * anHour - 7 * aDay * week
                - (calendar.get(Calendar.DAY_OF_WEEK) - 1) * aDay);
    }

    public static String[] weekString(long time) {
        String[] theWeek = new String[7],
                weekStr = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        Calendar calendar = Calendar.getInstance();
        time += getTimeOffset() * anHour;
        calendar.setTimeInMillis(time);
        calendar.setTimeInMillis(time - (calendar.get(Calendar.DAY_OF_WEEK) - 1) * aDay);

        for (int i = 0; i < 7; ++i) {
            theWeek[i] = "" + calendar.get(Calendar.YEAR) + "/" +
                                    (calendar.get(Calendar.MONTH) + 1)  + "/" +
                            calendar.get(Calendar.DAY_OF_MONTH) + " (" + weekStr[i] + ")";
            calendar.setTimeInMillis(calendar.getTimeInMillis() + aDay);
        }
        return theWeek;
    }

    public static String weekPeriodString(int week) { // 0: this week, 1: last week
        String periodString = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getPrevXWeek(week));

        periodString += (calendar.get(Calendar.MONTH) + 1) + "/" +
                calendar.get(Calendar.DAY_OF_MONTH) + " - ";
        calendar.setTimeInMillis(calendar.getTimeInMillis() + aDay * 6);
        periodString += (calendar.get(Calendar.MONTH) + 1) + "/" +
                calendar.get(Calendar.DAY_OF_MONTH);

        return periodString;
    }

    public static String dayString(int day) { // 0: today, 1: yesterday
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() - day * aDay);

        return  ("" + calendar.get(Calendar.YEAR) + "年" +
                (calendar.get(Calendar.MONTH) + 1)  + "月" +
                calendar.get(Calendar.DAY_OF_MONTH) + "日");
    }
}
