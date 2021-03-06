package com.dots.focus.config;

public enum KickState {
    REQUEST_NOT_DOWNLOADED(0),
    REQUEST_DOWNLOADED(1),
    KICK_NOT_DOWNLOADED(2),
    KICK_DOWNLOADED(3),
    RESPONSE_NOT_DOWNLOADED(4),
    RESPONSE_DOWNLOADED(5),
    READ(6);
    private final int value;

    KickState(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}