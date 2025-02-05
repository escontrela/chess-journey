package com.davidp.chessjourney.application.ui.board;

import com.almasb.fxgl.dsl.FXGL;
import com.davidp.chessjourney.application.factories.ScreenFactory;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import javafx.fxml.FXML;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PromoteViewController implements ScreenController {

  private ScreenController.ScreenStatus status;

  @FXML
  private Pane pnlBishop;

  @FXML
  private Pane pnlPawn;

  @FXML
  private Pane pnlPromote;

  @FXML
  private Pane pnlQueen;

  @FXML
  private Pane pnlRook;

  @FXML
  private Pane pnlknight;

  @FXML
  private Pane rootPane;

  @FXML
  void keyPressed(KeyEvent event) {

  }

  @FXML
  void mouseClicked(MouseEvent event) {

  }

  public void initialize() {

    status = ScreenController.ScreenStatus.INITIALIZED;

  }

  @Override
  public void setData(InputScreenData inputData) {

  }

  @Override
  public void setLayout(double layoutX, double layoutY) {

  }

  @Override
  public void show() {

    rootPane.setVisible(false);

    // Fade in animation when showing
    FXGL.animationBuilder()
            .duration(Duration.seconds(0.2))
            .onFinished(
                    () -> {
                      rootPane.setVisible(true);
                      rootPane.toFront();
                    })
            .fadeIn(rootPane)
            .buildAndPlay();
  }

  @Override
  public void show(InputScreenData inputData) {

    setData(inputData);
    status = ScreenController.ScreenStatus.VISIBLE;
    show();
  }

  @Override
  public void hide() {

    FXGL.animationBuilder()
            .duration(Duration.seconds(0.2))
            .onFinished(
                    () -> {
                      rootPane.setVisible(false);
                      status = ScreenStatus.HIDDEN;
                    })
            .fadeOut(rootPane)
            .buildAndPlay();
  }


  @Override
  public boolean isVisible() {
    return rootPane.isVisible();
  }

  @Override
  public boolean isHidden() {
    return !rootPane.isVisible();
  }

  @Override
  public Pane getRootPane() {
    return rootPane;
  }

  @Override
  public ScreenStatus getStatus() {
    return null;
  }

  @Override
  public boolean isInitialized() {

    return status == ScreenController.ScreenStatus.INITIALIZED;
  }
}
