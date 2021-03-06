package com.dots.focus.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.dots.focus.model.AppInfo;
import com.dots.focus.model.DayBlock;
import com.dots.focus.model.HourBlock;
import com.dots.focus.ui.IdleSettingsActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;


public class TrackAccessibilityUtil {
    public static int anHour = 3600000;
    public static long aDay = 86400000;
    private static DayBlock currentDay = null;
    private static HourBlock currentHour = null;
    private static String TAG = "TrackAccessibilityUtil";
    private static List<Integer> localIdle = null;

    public static String[] characters = new String[]{"社交高手", "理財專家", "知識王", "生活達人", "遊戲王",
            "影音高手", "交際咖", "商業土豪", "兩腳書櫥", "享樂主義", "遊戲宅宅", "影音狂人"};
    public static String[] categories = new String[]{"社交行為", "理財工作", "汲取新知", "生活資訊",
            "遊戲動漫", "影音娛樂"};

    private static List<Long> NotExistDayBlocks = new ArrayList<>();
    private static List<Long> NotExistHourBlocks = new ArrayList<>();


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
        } else if (localDay != currentDay.getTime())
            newDay(localDay);

        if (currentDay == null) Log.d(TAG, "return null day QAQ...");
        else Log.d(TAG, "day id: " + currentDay.getObjectId());
        return currentDay;
    }

    public static HourBlock getCurrentHour(long time) {
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
        } else if (time != currentHour.getTime()) {
            newHour(time, theHour);
        }
        if (currentHour == null) Log.d(TAG, "return null hour QAQ...");
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
        NotExistDayBlocks.remove(dayInLong);
        currentDay.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d(TAG, "currentDay pin error: " + e.getMessage());
                }
                String objectId = currentDay.getObjectId();
                if (objectId != null)
                    Log.d(TAG, "new DayBlock's objectId: " + objectId);
//                List<String> hourBlocks = currentDay.getHourBlocks();
//                if (hourBlocks == null) {
//                    Log.d(TAG, "hourBlocks is null.");
//                    return;
//                }
//                if (hourBlocks.size() < 24) {
//                    Log.d(TAG, "hourBlocks.size(): " + hourBlocks.size());
//                    return;
//                }
//                for (int i = 0; i < 24; ++i) {
//                    if (!hourBlocks.get(i).equals("")) {
//                        ParseQuery<HourBlock> query = ParseQuery.getQuery(HourBlock.class);
//                        query.getInBackground(hourBlocks.get(i), new GetCallback<HourBlock>() {
//                            @Override
//                            public void done(HourBlock hourBlock, ParseException e) {
//                                if (hourBlock != null && e == null)
//                                    hourBlock.setDayBlock(currentDay.getObjectId());
//                            }
//                        });
//                    }
//                }
            }
        });
        currentDay.pinInBackground();
    }

    public static void newHour(final long hourInLong, final int h) {
        storeSavedTime();
        storeHourInDay(hourInLong, h);

        currentHour = new HourBlock(hourInLong, h, ParseUser.getCurrentUser().getInt("AppIndex"));
        NotExistHourBlocks.remove(hourInLong);
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
        if (currentHour == null) return;
        if (currentDay != null) {
            String objectId = currentDay.getObjectId();
            if (objectId != null)
                currentHour.setDayBlock(objectId);
            else {
                Log.d(TAG, "currentDay.getObjectId() is null...\n" +
                        "day time: " + currentDay.getTime() + "\n" +
                        "hour time: " + currentHour.getTime());
            }
        }
        currentHour.saveEventually();

        if (currentDay == null) return;
        List<String> temp = currentDay.getHourBlocks();
        temp.set(h, currentHour.getObjectId());
        currentDay.setHourBlocks(temp);
        currentDay.saveEventually();

        long localDay = getLocalDay(hourInLong);
        if (localDay != currentDay.getLong("time"))
            newDay(localDay);
    }

    private static void storeSavedTime() {
        Integer goal = SettingsUtil.getInt("goal");
        if (goal == 0) goal = 120;
        goal *= 60;
        List<Integer> appLength = currentDay.getAppLength();
        for (int i = 0, length = appLength.size(); i < length; ++i)
            goal -= appLength.get(i);

        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put("SavedTotalTime", currentUser.getInt("SavedTotalTime") + goal);
    }

    public static int[][] getDayFirstThreeApp(int day, Context context) {
        initializeLocalIdle();

        int[][] x = new int[4][2];
        for (int i = 0; i < 4; ++i) {
            x[i][0] = -1;
            x[i][1] = 0;
        }
        long time = getPrevXDayInMilli(day);
        Log.d(TAG, "time: " + time);

        DayBlock dayBlock = getDayBlockByTime(time, checkNetworkAvailable(context));

        if (dayBlock != null) {
            List<Integer> appLength = dayBlock.getAppLength();
            Log.d(TAG, "appLength: " + appLength);
            for (int i = 0, s = appLength.size(); i < s; ++i) {
                if (inIdle(i))  continue;
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

    public static int[] weekUsage(long time, Context context) {
        initializeLocalIdle();

        int[] x = new int[7];
        List<DayBlock> dayBlocks = getDayBlocksInWeek(time, checkNetworkAvailable(context));

        if (dayBlocks == null) return x;

        for (int i = 0, size = dayBlocks.size(); i < size; ++i) {
            if (inIdle(i))  continue;
            int day = (int) ((dayBlocks.get(i).getLong("time") - time) / aDay);
            List<Integer> appLength = dayBlocks.get(i).getAppLength();

            for (int j = 0, n = appLength.size(); j < n; ++j)
                x[day] += appLength.get(j);
        }
        for (int i = 0; i < 7; ++i)
            Log.d(TAG, "weekUsage " + i + "th: " + x[i]);
        return x;
    }

    public static int getTotalInArray(int[] data) {
        int count = 0;
        for (int x : data) {
            count += x;
        }
        return count;
    }

    public static int[] dayUsage(long time, Context context) {
        initializeLocalIdle();

        int[] x = new int[24];

        List<HourBlock> hourBlocks = getHourBlocksInDay(time, checkNetworkAvailable(context));

        if (hourBlocks == null) return x;
        for (int i = 0, size = hourBlocks.size(); i < size; ++i) {
            if (inIdle(i))  continue;
            int hour = (int) ((hourBlocks.get(i).getLong("time") - time) / anHour);
            List<Integer> appLength = hourBlocks.get(i).getAppLength();

            for (int j = 0, n = appLength.size(); j < n; ++j)
                x[hour] += appLength.get(j);
        }

        return x;
    }

    public static List<List<Integer>> hourAppLength(long time, Context context) {
        initializeLocalIdle();

        ArrayList<List<Integer>> x = new ArrayList<>(24);
        for (int i = 0; i < 24; ++i) {
            x.add(new ArrayList<Integer>());
        }
        List<HourBlock> hourBlocks = getHourBlocksInDay(time, checkNetworkAvailable(context));

        if (hourBlocks == null) return x;

        for (int i = 0, size = hourBlocks.size(); i < size; ++i) {
            if (inIdle(i))  continue;
            int hour = (int) ((hourBlocks.get(i).getLong("time") - time) / anHour);
            List<Integer> appLength = hourBlocks.get(i).getAppLength();
            if (hour < 0 || hour >= x.size())   continue;
            x.set(hour, appLength);
        }

        return x;
    }

    public static int[] getFirstThree(List<Integer> appLength) {
        initializeLocalIdle();

        int[] x = new int[3], value = new int[3];
        for (int i = 0; i < 3; ++i) {
            x[i] = -1;
            value[i] = 0;
        }
        for (int i = 0, size = appLength.size(); i < size; ++i) {
            if (inIdle(i))  continue;
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

    public static List<List<Integer>> weekAppUsage(long time, Context context) {
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

        List<DayBlock> dayBlocks = getDayBlocksInWeek(time, checkNetworkAvailable(context));

        if (dayBlocks != null) {
            for (int i = 0, size = dayBlocks.size(); i < size; ++i) {
                initializeLocalIdle();
                int day = (int) ((dayBlocks.get(i).getTime() - time) / aDay);
                List<Integer> appLength2 = dayBlocks.get(i).getAppLength();
                for (int j = 0, n = appLength2.size(); j < n && j < temp; ++j) {
                    if (inIdle(j))  continue;
                    appLength.set(j, appLength.get(j) + appLength2.get(j));
                }
                appLengths.set(day, appLength2);
            }
        }
        appLengths.add(appLength);

        return appLengths;

    }

    public static int[] timeBox(long time, Context context) {
        int[] x = new int[8];
        for (int i = 0; i < 7; ++i)
            x[i] = -1;
        x[7] = 0;

        List<DayBlock> dayBlocks = new ArrayList<>();
        ParseQuery<DayBlock> query = ParseQuery.getQuery(DayBlock.class);
        if (!checkNetworkAvailable(context))
            query.fromLocalDatastore();
        try {
            dayBlocks = query.find();
        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }

        int maxDay = 0, minDay = 0;

        for (int i = 0, size = dayBlocks.size(); i < size; ++i) {
            initializeLocalIdle();
            int count = 0, day = (int) ((dayBlocks.get(i).getTime() - time) / 86400000);
            List<Integer> appLength = dayBlocks.get(i).getAppLength();
            for (int j = 0, n = appLength.size(); j < n; ++j) {
                if (inIdle(j))  continue;
                count += appLength.get(j);
            }
            if (day < 7 && day > -1) x[day] = count;
            x[7] += count;

            if (i == 0) {
                maxDay = day;
                minDay = day;
            } else {
                if (day > maxDay) maxDay = day;
                if (day < minDay) minDay = day;
            }
        }
//        x[7] -= 2 * anHour * (maxDay - minDay + 1);
        double goalHour = SettingsUtil.getInt("goal") / 60;

        x[7] = (int) (goalHour * (anHour / 1000) * (maxDay - minDay + 1) - x[7]);

        return x;
    }

    public static int[] getCategory(int day, Context context) {
        initializeLocalIdle();

        Long time = getPrevXDayInMilli(day);
        int[] data = new int[]{0, 0, 0, 0, 0, 0, 0};
        DayBlock dayBlock = getDayBlockByTime(time, checkNetworkAvailable(context));

        int size = FetchAppUtil.getSize();
        List<Integer> appLength = new ArrayList<>();

        if (dayBlock != null)
            appLength = dayBlock.getAppLength();

        int size1 = appLength.size();
        if (size > size1) size = size1;
        for (int i = 0; i < size; ++i) {
            if (inIdle(i))  continue;
            AppInfo appInfo = FetchAppUtil.getApp(i);
            if (appInfo == null) continue;
            int index = getCategoryUnion(appInfo.getCategory());
            if (index != -1)
                data[index] += appLength.get(i);
        }
        return data;
    }

    public static int[] getCategoryClicks(int day, Context context) {
        initializeLocalIdle();

        Long time = getPrevXDayInMilli(day);
        int[] data = new int[]{0, 0, 0, 0, 0, 0, 0};
        int[] data2 = data.clone();
        DayBlock dayBlock = getDayBlockByTime(time, checkNetworkAvailable(context));

        int size = FetchAppUtil.getSize();
        List<Integer> clicks = new ArrayList<>();
        List<Integer> appLength = new ArrayList<>();

        if (dayBlock != null) {
            appLength = dayBlock.getAppLength();
            clicks = dayBlock.getCategoryClick();
        }

        int size1 = appLength.size(), size2 = clicks.size();
        if (size > size1 && size > size2) {
            if (size1 > size2) size = size1;
            else size = size2;
        }

        for (int i = 0; i < size; ++i) {
            if (inIdle(i))  continue;
            AppInfo appInfo = FetchAppUtil.getApp(i);
            if (appInfo == null) continue;
            int index = getCategoryUnion(appInfo.getCategory());
            if (index != -1) {
                if (i < size1)
                    data[index] += appLength.get(i);
                if (i < size2)
                    data2[index] += clicks.get(i);
            }
        }

        int maxIndex = 0, maxIndex2 = 0;
        for (int i = 1; i < 6; ++i) {
            if (data[i] > data[maxIndex])
                maxIndex = i;
            if (data2[i] > data2[maxIndex2])
                maxIndex2 = i;
        }

        if (data2[maxIndex] >= 60) {
            maxIndex += 6;
        }

        return new int[]{maxIndex, maxIndex2, data[maxIndex]};
    }

    public static String descriptionOfCharacter(int[] indexes) {
        if (indexes.length != 2)
            Log.d(TAG, "descriptionOfCharacter: error length: " + indexes.length);
        String string = "您是一個" + characters[indexes[0]] + "！（單日應用軟體點擊次數 60 次以";
        if (indexes[2] >= 60)
            string += "上";
        else
            string += "下";
        return string + "），" +
                categories[indexes[1]] + " 類別軟體佔總時間比例最長）";
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
                    (calendar.get(Calendar.MONTH) + 1) + "/" +
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

        return ("" + calendar.get(Calendar.YEAR) + "年" +
                (calendar.get(Calendar.MONTH) + 1) + "月" +
                calendar.get(Calendar.DAY_OF_MONTH) + "日");
    }

    public static int dayCategoryClicksLevel(int day, Context context) {
        initializeLocalIdle();

        long time = getPrevXDayInMilli(day);
        DayBlock dayBlock = getDayBlockByTime(time, checkNetworkAvailable(context));
        int count = 0;
        if (dayBlock != null) {
            List<Integer> clicks = dayBlock.getCategoryClick();
            for (int i = 0, length = clicks.size(); i < length; ++i) {
                if (inIdle(i))  continue;
                count += clicks.get(i);
            }
            count /= 20;
            if (count > 3) count = 3;
        }
        return count;
    }

    public static int[] dayCategoryClicks(int day, Context context) {
        long time = getPrevXDayInMilli(day);
        DayBlock dayBlock = getDayBlockByTime(time, checkNetworkAvailable(context));
        int[] x = new int[7];
        if (dayBlock != null) {
            List<Integer> clicks = dayBlock.getCategoryClick();
            for (int i = 0; i < 7; ++i)
                x[i] = clicks.get(i);
        }
        return x;
    }

    public static int[] dayCategoryClicksInWeek(int week, Context context) {
        int[] x = new int[]{0, 0, 0, 0, 0, 0, 0};

        Long time = getPrevXWeek(week);
        List<DayBlock> dayBlocks = getDayBlocksInWeek(time, checkNetworkAvailable(context));

        if (dayBlocks == null) return x;

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

    public static int[] getUsageValuation(int[] data) { // input week clicks
        if (data.length != 7)
            Log.d(TAG, "getUsageValuation: length is not 7: " + data.length);
        int[] x = new int[]{0, 0, 0};
        for (int click : data) {
            if (click >= 60)
                ++x[0];
            else if (click >= 40)
                ++x[1];
            else if (click >= 20)
                ++x[2];
        }
        return x; // 重度、中度、輕度
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

    private static DayBlock getDayBlockByTime(Long time, boolean networkAvailable) {
        Log.d(TAG, "NotExistDayBlocks: " + NotExistDayBlocks);
        if (NotExistDayBlocks.contains(time))   return null;
        DayBlock dayBlock = null;
        ParseQuery<DayBlock> query = ParseQuery.getQuery(DayBlock.class);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)    return null;
        query.whereEqualTo("User", currentUser);
        query.whereEqualTo("time", time);
        query.fromLocalDatastore(); // assume don't delete data from LocalDatastore
        try {
            dayBlock = query.getFirst();
        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }
        if (dayBlock == null && networkAvailable) {
            ParseQuery<DayBlock> query2 = ParseQuery.getQuery(DayBlock.class);
            query2.whereEqualTo("time", time);
            try {
                dayBlock = query2.getFirst();
            } catch (ParseException e) {
                Log.d(TAG, e.getMessage());
            }
            if (dayBlock != null)
                dayBlock.pinInBackground();
            else
                NotExistDayBlocks.add(time);
        }

        return dayBlock;
    }

    private static List<HourBlock> getHourBlocksInDay(Long time, boolean networkAvailable) {
        ArrayList<Long> times = new ArrayList<>();
        times.ensureCapacity(24);
        List<HourBlock> hourBlocks = new ArrayList<>();
        for (int i = 0; i < 24; ++i) {
            if (!NotExistHourBlocks.contains(time + i * anHour))
                times.add(time + i * anHour);
        }
        Log.d(TAG, "NotExistHourBlocks: " + NotExistHourBlocks);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)    return hourBlocks;
        ParseQuery<HourBlock> query = ParseQuery.getQuery(HourBlock.class);
        query.whereEqualTo("User", currentUser);
        query.whereContainedIn("time", times);
        query.fromLocalDatastore(); // assume don't delete data from LocalDatastore
        try {
            hourBlocks = query.find();
        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }

        if (!networkAvailable)   return hourBlocks;
        // search cloud's datastore
        if (hourBlocks != null) {
            for (int i = 0, size = hourBlocks.size(); i < size; ++i)
                times.remove(hourBlocks.get(i).getTime());
        } else {
            hourBlocks = new ArrayList<>();
        }

        if (!times.isEmpty()) {
            List<HourBlock> hourBlocksOnCloud = new ArrayList<>();
            ParseQuery<HourBlock> query2 = ParseQuery.getQuery(HourBlock.class);
            query2.whereEqualTo("User", currentUser);
            query2.whereContainedIn("time", times);
            try {
                hourBlocksOnCloud = query2.find();
            } catch (ParseException e) {
                Log.d(TAG, e.getMessage());
            }
            if (hourBlocksOnCloud != null) {
                ParseObject.saveAllInBackground(hourBlocksOnCloud);
                hourBlocks.addAll(hourBlocksOnCloud);
                for (int i = 0, size = hourBlocksOnCloud.size(); i < size; ++i)
                    times.remove(hourBlocksOnCloud.get(i).getTime());
            }
            NotExistHourBlocks.addAll(times);
        }
        return hourBlocks;
    }

    private static List<DayBlock> getDayBlocksInWeek(Long time, boolean networkAvailable) {
        ArrayList<Long> times = new ArrayList<>();
        times.ensureCapacity(7);
        List<DayBlock> dayBlocks = new ArrayList<>();
        for (int i = 0; i < 7; ++i) {
            if (!NotExistDayBlocks.contains(time + i * aDay))
                times.add(time + i * aDay);
        }
        Log.d(TAG, "NotExistDayBlocks: " + NotExistDayBlocks);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)    return dayBlocks;
        ParseQuery<DayBlock> query = ParseQuery.getQuery(DayBlock.class);
        query.whereEqualTo("User", currentUser);
        query.whereContainedIn("time", times);
        query.fromLocalDatastore(); // assume don't delete data from LocalDatastore
        try {
            dayBlocks = query.find();
        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }

        if (!networkAvailable)   return dayBlocks;
        // search cloud's datastore
        if (dayBlocks != null) {
            for (int i = 0, size = dayBlocks.size(); i < size; ++i)
                times.remove(dayBlocks.get(i).getTime());
        } else {
            dayBlocks = new ArrayList<>();
        }

        if (!times.isEmpty()) {
            List<DayBlock> dayBlocksOnCloud = new ArrayList<>();
            ParseQuery<DayBlock> query2 = ParseQuery.getQuery(DayBlock.class);
            query2.whereEqualTo("User", currentUser);
            query2.whereContainedIn("time", times);
            try {
                dayBlocksOnCloud = query2.find();
            } catch (ParseException e) {
                Log.d(TAG, e.getMessage());
            }
            if (dayBlocksOnCloud != null) {
                ParseObject.saveAllInBackground(dayBlocksOnCloud);
                dayBlocks.addAll(dayBlocksOnCloud);
                for (int i = 0, size = dayBlocksOnCloud.size(); i < size; ++i)
                    times.remove(dayBlocksOnCloud.get(i).getTime());
            }
            NotExistDayBlocks.addAll(times);
        }
        return dayBlocks;
    }

    public static String getDateByMilli(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        int hr = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        String timeStr = String.format("%02d:%02d:%02d", hr, min, sec);
        return calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" +
                calendar.get(Calendar.DAY_OF_MONTH) + "日 " + timeStr;
    }

    public static long[] getSavedTimeAndRank() {
        long[] x = new long[]{0, 100, 0, 0, 0}; // 個人省時(sec), rank percentage, rank, 總省時(hr), user
        ParseUser currentUser = ParseUser.getCurrentUser();
        x[0] = currentUser.getInt("SavedTotalTime");
        x[2] = currentUser.getInt("save_time_ranking");
        ParseObject rankInfo = null;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("RankInfo");
        try {
            rankInfo = query.getFirst();
        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }
        if (rankInfo != null) {
            x[3] = rankInfo.getLong("SavedTimeOfAllUsers") / 3600;
            int NumOfUsers = rankInfo.getInt("NumOfUsers");
            x[4] = NumOfUsers;
            if (NumOfUsers != 0) {
                x[1] = (x[2] / NumOfUsers) * 100;
                if (x[1] != 100) ++x[1];
            }
        }

        return x;
    }

    public static boolean checkNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Log.d(TAG, "checkNetworkAvailable: " + (activeNetworkInfo != null && activeNetworkInfo
                .isConnected()));
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static void initializeLocalIdle() {
        Integer[] temp = IdleSettingsActivity.defaultMultiChoice;
        if (temp == null) return;
        localIdle = new LinkedList<>();
        for (int i : temp) {
            Log.d(TAG, "initializeLocalIdle, i: " + i);
            localIdle.add(i);
        }
    }

    private static boolean inIdle(int i) {
        if (localIdle != null) {
            if (localIdle.size() != 0)
                if (localIdle.get(0) == i) {
                    localIdle.remove(0);
                    return true;
                }
        }
        return false;
    }
}
