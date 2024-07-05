package com.davidp.chessjourney.domain.common;

public enum Col {
  A(1),
  B(2),
  C(3),
  D(4),
  E(5),
  F(6),
  G(7),
  H(8);

  private final int value;

  Col(final int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
