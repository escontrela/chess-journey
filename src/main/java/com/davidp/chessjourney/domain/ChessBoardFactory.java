package com.davidp.chessjourney.domain;

import com.davidp.chessjourney.domain.common.Fen;
import com.davidp.chessjourney.domain.common.GameState;
import com.davidp.chessjourney.domain.common.PiecePosition;
import com.davidp.chessjourney.domain.services.FenServiceFactory;
import java.util.Collection;

/**
 * This class is responsible for creating a ChessBoard object from a FEN string or from a collection
 * of PiecePosition objects.
 */
public class ChessBoardFactory {

  public static ChessBoard createFrom(Collection<PiecePosition> piecePositions) {

    ChessBoard chessBoard = new ChessBoard();
    piecePositions.forEach(pp -> chessBoard.addPiece(pp.getPiece(), pp.getPosition()));

    return chessBoard;
  }

  public static ChessBoard createEmpty() {

    return new ChessBoard();
  }

  public static ChessBoard createFromFEN(Fen fen) {

    GameState gameState = FenServiceFactory.getFenService().parseString(fen);
    ChessBoard chessBoard = new ChessBoard();
    chessBoard.setEnPassantTargetSquare(gameState.getEnPassantTargetSquare());

    ///TODO: important to set the game state before adding the pieces to the board!!!.

    gameState.getPieces().forEach(pp -> chessBoard.addPiece(pp.getPiece(), pp.getPosition()));
    return chessBoard;
  }
}
