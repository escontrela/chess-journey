package com.davidp.chessjourney.domain.games.memory;

import static org.junit.jupiter.api.Assertions.*;

import com.davidp.chessjourney.domain.ChessBoard;
import com.davidp.chessjourney.domain.ChessBoardFactory;
import com.davidp.chessjourney.domain.Player;
import com.davidp.chessjourney.domain.common.*;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GuessMemoryGameTest {

  private GuessMemoryGame game;

  @BeforeEach
  void setUp() {

    Player testPlayer = new Player("Test Player", 1L);
    ChessBoard testBoard = ChessBoardFactory.createEmpty();
    TimeControl timeControl = new TimeControl(5, 1);
    ExerciseType exerciseType = new ExerciseType(UUID.randomUUID(), "test", "test");
    DifficultyLevel difficultyLevel = new DifficultyLevel(UUID.randomUUID(), "test", "test");
    List<Exercise> exercises =
        List.of(
            new Exercise(
                UUID.randomUUID(),
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                "",
                exerciseType,
                difficultyLevel));
    game = new GuessMemoryGame(testPlayer, testBoard, timeControl, difficultyLevel, exercises);
  }

  @Test
  void shouldStartGameCorrectly() {
    game.startGame();
    assertEquals(MemoryGame.GameState.SHOWING_PIECES, game.getGameState());
    assertNotNull(game.getHiddenPiecePositions());

    assertFalse(game.getHiddenPiecePositions().isEmpty());
  }

  @Test
  void shouldCorrectlyValidateCorrectGuess() {

    game.startGame();
    PiecePosition hiddenPiece = game.getHiddenPiecePositions().get(0);

    boolean result = game.submitAnswer(hiddenPiece);

    assertTrue(result);
    assertEquals(1, game.getPartialStepCounter());
  }

  @Test
  void shouldTransitionToNextExerciseWhenAllPiecesGuessed() {
    game.startGame();

    for (PiecePosition piece : game.getHiddenPiecePositions()) {

      game.submitAnswer(piece);
    }

    assertTrue(game.isTimeToMoveToNextExercise());
  }

  @Test
  void shouldCalculateSuccessPercentageCorrectly() {

    game.startGame();
    PiecePosition correctPiece = game.getHiddenPiecePositions().get(0);

    game.submitAnswer(correctPiece);

    int expectedPercentage = 100;
    assertEquals(expectedPercentage, game.getSuccessPercentage());
  }
}
