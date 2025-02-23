package com.davidp.chessjourney.domain.common;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/** This class represents a piece position in a chess board. */
public class PiecePosition {

  private final Piece piece;
  private final Pos position;

  public PiecePosition(final Piece piece, final Pos position) {

    this.piece = piece;
    this.position = position;
  }

  public Piece getPiece() {

    return piece;
  }

  public Pos getPosition() {

    return position;
  }

  @Override
  public String toString() {

    return piece.toString() + " " + position.toString();
  }

  /**
   * This method returns the piece position in a chess board.
   *
   * @param pieceType
   * @param color
   * @param piecePositions
   * @return a set of piece positions
   */
  public static Set<Pos> findPiecePosition(
      PieceType pieceType, PieceColor color, Collection<PiecePosition> piecePositions) {
    return piecePositions.stream()
        .filter(pp -> pp.getPiece().getType() == pieceType && pp.getPiece().getColor() == color)
        .map(PiecePosition::getPosition)
        .collect(Collectors.toSet());
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PiecePosition that = (PiecePosition) o;
    return piece.equals(that.piece) && position.equals(that.position);
  }
}
