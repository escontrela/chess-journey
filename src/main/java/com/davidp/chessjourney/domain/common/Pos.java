package com.davidp.chessjourney.domain.common;

import java.util.Objects;

/** A position in the chess board. */
public class Pos {

  private final Row row;
  private final Col col;

  public Pos(final Col col, final Row row) {

    this.col = col;
    this.row = row;
  }

  public Row getRow() {

    return row;
  }

  public Col getCol() {

    return col;
  }

  public static Pos of(Col col, Row row) {

    Objects.requireNonNull(col, "col cannot be null");
    Objects.requireNonNull(row, "row cannot be null");

    return new Pos(col, row);
  }

  public static Pos parseString(final String posString) {

    Objects.requireNonNull(posString, "posString cannot be null");
    if (posString.length() != 2) {

      throw new IllegalArgumentException("Invalid position string: " + posString);
    }

    Col col = Col.valueOf(posString.substring(0, 1).toUpperCase());
    Row row = Row.fromValue(Integer.parseInt(posString.substring(1, 2)));
    return new Pos(col, row);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pos pos = (Pos) o;
    return row == pos.row && col == pos.col;
  }

  @Override
  public int hashCode() {
    return Objects.hash(row, col);
  }

  @Override
  public String toString() {

    return col.name() + row.getValue();
  }
}
