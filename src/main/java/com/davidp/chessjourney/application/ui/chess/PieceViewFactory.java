package com.davidp.chessjourney.application.ui.chess;

import com.davidp.chessjourney.domain.common.PieceColor;
import com.davidp.chessjourney.domain.common.PieceType;

/** This factory create Chess Pieces on the view, from model data. */
public class PieceViewFactory {

  public static PieceView getPiece(final PieceType pieceType, final PieceColor color) {

    String viewColor = (color == PieceColor.WHITE ? "white" : "black");

    switch (pieceType) {
      case KING:
        return new King(viewColor);

      case PAWN:
        return new Pawn(viewColor);

      case ROOK:
        return new Rook(viewColor);

      case BISHOP:
        return new Bishop(viewColor);

      case QUEEN:
        return new Queen(viewColor);

      case KNIGHT:
        return new Knight(viewColor);
    }

    throw new RuntimeException("No piece found!");
  }
}
