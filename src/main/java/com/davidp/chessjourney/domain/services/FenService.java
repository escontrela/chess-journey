package com.davidp.chessjourney.domain.services;

import com.davidp.chessjourney.domain.common.*;
import java.util.List;

/**
 * This class is responsible for creating a Fen object from a string or from a game status.
 */
public interface FenService {

  /**
   * Creates a Fen object from a string.
   * @param fen
   * @return
   */
  FenParserResponse parseString(Fen fen);

  /**
   * Creates a Fen object from a game status.
   * @param fenParserResponse
   * @return
   */
  Fen parseActualStatus(FenParserResponse fenParserResponse);

  class FenParserResponse {

    final List<PiecePosition> pieces;
    final PieceColor activeColor;
    final CastlingAvailability castlingAvailability;
    final EnPassantTargetSquare enPassantTargetSquare;
    final int halfMoveClock;
    final int fullMoveNumber;

    protected FenParserResponse(
        List<PiecePosition> pieces,
        PieceColor activeColor,
        CastlingAvailability castlingAvailability,
        EnPassantTargetSquare enPassantTargetSquare,
        int halfMoveClock,
        int fullMoveNumber) {
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
  }

  class PiecePosition {

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
  }
}
