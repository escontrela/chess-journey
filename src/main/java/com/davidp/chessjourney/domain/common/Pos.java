package com.davidp.chessjourney.domain.common;

import java.util.Objects;

/** A position in the chess board. */
public class Pos {

  private final Row row;
  private final Col col;

  public Pos(final Row row, final Col col) {

    this.row = row;
    this.col = col;
  }

  public Row getRow() {

    return row;
  }

  public Col getCol() {

    return col;
  }

  public static Pos of(Row row, Col col) {

    Objects.requireNonNull(row, "row cannot be null");
    Objects.requireNonNull(col, "col cannot be null");

    return new Pos(row, col);
  }

  public static Pos parseString(final String posString) {

    Objects.requireNonNull(posString, "posString cannot be null");
    if (posString.length() != 2) {

      throw new IllegalArgumentException("Invalid position string: " + posString);
    }

    Col col = Col.valueOf(posString.substring(0, 1).toUpperCase());
    Row row = Row.fromValue(Integer.parseInt(posString.substring(1, 2)));
    return new Pos(row, col);
  }

  @Override
  public String toString() {

    return col.name() + row.getValue();
  }
}
