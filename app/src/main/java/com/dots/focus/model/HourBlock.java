package com.dots.focus.model;

import android.util.Log;

import com.dots.focus.util.FetchAppUtil;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


@ParseClassName("HourBlock")
public class HourBlock extends ParseObject {

    public HourBlock() {}

    public HourBlock(final long hourInLong, final int h, final int index) {

        int size = FetchAppUtil.getSize();
        List<Integer> appLength = new ArrayList<>(size);
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
        setStartIndex(index);
        setEndIndex(index);
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
    public int getStartIndex() {
        return getInt("startIndex");
    }
    public void setStartIndex(int index) {
        put("startIndex", index);
    }
    public int getEndIndex() {
        return getInt("endIndex");
    }
    public void setEndIndex(int index) {
        put("endIndex", index);
    }

    public static ParseQuery<HourBlock> getQuery() {
        return ParseQuery.getQuery(HourBlock.class);
    }
}