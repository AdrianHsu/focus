package com.dots.focus.config;

public enum FriendRelationship {
    NOT_FRIEND(0),
    FRIEND_INVITED(1),
    FRIEND_CONFIRMED(2),
    IS_FRIEND(3),
    FRIEND_INVITING(4);
    private final int value;

    FriendRelationship(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
