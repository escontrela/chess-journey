package com.davidp.chessjourney.domain;

import com.davidp.chessjourney.domain.common.*;
import com.davidp.chessjourney.domain.services.FenService;
import com.davidp.chessjourney.domain.services.FenServiceFactory;
import java.util.*;

/**
 * This class represents a chess game. It contains a chess board, a chess rules object, and a time.
 * ChessGame is responsible for managing the overall game state.
 */
public class ChessGame extends Game {

  private final FenService fenService =
      FenServiceFactory.getFenService(); // TODO move to the constructor
  private final Player whitePlayer;
  private final Player blackPlayer;
  private final ChessRules chessRules;
  private final TimeControl timeControl;
  private PieceColor currentTurnColor;
  private final Map<Player, Set<PiecePosition>> capturedPieces;

  /**
   * TOOD the game state and FEN should be part of the ChessGame class, not the ChessBoard class!!!.
   * Please, do the refactoring.
   *
   * @param whitePlayer
   * @param blackPlayer
   * @param rules
   * @param board
   * @param timeControl
   */
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

    this.currentTurnColor = setInitialStateOfTurnColor(chessBoard.getFen());
    this.capturedPieces =
        setInitialStateOfCapturedPieces(whitePlayer, blackPlayer, chessBoard.getFen());

    // TODO set the rest of the initial state of the game
  }

  private Map<Player, Set<PiecePosition>> setInitialStateOfCapturedPieces(
      Player whitePlayer, Player blackPlayer, Fen fen) {

    /*
      TODO if the game is not in the initial position,
      the capturedPieces should be initialized with the captured pieces from the initial position
    */

    final Map<Player, Set<PiecePosition>> capturedPieces = new HashMap<>();
    capturedPieces.put(whitePlayer, new HashSet<>());
    capturedPieces.put(blackPlayer, new HashSet<>());
    return capturedPieces;
  }

  private PieceColor setInitialStateOfTurnColor(final Fen fen) {

    GameState gameState = fenService.parseString(fen);
    return gameState.getActiveColor();
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

  public PieceColor getCurrentTurnColor() {
    return currentTurnColor;
  }

  public Player getCurrentPlayer() {

    return currentTurnColor == PieceColor.WHITE ? whitePlayer : blackPlayer;
  }

  public Player getOpponentPlayer() {

    return currentTurnColor == PieceColor.WHITE ? blackPlayer : whitePlayer;
  }

  public void move(final Pos from, final Pos to) throws IllegalMoveException {

    // TODO WEE NED TO USE THE CHESS RULES AND USE THE CHESSSPRESSO LIBRARY INTERNALLY!

    // TODO update the game status -> moves, captures, etc.

    if (chessBoard.isThereAnyPiece(from).isEmpty()) {

      throw new RuntimeException("There is no piece in the position " + from);
    }

    // Check if the move is valid
    if (!chessRules.isValidMove(from, to, chessBoard.getFen())) {

      throw new IllegalMoveException("Invalid move from " + from + " to " + to);
    }

    // Perform the move
    Piece piece = chessBoard.getPiece(from).getPiece();

    if (piece != null && piece.getColor() == getCurrentTurnColor()) {

      /* The board is not the responsible for the move, it's the chess rules,
      so we need to detect if there are any capture on this move! */

      if (isCapture(to)) {

        PiecePosition capturedPiece = chessBoard.getPiece(to);
        capturedPieces.get(getCurrentPlayer()).add(capturedPiece);
      }

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

      chessBoard.setTurn(currentTurnColor);

      // TODO ADD MOVE TO THE GAME HISTORY!

    } else {

      throw new IllegalMoveException(
          "The piece " + piece + " is not the current turn color " + getCurrentTurnColor());
    }
  }

  public boolean isCapture(Pos to) {

    return chessBoard.isThereAnyPiece(to).isPresent();
  }

  public void printBoard() {

    // Dummy implementation for printing the board
    System.out.println(chessBoard);
  }

  public boolean isPromoted() {
    // TODO implement this method
    return false;
  }

  public Collection<PiecePosition> getCapturedPiecesForPlayer(Player player) {

    return capturedPieces.get(player);
  }
}
