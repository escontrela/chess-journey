package com.davidp.chessjourney.domain;

import com.davidp.chessjourney.domain.common.*;

public class ChessGame extends Game {

  private final Player whitePlayer;
  private final Player blackPlayer;
  private final ChessRules chessRules;
  private final TimeControl timeControl;
  private PieceColor currentTurnColor;

  public ChessGame(
      Player whitePlayer,
      Player blackPlayer,
      ChessRules rules,
      ChessBoard board,
      TimeControl timeControl) {

    super();
    this.chessRules = rules;
    this.chessBoard = board;
    this.whitePlayer = whitePlayer;
    this.blackPlayer = blackPlayer;
    this.timeControl = timeControl;
    this.currentTurnColor = PieceColor.WHITE;
  }

  public boolean isCheck() {
    // Dummy implementation for check detection
    return false;
  }

  public boolean isCheckmate() {
    // Dummy implementation for checkmate detection
    return false;
  }

  public boolean isStalemate() {
    // Dummy implementation for stalemate detection
    return false;
  }

  public boolean offerDraw(Player player) {
    // Dummy implementation for offering a draw
    return true;
  }

  public boolean promotePawn(Piece piece, PieceType newType) {
    // Dummy implementation for pawn promotion
    return true;
  }

  // TODO: implement this method
  public PieceColor getCurrentTurnColor() {
    return currentTurnColor;
  }

  public void move(final Pos from, final Pos to) throws IllegalMoveException {

    // TODO WEE NED TO USE THE CHESS RULES AND USE THE CHESSSPRESSO LIBRARY INTERNALLY!

    // Check if the move is valid
    if (!chessRules.isValidMove(from, to)) {

      throw new IllegalMoveException("Invalid move from " + from + " to " + to);
    }

    if (chessBoard.isThereAnyPiece(from).isEmpty()) {

      throw new RuntimeException("There is no piece in the position " + from);
    }

    // Perform the move
    Piece piece = chessBoard.getPiece(from).getPiece();

    if (piece != null && piece.getColor() == getCurrentTurnColor()) {

      chessBoard.movePiece(piece, from, to);

      // Check for check, checkmate, and stalemate
      if (isCheck()) {

        System.out.println(getCurrentTurnColor() + " is in check!");
      }
      if (isCheckmate()) {

        System.out.println("Checkmate! " + getCurrentTurnColor() + " loses.");
      }
      if (isStalemate()) {

        System.out.println("Stalemate! It's a draw.");
      }

      currentTurnColor =
          getCurrentTurnColor() == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;

      //TODO ADD MOVE TO THE GAME HISTORY!

    } else {

      throw new IllegalMoveException(
          "The piece " + piece + " is not the current turn color " + getCurrentTurnColor());
    }
  }

  public void printBoard() {

    // Dummy implementation for printing the board
    System.out.println(chessBoard);
  }

  public boolean isPromoted() {
    // TODO implement this method
    return false;
  }
}
