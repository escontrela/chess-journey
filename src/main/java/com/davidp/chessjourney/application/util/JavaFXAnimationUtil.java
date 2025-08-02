package com.davidp.chessjourney.application.util;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class JavaFXAnimationUtil {
    public static AnimationBuilder animationBuilder() {
        return new AnimationBuilder();
    }

    public static class AnimationBuilder {
        private Duration duration = Duration.seconds(0.2);
        private Runnable onFinished;
        private Node node;
        private boolean fadeIn = true;

        public AnimationBuilder duration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public AnimationBuilder onFinished(Runnable onFinished) {
            this.onFinished = onFinished;
            return this;
        }

        public AnimationBuilder fadeIn(Node node) {
            this.node = node;
            this.fadeIn = true;
            return this;
        }

        public AnimationBuilder fadeOut(Node node) {
            this.node = node;
            this.fadeIn = false;
            return this;
        }

        public void buildAndPlay() {
            if (node == null) return;
            FadeTransition ft = new FadeTransition(duration, node);
            if (fadeIn) {
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
            } else {
                ft.setFromValue(1.0);
                ft.setToValue(0.0);
            }
            if (onFinished != null) {
                ft.setOnFinished(e -> onFinished.run());
            }
            ft.play();
        }
    }
}

