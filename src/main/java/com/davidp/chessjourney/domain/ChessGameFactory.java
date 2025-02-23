package com.davidp.chessjourney.domain;

import com.davidp.chessjourney.domain.common.Fen;
import com.davidp.chessjourney.domain.common.TimeControl;
import com.davidp.chessjourney.domain.games.memory.MemoryGame;

import java.util.List;

public class ChessGameFactory {

  public static ChessGame createFrom(
      Fen fen, Player player1, Player player2, TimeControl timeControl) {

    ChessBoard chessBoard = ChessBoardFactory.createFromFEN(fen);
    ChessRules rules = new ChessRules();

    return new ChessGame(player1, player2, rules, chessBoard, timeControl);
  }

  public static MemoryGame createMemoryGameFrom(final Player player, final TimeControl timeControl
          , final List<Fen> positions){

      if (positions == null || positions.isEmpty()) {

          throw new IllegalArgumentException("Positions list cannot be null or empty");
      }
      ChessBoard chessBoard = ChessBoardFactory.createFromFEN(positions.get(0));
      return new MemoryGame(player,chessBoard,timeControl,positions);

  }
}