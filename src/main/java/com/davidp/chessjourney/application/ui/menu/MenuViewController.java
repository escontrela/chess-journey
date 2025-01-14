package com.davidp.chessjourney.application.ui.menu;

import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class MenuViewController implements ScreenController {

  @FXML private Pane rootPane;

  public void initialize() {

  }


  @Override
  public void setData(InputScreenData inputData) {

    if (inputData.isLayoutInfoValid()){

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
    rootPane.setVisible(true);
  }

  @Override
  public void hide() {
    rootPane.setVisible(false);
  }

  @Override
  public boolean isVisible() {
    return rootPane.isVisible();
  }

  @Override
  public Pane getRootPane() {
      return rootPane;
  }
}