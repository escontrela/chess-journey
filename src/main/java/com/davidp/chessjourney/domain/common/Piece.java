package com.davidp.chessjourney.domain.common;

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
}