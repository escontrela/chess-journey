package com.davidp.chessjourney.domain;

public enum Row {
  ONE(0),
  TWO(1),
  THREE(2),
  FOUR(3),
  FIVE(4),
  SIX(5),
  SEVEN(6),
  EIGHT(7);

  private final int value;

  Row(final int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
