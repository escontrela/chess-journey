package com.davidp.chessjourney.application.ui.user;

import com.almasb.fxgl.dsl.FXGL;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.board.PromoteViewInputScreenData;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class UserViewController implements ScreenController {

  private ScreenStatus status;

  private PromoteViewInputScreenData promoteViewInputScreenData;


  @FXML
  private Button btClose;

  @FXML
  private ImageView imgClose;

  @FXML
  private Label lbUser;

  @FXML
  private Pane rootPane;



  public void initialize() {

    status = ScreenStatus.INITIALIZED;

  }


  @Override
  public void setData(InputScreenData inputData) {

    if (inputData.isLayoutInfoValid()) {

      setLayout(inputData.getLayoutX(), inputData.getLayoutY());
    }

  }


  @Override
  public void setLayout(double layoutX, double layoutY) {

    rootPane.setLayoutX(layoutX);
    rootPane.setLayoutY(layoutY);
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
    status = ScreenStatus.VISIBLE;
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

    return status == ScreenStatus.INITIALIZED;
  }

  protected void setPromoteViewInputScreenData(PromoteViewInputScreenData promoteViewInputScreenData) {

    this.promoteViewInputScreenData = promoteViewInputScreenData;
  }

  protected PromoteViewInputScreenData getPromoteViewInputScreenData() {

    return promoteViewInputScreenData;
  }

  @FXML
  void buttonAction(ActionEvent event) {

    if (event.getSource() == btClose) {

      rootPane.setVisible(false);
    }

  }
}
