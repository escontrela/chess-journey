package com.davidp.chessjourney.application.ui.util;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeInAnimation {

  private final Node target;
  private final Duration duration;
  private Runnable onFinished;

  public FadeInAnimation(Node target, Duration duration) {
    this.target = target;
    this.duration = duration;
  }

  /** Configura una acción a ejecutar al finalizar la animación. */
  public FadeInAnimation onFinished(Runnable onFinished) {
    this.onFinished = onFinished;
    return this;
  }

  /** Construye y reproduce la animación. */
  public void play() {
    // Asegurarnos de que el nodo es visible antes de iniciar la animación
    target.setOpacity(0);
    target.setVisible(true);

    Timeline timeline =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(target.opacityProperty(), 0)),
            new KeyFrame(duration, new KeyValue(target.opacityProperty(), 1)));

    timeline.setOnFinished(
        event -> {
          // Ejecuta la acción configurada, si existe
          if (onFinished != null) {
            onFinished.run();
          }
        });

    timeline.play();
  }
}
