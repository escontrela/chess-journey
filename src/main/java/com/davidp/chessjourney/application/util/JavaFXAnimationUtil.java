package com.davidp.chessjourney.application.util;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

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

    /**
     * Efecto de niebla animada con píxeles grises ascendentes.
     *
     * @param targetPane      Pane donde se dibuja la niebla
     * @param durationSegundos Duración total de la animación en segundos
     */
    public static void playFogEffect(Pane targetPane, double durationSegundos) {
        if (targetPane.getWidth() < 10 || targetPane.getHeight() < 10) return; // Evita problemas si el Pane es muy pequeño

        Random random = new Random();
        int width = (int) targetPane.getWidth();
        int height = (int) targetPane.getHeight();
        int numClouds = 6 + random.nextInt(4); // 6-9 agrupaciones
        ParallelTransition allClouds = new ParallelTransition();

        for (int c = 0; c < numClouds; c++) {
            int cloudWidth = 80 + random.nextInt(60); // 80-140 px
            int cloudHeight = 30 + random.nextInt(20); // 30-50 px
            int baseX = random.nextInt(Math.max(1, width - cloudWidth));
            int baseY = height - 30 - random.nextInt(30); // parte baja
            int density = 18 + random.nextInt(10); // píxeles por nube

            for (int i = 0; i < density; i++) {
                int px = baseX + random.nextInt(cloudWidth);
                int py = baseY + random.nextInt(cloudHeight);
                int size = 3 + random.nextInt(4); // 3-6 px
                Rectangle pixel = new Rectangle(size, size, Color.rgb(
                        180 + random.nextInt(40),
                        180 + random.nextInt(40),
                        180 + random.nextInt(40),
                        0.45 + 0.25 * random.nextDouble()));
                pixel.setArcWidth(size * 0.7);
                pixel.setArcHeight(size * 0.7);
                pixel.setLayoutX(px);
                pixel.setLayoutY(py);
                targetPane.getChildren().add(pixel);

                double moveY = -60 - random.nextInt(40); // suben 60-100px
                double delay = random.nextDouble() * (durationSegundos * 0.5); // dispersión
                TranslateTransition tt = new TranslateTransition(
                        Duration.seconds(durationSegundos * (0.7 + 0.5 * random.nextDouble())), pixel);
                tt.setFromY(0);
                tt.setToY(moveY);
                tt.setDelay(Duration.seconds(delay));
                tt.setOnFinished(e -> targetPane.getChildren().remove(pixel));
                allClouds.getChildren().add(tt);
            }
        }
        allClouds.setOnFinished(e -> {});
        allClouds.play();
    }
}
