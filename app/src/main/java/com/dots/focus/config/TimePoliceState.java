package com.dots.focus.config;

public enum TimePoliceState {
    INVITE_NOT_DOWNLOADED(0),
    INVITE_DOWNLOADED(1),
    REPLY_NOT_DOWNLOADED(2),
    REPLY_DOWNLOADED(3),
    INVITING(4);

    private final int value;

    TimePoliceState(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
