package com.dots.focus.model;

import com.dots.focus.util.FetchAppUtil;
import com.dots.focus.util.TrackAccessibilityUtil;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


@ParseClassName("DayBlock")
public class DayBlock extends ParseObject {
    public List<HourBlock> waiting = new ArrayList<>();

    public DayBlock() {}

    public DayBlock(long dayInLong) {
        int size = FetchAppUtil.getSize();
        List<Integer> appLength = new ArrayList<>(size);
        for (int i = 0; i < size; ++i)
            appLength.add(0);
        List<String> hours = new ArrayList<>(24);
        for (int i = 0; i < 24; ++i)
            hours.add("");

        setUser(ParseUser.getCurrentUser());
        setAppLength(appLength);
        setTime(dayInLong);
        setOffset(TrackAccessibilityUtil.getTimeOffset());
        setEnd(false);
        setHourBlocks(hours);
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
    public void setAppLength(ArrayList<Integer> appLength) {
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
    public List<String> getHourBlocks() {
        return getList("hourBlocks");
    }
    public void setHourBlocks(List<String> hours) {
        put("hourBlocks", hours);
    }
    public static ParseQuery<DayBlock> getQuery() {
        return ParseQuery.getQuery(DayBlock.class);
    }
}
