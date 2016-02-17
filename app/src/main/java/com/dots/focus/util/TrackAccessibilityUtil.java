package com.dots.focus.util;


import android.util.Log;

import com.dots.focus.model.AppInfo;
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

    public static DayBlock getCurrentDay(long time) {
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
        for (int i = 0; i < 4; ++i) {
            x[i][0] = -1;
            x[i][1] = 0;
        }
        long time = getPrevXDayInMilli(day);

        DayBlock dayBlock = getDayBlockByTime(time);

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

    public static int[] weekUsage(long time) {
        int[] x = new int[7];

        List<DayBlock> dayBlocks = getDayBlocksInWeek(time);

        if (dayBlocks == null)  return x;

        for (int i = 0, size = dayBlocks.size(); i < size; ++i) {
            int day = (int) ((dayBlocks.get(i).getLong("time") - time) / aDay);
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
    public static List<List<Integer>> weekAppUsage(long time) {
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

        List<DayBlock> dayBlocks = getDayBlocksInWeek(time);

        if (dayBlocks != null) {
            for (int i = 0, size = dayBlocks.size(); i < size; ++i) {
                int day = (int) ((dayBlocks.get(i).getTime() - time) / aDay);
                List<Integer> appLength2 = dayBlocks.get(i).getAppLength();
                for (int j = 0, n = appLength2.size(); j < n && j < temp; ++j)
                    appLength.set(j, appLength.get(j) + appLength2.get(j));
                appLengths.set(day, appLength2);
            }
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

    public static int[] getCategory(int day) {
        Long time = getPrevXDayInMilli(day);
        int[] data = new int[] {0, 0, 0, 0, 0, 0, 0};
        DayBlock dayBlock = getDayBlockByTime(time);

        int size = FetchAppUtil.getSize();
        List<Integer> appLength = new ArrayList<>(size);
        for (int i = 0; i < size; ++i)
            appLength.add(0);

        if (dayBlock != null) {
            List<Integer> appLength1 = dayBlock.getAppLength();
            for (int i = 0, temp = appLength1.size(); i < temp && i < size; ++i)
                appLength.set(i, appLength.get(i) + appLength1.get(i));
        }

        for (int i = 0; i < size; ++i) {
            AppInfo appInfo = FetchAppUtil.getApp(i);
            if (appInfo == null)    continue;
            int index = getCategoryUnion(appInfo.getCategory());
            if (index != -1)
                data[index] += appLength.get(i);
        }
        return data;
    }

    public static int getCategoryUnion(String category) {
        switch (category) {
            case "Social":
            case "Communication":
                return 0;

            case "Productivity":
            case "Finance":
            case "Business":
                return 1;

            case "Education":
            case "News & Magazines":
            case "Books & Reference":
                return 2;

            case "Weather":
            case "Lifestyle":
            case "Transportation":
            case "Family":
            case "Travel & Local":
            case "Health & Fitness":
            case "Sports":
            case "Shopping":
            case "Medical":
                return 3;

            case "Game":
            case "Comics":
                return 4;

            case "Media & Video":
            case "Music & Audio":
            case "Entertainment":
            case "Photography":
                return 5;

            case "Android Wear":
            case "Libraries & Demo":
            case "Live Wallpaper":
            case "Personalization":
            case "Tools":
            case "Widgets":
                return 6;

            default:
                Log.d(TAG, "No such category: " + category);
                return -1;
        }
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

    public static int dayCategoryClicksLevel(int day) {
        long time = getPrevXDayInMilli(day);
        DayBlock dayBlock = getDayBlockByTime(time);
        int count = 0;
        if (dayBlock != null) {
            List<Integer> clicks = dayBlock.getCategoryClick();
            for (int i = 0, length = clicks.size(); i < length; ++i)
                count += clicks.get(i);
            count = (count + 19) / 20;
            if (count > 3) count = 3;
        }
        return count;
    }

    public static int[] dayCategoryClicks(int day) {
        long time = getPrevXDayInMilli(day);
        DayBlock dayBlock = getDayBlockByTime(time);
        int[] x = new int[7];
        if (dayBlock != null) {
            List<Integer> clicks = dayBlock.getCategoryClick();
            for (int i = 0; i < 7; ++i)
                x[i] = clicks.get(i);
        }
        return x;
    }

    public static int[] dayCategoryClicksInWeek(int week) {
        int[] x = {0, 0, 0, 0, 0, 0, 0};

        Long time = getPrevXWeek(week);
        List<DayBlock> dayBlocks = getDayBlocksInWeek(time);

        if (dayBlocks == null)  return x;

        for (int i = 0, size = dayBlocks.size(); i < size; ++i) {
            int day = (int) ((dayBlocks.get(i).getLong("time") - time) / aDay);
            List<Integer> clicks = dayBlocks.get(i).getCategoryClick();

            for (int j = 0, n = clicks.size(); j < n; ++j)
                x[day] += clicks.get(j);
        }
        return x;
    }

    public static long getPrevXDayInMilli(int day) {
        int offset = getTimeOffset() * anHour;
        return aDay * ((System.currentTimeMillis() + offset) / aDay - day) - offset;
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

    private static DayBlock getDayBlockByTime(Long time) {
        DayBlock dayBlock = null;
        ParseQuery<DayBlock> query = ParseQuery.getQuery(DayBlock.class);
        query.whereEqualTo("time", time);
        query.fromLocalDatastore(); // assume don't delete data from LocalDatastore
        try {
            dayBlock = query.getFirst();
        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }

        return dayBlock;
    }

    private static List<DayBlock> getDayBlocksInWeek(Long time) {
        ArrayList<Long> times = new ArrayList<>();
        times.ensureCapacity(7);
        List<DayBlock> dayBlocks = new ArrayList<>();
        for (int i = 0; i < 7; ++i)
            times.add(time + i * aDay);

        ParseQuery<DayBlock> query = ParseQuery.getQuery(DayBlock.class);
        query.whereContainedIn("time", times);
        query.fromLocalDatastore(); // assume don't delete data from LocalDatastore
        try {
            dayBlocks = query.find();
        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }
        return dayBlocks;
    }
    public static String getDateByMilli(long time) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(time + TrackAccessibilityUtil.getTimeOffset() *
                              TrackAccessibilityUtil.anHour);

      int hr = calendar.get(Calendar
                              .HOUR_OF_DAY);
      int min = calendar.get(Calendar.MINUTE);
      int sec = calendar.get(Calendar.SECOND);
      String timeStr = String.format("%02d:%02d:%02d", hr, min, sec);
      return calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" +
                              calendar.get(Calendar.DAY_OF_MONTH) + "日 " + timeStr;

    }
}
