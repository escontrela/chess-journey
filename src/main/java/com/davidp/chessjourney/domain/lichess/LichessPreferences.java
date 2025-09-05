package com.davidp.chessjourney.domain.lichess;

/**
 * Represents Lichess user preferences from the API.
 */
public class LichessPreferences {
  
  private boolean dark;
  private boolean transp;
  private String bgImg;
  private boolean is3d;
  private String theme;
  private String pieceSet;
  private String theme3d;
  private String pieceSet3d;
  private String soundSet;
  private int blindfold;
  private int autoQueen;
  private int autoThreefold;
  private int takeback;
  private int moretime;
  private int clockTenths;
  private boolean clockBar;
  private boolean clockSound;
  private boolean premove;
  private int animation;
  private boolean captured;
  private boolean follow;
  private boolean highlight;
  private boolean destination;
  private boolean coords;
  private boolean replay;
  private int challenge;
  private int message;
  private int coordColor;
  private int submitMove;
  private int confirmResign;
  private int insightShare;
  private int keyboardMove;
  private int zen;
  private int moveEvent;
  private int rookCastle;

  public LichessPreferences() {}

  // Getters and setters
  public boolean isDark() {
    return dark;
  }

  public void setDark(boolean dark) {
    this.dark = dark;
  }

  public boolean isTransp() {
    return transp;
  }

  public void setTransp(boolean transp) {
    this.transp = transp;
  }

  public String getBgImg() {
    return bgImg;
  }

  public void setBgImg(String bgImg) {
    this.bgImg = bgImg;
  }

  public boolean isIs3d() {
    return is3d;
  }

  public void setIs3d(boolean is3d) {
    this.is3d = is3d;
  }

  public String getTheme() {
    return theme;
  }

  public void setTheme(String theme) {
    this.theme = theme;
  }

  public String getPieceSet() {
    return pieceSet;
  }

  public void setPieceSet(String pieceSet) {
    this.pieceSet = pieceSet;
  }

  public String getTheme3d() {
    return theme3d;
  }

  public void setTheme3d(String theme3d) {
    this.theme3d = theme3d;
  }

  public String getPieceSet3d() {
    return pieceSet3d;
  }

  public void setPieceSet3d(String pieceSet3d) {
    this.pieceSet3d = pieceSet3d;
  }

  public String getSoundSet() {
    return soundSet;
  }

  public void setSoundSet(String soundSet) {
    this.soundSet = soundSet;
  }

  public int getBlindfold() {
    return blindfold;
  }

  public void setBlindfold(int blindfold) {
    this.blindfold = blindfold;
  }

  public int getAutoQueen() {
    return autoQueen;
  }

  public void setAutoQueen(int autoQueen) {
    this.autoQueen = autoQueen;
  }

  public int getAutoThreefold() {
    return autoThreefold;
  }

  public void setAutoThreefold(int autoThreefold) {
    this.autoThreefold = autoThreefold;
  }

  public int getTakeback() {
    return takeback;
  }

  public void setTakeback(int takeback) {
    this.takeback = takeback;
  }

  public int getMoretime() {
    return moretime;
  }

  public void setMoretime(int moretime) {
    this.moretime = moretime;
  }

  public int getClockTenths() {
    return clockTenths;
  }

  public void setClockTenths(int clockTenths) {
    this.clockTenths = clockTenths;
  }

  public boolean isClockBar() {
    return clockBar;
  }

  public void setClockBar(boolean clockBar) {
    this.clockBar = clockBar;
  }

  public boolean isClockSound() {
    return clockSound;
  }

  public void setClockSound(boolean clockSound) {
    this.clockSound = clockSound;
  }

  public boolean isPremove() {
    return premove;
  }

  public void setPremove(boolean premove) {
    this.premove = premove;
  }

  public int getAnimation() {
    return animation;
  }

  public void setAnimation(int animation) {
    this.animation = animation;
  }

  public boolean isCaptured() {
    return captured;
  }

  public void setCaptured(boolean captured) {
    this.captured = captured;
  }

  public boolean isFollow() {
    return follow;
  }

  public void setFollow(boolean follow) {
    this.follow = follow;
  }

  public boolean isHighlight() {
    return highlight;
  }

  public void setHighlight(boolean highlight) {
    this.highlight = highlight;
  }

  public boolean isDestination() {
    return destination;
  }

  public void setDestination(boolean destination) {
    this.destination = destination;
  }

  public boolean isCoords() {
    return coords;
  }

  public void setCoords(boolean coords) {
    this.coords = coords;
  }

  public boolean isReplay() {
    return replay;
  }

  public void setReplay(boolean replay) {
    this.replay = replay;
  }

  public int getChallenge() {
    return challenge;
  }

  public void setChallenge(int challenge) {
    this.challenge = challenge;
  }

  public int getMessage() {
    return message;
  }

  public void setMessage(int message) {
    this.message = message;
  }

  public int getCoordColor() {
    return coordColor;
  }

  public void setCoordColor(int coordColor) {
    this.coordColor = coordColor;
  }

  public int getSubmitMove() {
    return submitMove;
  }

  public void setSubmitMove(int submitMove) {
    this.submitMove = submitMove;
  }

  public int getConfirmResign() {
    return confirmResign;
  }

  public void setConfirmResign(int confirmResign) {
    this.confirmResign = confirmResign;
  }

  public int getInsightShare() {
    return insightShare;
  }

  public void setInsightShare(int insightShare) {
    this.insightShare = insightShare;
  }

  public int getKeyboardMove() {
    return keyboardMove;
  }

  public void setKeyboardMove(int keyboardMove) {
    this.keyboardMove = keyboardMove;
  }

  public int getZen() {
    return zen;
  }

  public void setZen(int zen) {
    this.zen = zen;
  }

  public int getMoveEvent() {
    return moveEvent;
  }

  public void setMoveEvent(int moveEvent) {
    this.moveEvent = moveEvent;
  }

  public int getRookCastle() {
    return rookCastle;
  }

  public void setRookCastle(int rookCastle) {
    this.rookCastle = rookCastle;
  }
}