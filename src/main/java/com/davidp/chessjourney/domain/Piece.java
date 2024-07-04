package com.davidp.chessjourney.domain;

public class Piece {

  private final PieceType type;
  private final PieceColor color;
  private final Row row;
  private final Col col;

  public Piece(final PieceType type, final PieceColor color, final Row row, final Col col) {

    this.type = type;
    this.color = color;
    this.row = row;
    this.col = col;
  }

  public PieceType getType() {
    return type;
  }

  public PieceColor getColor() {
    return color;
  }

  public Row getRow() {
    return row;
  }

  public Col getCol() {
    return col;
  }

  public boolean isColor(final PieceColor color) {

    return this.color == color;
  }

  public boolean is(final PieceType type) {

    return this.type == type;
  }
}
