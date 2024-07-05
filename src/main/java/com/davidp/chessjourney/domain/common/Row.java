package com.davidp.chessjourney.domain.common;

public enum Row {
  ONE(1),
  TWO(2),
  THREE(3),
  FOUR(4),
  FIVE(5),
  SIX(6),
  SEVEN(7),
  EIGHT(8);

  private final int value;

  Row(final int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static Row fromValue(int value) {
    for (Row row : Row.values()) {
      if (row.value == value) {
        return row;
      }
    }
    throw new IllegalArgumentException("Invalid value for Row: " + value);
  }

}
