package com.davidp.chessjourney.domain.common;

public enum PieceType {
  KING('k'),
  QUEEN('q'),
  ROOK('r'),
  BISHOP('b'),
  KNIGHT('n'),
  PAWN('p');

  private final char symbol;

  PieceType(char symbol) {
    this.symbol = symbol;
  }

  public char getSymbol() {
    return symbol;
  }
}
