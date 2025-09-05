package com.davidp.chessjourney.domain.lichess;

/**
 * Represents user data from Lichess API.
 * Based on response from GET https://lichess.org/api/account
 */
public class LichessUser {
  
  private String id;
  private String username; 
  private String email;
  private LichessPreferences prefs;
  private LichessStats count;
  
  public LichessUser() {}
  
  public LichessUser(String id, String username, String email, LichessPreferences prefs, LichessStats count) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.prefs = prefs;
    this.count = count;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LichessPreferences getPrefs() {
    return prefs;
  }

  public void setPrefs(LichessPreferences prefs) {
    this.prefs = prefs;
  }

  public LichessStats getCount() {
    return count;
  }

  public void setCount(LichessStats count) {
    this.count = count;
  }
}