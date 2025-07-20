package com.davidp.chessjourney.domain.common;

import java.util.List;

public class GameState {

  private final List<PiecePosition> pieces;
  private final PieceColor activeColor;
  private final CastlingAvailability castlingAvailability;
  private final EnPassantTargetSquare enPassantTargetSquare;
  private final int halfMoveClock;
  private final int fullMoveNumber;

  public GameState(
      final List<PiecePosition> pieces,
      final PieceColor activeColor,
      final CastlingAvailability castlingAvailability,
      final EnPassantTargetSquare enPassantTargetSquare,
      final int halfMoveClock,
      final int fullMoveNumber) {

    this.pieces = pieces;
    this.activeColor = activeColor;
    this.castlingAvailability = castlingAvailability;
    this.enPassantTargetSquare = enPassantTargetSquare;
    this.halfMoveClock = halfMoveClock;
    this.fullMoveNumber = fullMoveNumber;
  }

  public List<PiecePosition> getPieces() {
    return pieces;
  }

  public PieceColor getActiveColor() {
    return activeColor;
  }
  public PieceColor getNotActiveColor() {

    return getActiveColor() == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;
  }

  public CastlingAvailability getCastlingAvailability() {
    return castlingAvailability;
  }

  public EnPassantTargetSquare getEnPassantTargetSquare() {
    return enPassantTargetSquare;
  }

  public int getHalfMoveClock() {
    return halfMoveClock;
  }

  public int getFullMoveNumber() {
    return fullMoveNumber;
  }

  @Override
  public String toString() {
    return "GameState{"
        + "pieces="
        + pieces
        + ", activeColor="
        + activeColor
        + ", castlingAvailability="
        + castlingAvailability
        + ", enPassantTargetSquare="
        + enPassantTargetSquare
        + ", halfMoveClock="
        + halfMoveClock
        + ", fullMoveNumber="
        + fullMoveNumber
        + '}';
  }
}
