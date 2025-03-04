package com.davidp.chessjourney.application.ui.user;

import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import java.awt.*;

public class UserStatsInputScreenData extends InputScreenData {

  private final Long userId;

  public UserStatsInputScreenData(Long userId, double layoutX, double layoutY) {

    super(layoutX, layoutY);
    this.userId = userId;
  }

  public UserStatsInputScreenData(Long userId, Point point) {

    super(point.getX(), point.getY());
    this.userId = userId;
  }

  public Long getUserId() {

    return userId;
  }
}
