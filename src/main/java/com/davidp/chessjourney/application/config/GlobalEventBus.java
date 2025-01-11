package com.davidp.chessjourney.application.config;


import com.google.common.eventbus.EventBus;

public class GlobalEventBus {

    private static final EventBus eventBus = new EventBus("GlobalBus");

    private GlobalEventBus() {
    }

    public static EventBus get() {
        return eventBus;
    }
}