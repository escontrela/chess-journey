package com.davidp.chessjourney.domain;

import com.davidp.chessjourney.domain.common.DifficultyLevel;
import com.davidp.chessjourney.domain.common.Exercise;
import com.davidp.chessjourney.domain.common.Fen;
import com.davidp.chessjourney.domain.common.TimeControl;
import com.davidp.chessjourney.domain.games.memory.DefendMemoryGame;
import com.davidp.chessjourney.domain.games.memory.GuessMemoryGame;
import com.davidp.chessjourney.domain.games.memory.MemoryGame;
import java.util.List;

public class ChessGameFactory {

  public static ChessGame createFrom(
      Fen fen, Player player1, Player player2, TimeControl timeControl) {

    ChessBoard chessBoard = ChessBoardFactory.createFromFEN(fen);
    ChessRules rules = new ChessRules();

    return new ChessGame(player1, player2, rules, chessBoard, timeControl);
  }



    public static GuessMemoryGame createGuessMemoryGameFrom(final Player player, final TimeControl timeControl
            , DifficultyLevel difficultyLevel, List<Exercise> exercises) {

        if (exercises == null || exercises.isEmpty()) {

            throw new IllegalArgumentException("Positions list cannot be null or empty");
        }
        ChessBoard chessBoard = ChessBoardFactory.createFromFEN(Fen.createCustom(exercises.get(0).getFen()));
        return new GuessMemoryGame(player, chessBoard, timeControl, difficultyLevel, exercises);
    }

    public static MemoryGame<String> createDefendMemoryGameFrom(Player player, TimeControl timeControl,
                                                                DifficultyLevel level, List<Exercise> exercises) {

        if (exercises == null || exercises.isEmpty()) {

            throw new IllegalArgumentException("Positions list cannot be null or empty");
        }
        ChessBoard chessBoard = ChessBoardFactory.createFromFEN(Fen.createCustom(exercises.get(0).getFen()));
        return new DefendMemoryGame(player, chessBoard, timeControl, level, exercises);
    }
}