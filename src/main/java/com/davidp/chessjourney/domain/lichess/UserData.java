package com.davidp.chessjourney.domain.lichess;

import com.davidp.chessjourney.domain.User;

/**
 * Combined user data that includes both local application user data
 * and Lichess account data when available.
 */
public class UserData {
  
  private User localUser;
  private LichessUser lichessUser;
  private boolean lichessConnected;
  private String lichessError;

  public UserData() {}

  public UserData(User localUser) {
    this.localUser = localUser;
    this.lichessConnected = false;
  }

  public UserData(User localUser, LichessUser lichessUser) {
    this.localUser = localUser;
    this.lichessUser = lichessUser;
    this.lichessConnected = true;
  }

  public User getLocalUser() {
    return localUser;
  }

  public void setLocalUser(User localUser) {
    this.localUser = localUser;
  }

  public LichessUser getLichessUser() {
    return lichessUser;
  }

  public void setLichessUser(LichessUser lichessUser) {
    this.lichessUser = lichessUser;
    this.lichessConnected = (lichessUser != null);
  }

  public boolean isLichessConnected() {
    return lichessConnected;
  }

  public void setLichessConnected(boolean lichessConnected) {
    this.lichessConnected = lichessConnected;
  }

  public String getLichessError() {
    return lichessError;
  }

  public void setLichessError(String lichessError) {
    this.lichessError = lichessError;
    this.lichessConnected = false;
  }
}