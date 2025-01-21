package com.davidp.chessjourney.application.ui.settings;

import java.awt.*;

public class InputScreenData {

  protected double layoutX;
  protected double layoutY;

  public InputScreenData(double layoutX, double layoutY) {

    this.layoutX = layoutX;
    this.layoutY = layoutY;
  }

  public double getLayoutX() {

    return layoutX;
  }

  public double getLayoutY() {

    return layoutY;
  }

  public boolean isLayoutInfoValid() {

    return layoutX != 0 && layoutY != 0;
  }

  public static InputScreenData fromPosition(double layoutX, double layoutY) {

    return new InputScreenData(layoutX, layoutY);
  }

  public static InputScreenData fromPosition(Point point) {

    return new InputScreenData(point.getX(), point.getY());
  }
}
