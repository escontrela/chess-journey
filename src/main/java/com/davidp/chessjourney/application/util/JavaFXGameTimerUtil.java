package com.davidp.chessjourney.application.util;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class JavaFXGameTimerUtil {
    private static final List<Animation> activeTimers = new ArrayList<>();

    public static void runLoop(Runnable action, Duration interval) {
        Timeline timeline = new Timeline(new KeyFrame(interval, e -> action.run()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        addTimer(timeline);
    }

    public static void addTimer(Animation timer) {
        activeTimers.add(timer);
    }

    public static void clear() {
        for (Animation timer : activeTimers) {
            timer.stop();
        }
        activeTimers.clear();
    }
}
