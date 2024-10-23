package com.davidp.chessjourney.domain;

import com.davidp.chessjourney.domain.common.Fen;
import com.davidp.chessjourney.domain.common.TimeControl;

public class ChessGameFactory {

  public static ChessGame createFrom(
      Fen fen, Player player1, Player player2, TimeControl timeControl) {

    ChessBoard chessBoard = ChessBoardFactory.createFromFEN(fen);
    ChessRules rules = new ChessRules();

    return new ChessGame(player1, player2, rules, chessBoard, timeControl);
  }
}
