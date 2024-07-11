package com.davidp.chessjourney.domain.common;

import java.util.Objects;

public class Piece {
  private final PieceType type;
  private final PieceColor color;

  public Piece(final PieceType type, final PieceColor color) {

    this.type = type;
    this.color = color;
  }

  public PieceType getType() {

    return type;
  }

  public PieceColor getColor() {

    return color;
  }

  public boolean isColor(PieceColor color) {

    return this.color == color;
  }

  public boolean is(PieceType type) {

    return this.type == type;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Piece piece = (Piece) o;
    return type == piece.type && color == piece.color;
  }

  @Override
  public int hashCode() {

    return Objects.hash(type, color);
  }

  public String toString() {

    return color.toString() + " " + type.toString();
  }
}
