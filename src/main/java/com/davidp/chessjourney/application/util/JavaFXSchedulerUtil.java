package com.davidp.chessjourney.application.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class JavaFXSchedulerUtil {
    public static void runOnce(Runnable action, Duration delay) {
        Timeline timeline = new Timeline(new KeyFrame(delay, e -> action.run()));
        timeline.setCycleCount(1);
        timeline.play();
    }
}

