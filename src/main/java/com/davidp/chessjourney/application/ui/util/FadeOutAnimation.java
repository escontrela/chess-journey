package com.davidp.chessjourney.application.ui.util;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeOutAnimation {

  private final Node target;
  private final Duration duration;
  private Runnable onFinished;

  public FadeOutAnimation(Node target, Duration duration) {
    this.target = target;
    this.duration = duration;
  }

  /** Configura una acción a ejecutar al finalizar la animación. */
  public FadeOutAnimation onFinished(Runnable onFinished) {
    this.onFinished = onFinished;
    return this;
  }

  /** Construye y reproduce la animación. */
  public void play() {
    Timeline timeline =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(target.opacityProperty(), 1)),
            new KeyFrame(duration, new KeyValue(target.opacityProperty(), 0)));

    timeline.setOnFinished(
        event -> {
          // Opcional: oculta el nodo tras el fade-out
          target.setVisible(false);

          // Ejecuta la acción configurada, si existe
          if (onFinished != null) {
            onFinished.run();
          }
        });

    timeline.play();
  }
}
