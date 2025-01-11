package com.davidp.chessjourney.application.domain;


public class UserSavedAppEvent {

    private final long userId;

    public UserSavedAppEvent(final long userId) {

        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }
}