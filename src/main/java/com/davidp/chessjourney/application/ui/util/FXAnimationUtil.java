package com.davidp.chessjourney.application.ui.util;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class FXAnimationUtil {
    public static FadeBuilder fadeIn(Node node, double seconds) {
        return new FadeBuilder(node, seconds, true);
    }

    public static FadeBuilder fadeOut(Node node, double seconds) {
        return new FadeBuilder(node, seconds, false);
    }

    public static class FadeBuilder {
        private final Node node;
        private final double seconds;
        private final boolean fadeIn;
        private int repeatCount = 1;
        private boolean autoReverse = false;
        private Runnable onFinished;

        public FadeBuilder(Node node, double seconds, boolean fadeIn) {
            this.node = node;
            this.seconds = seconds;
            this.fadeIn = fadeIn;
        }

        public FadeBuilder repeat(int count) {
            this.repeatCount = count;
            return this;
        }

        public FadeBuilder autoReverse(boolean value) {
            this.autoReverse = value;
            return this;
        }

        public FadeBuilder onFinished(Runnable onFinished) {
            this.onFinished = onFinished;
            return this;
        }

        public void buildAndPlay() {
            FadeTransition ft = new FadeTransition(Duration.seconds(seconds), node);
            ft.setFromValue(fadeIn ? 0.0 : 1.0);
            ft.setToValue(fadeIn ? 1.0 : 0.0);
            ft.setCycleCount(repeatCount);
            ft.setAutoReverse(autoReverse);
            if (onFinished != null) {
                ft.setOnFinished(e -> onFinished.run());
            }
            ft.play();
        }
    }
}
