package com.dots.focus.config;

public enum LimitType {
    DAY_LIMIT(0),
    HOUR_LIMIT(1);
    private final int value;

    LimitType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
