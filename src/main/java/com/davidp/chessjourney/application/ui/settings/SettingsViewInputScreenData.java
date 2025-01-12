package com.davidp.chessjourney.application.ui.settings;

public class SettingsViewInputScreenData extends InputScreenData {

  private final Long userId;

  public SettingsViewInputScreenData(Long userId, double layoutX, double layoutY) {

    super(layoutX, layoutY);
    this.userId = userId;
  }

  public Long getUserId() {

    return userId;
  }
}
