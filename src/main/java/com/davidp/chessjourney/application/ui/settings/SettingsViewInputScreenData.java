package com.davidp.chessjourney.application.ui.settings;

import java.awt.*;

public class SettingsViewInputScreenData extends InputScreenData {

  private final Long userId;

  public SettingsViewInputScreenData(Long userId, double layoutX, double layoutY) {

    super(layoutX, layoutY);
    this.userId = userId;
  }

  public SettingsViewInputScreenData(Long userId, Point point) {

    super(point.getX(), point.getY());
    this.userId = userId;
  }

  public Long getUserId() {

    return userId;
  }
}
