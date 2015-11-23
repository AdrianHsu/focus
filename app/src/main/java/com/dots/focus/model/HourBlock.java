package com.dots.focus.model;

import android.util.Log;

import com.dots.focus.util.FetchAppUtil;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harvey on 2015/11/16.
 */
@ParseClassName("HourBlock")
public class HourBlock extends ParseObject {

    public HourBlock() {
        List<Integer> appLength = new ArrayList<>();
        int size = FetchAppUtil.getSize();
        for(int i = 0; i < size; ++i)
            appLength.add(0);

        if (ParseUser.getCurrentUser() != null)
            setUser(ParseUser.getCurrentUser());
        setAppLength(appLength);
        setTime(0);
        setOffset(TrackAccessibilityUtil.getTimeOffset());
        setEnd(false);
        setDayBlock("");
        setAppUsage(new ArrayList<String>());
    }

    public HourBlock(final long hourInLong, final int h) {

        List<Integer> appLength = new ArrayList<>();
        int size = FetchAppUtil.getSize();
        for(int i = 0; i < size; ++i)
            appLength.add(0);

        if (ParseUser.getCurrentUser() != null)
            setUser(ParseUser.getCurrentUser());
        setAppLength(appLength);
        setTime(hourInLong);
        setOffset(TrackAccessibilityUtil.getTimeOffset());
        setEnd(false);

        String id = TrackAccessibilityUtil.getCurrentDay(hourInLong).getObjectId();
        if (id != null)
            setDayBlock(id);

        setAppUsage(new ArrayList<String>());
    }

    public ParseUser getUser() {
        return getParseUser("User");
    }
    public void setUser(ParseUser user) {
        put("User", user);
    }
    public List<Integer> getAppLength() {
        return getList("appLength");
    }
    public void setAppLength(List<Integer> appLength) {
        put("appLength", appLength);
    }
    public long getTime() {
        return getLong("time");
    }
    public void setTime(long time) {
        put("time", time);
    }
    public int getOffset() {
        return getInt("offset");
    }
    public void setOffset(int offset) {
        put("offset", offset);
    }
    public boolean getEnd() {
        return getBoolean("end");
    }
    public void setEnd(boolean end) {
        put("end", end);
    }
    public String getDayBlock() {
        return getString("prev");
    }
    public void setDayBlock(String dayId) {
        put("prev", dayId);
    }
    public List<String> getAppUsage() {
        return getList("appUsage");
    }
    public void setAppUsage(List<String> appUsage) {
        put("appUsage", appUsage);
    }
    public static ParseQuery<HourBlock> getQuery() {
        return ParseQuery.getQuery(HourBlock.class);
    }
}