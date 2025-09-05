package com.davidp.chessjourney.domain.lichess;

/**
 * Represents Lichess user statistics from the API.
 */
public class LichessStats {
  
  private int all;
  private int rated;
  private int ai;
  private int draw;
  private int drawH;
  private int loss;
  private int lossH;
  private int win;
  private int winH;
  private int bookmark;
  private int playing;
  private int imported;
  private int me;

  public LichessStats() {}

  // Getters and setters
  public int getAll() {
    return all;
  }

  public void setAll(int all) {
    this.all = all;
  }

  public int getRated() {
    return rated;
  }

  public void setRated(int rated) {
    this.rated = rated;
  }

  public int getAi() {
    return ai;
  }

  public void setAi(int ai) {
    this.ai = ai;
  }

  public int getDraw() {
    return draw;
  }

  public void setDraw(int draw) {
    this.draw = draw;
  }

  public int getDrawH() {
    return drawH;
  }

  public void setDrawH(int drawH) {
    this.drawH = drawH;
  }

  public int getLoss() {
    return loss;
  }

  public void setLoss(int loss) {
    this.loss = loss;
  }

  public int getLossH() {
    return lossH;
  }

  public void setLossH(int lossH) {
    this.lossH = lossH;
  }

  public int getWin() {
    return win;
  }

  public void setWin(int win) {
    this.win = win;
  }

  public int getWinH() {
    return winH;
  }

  public void setWinH(int winH) {
    this.winH = winH;
  }

  public int getBookmark() {
    return bookmark;
  }

  public void setBookmark(int bookmark) {
    this.bookmark = bookmark;
  }

  public int getPlaying() {
    return playing;
  }

  public void setPlaying(int playing) {
    this.playing = playing;
  }

  public int getImported() {
    return imported;
  }

  public void setImported(int imported) {
    this.imported = imported;
  }

  public int getMe() {
    return me;
  }

  public void setMe(int me) {
    this.me = me;
  }
}