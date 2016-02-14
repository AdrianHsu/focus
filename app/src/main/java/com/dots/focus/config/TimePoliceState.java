package com.dots.focus.config;

/**
 * Created by Harvey on 2/14/2016.
 */
public enum TimePoliceState {
    INVITE_NOT_DOWNLOADED(0),
    INVITE_DOWNLOADED(1),
    REPLY_NOT_DOWNLOADED(2),
    REPLY_DOWNLOADED(3);

    private final int value;

    TimePoliceState(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
