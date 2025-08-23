package com.davidp.chessjourney.domain.games.tactic;

import com.davidp.chessjourney.domain.Player;
import com.davidp.chessjourney.domain.common.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the TacticGame2 class.
 * This class tests the functionality of the TacticGame2 game logic, including starting the game,
 * submitting moves, handling automatic responses, and transitioning between exercises.
 */
public class TacticGameTest {

  private static final DifficultyLevel TEST_DIFFICULTY_LEVEL =
      new DifficultyLevel(
          UUID.fromString("903ec9bd-aeb1-4b01-8fbd-a3ec2dce976f"), "easy", "easy level");

  private static final ExerciseType TEST_EXERCISE_TYPE =
      new ExerciseType(UUID.randomUUID(), "tactic_game", "Tactic Game Exercise");

  private static Player anyPlayer() {
    return new Player("Test", 1L);
  }

  private static TimeControl anyTime() {
    return new TimeControl(1, 0);
  }

  Exercise ex1 =
      new Exercise(
          UUID.fromString("903ec9bd-aeb1-4b01-8fbd-a3ec2dce976f"),
          "8/4r3/8/4k3/8/2K5/2R5/8 w - - 0 1",
          "1. Re2+ Kf6 2. Rxe7",
          TEST_EXERCISE_TYPE,
          TEST_DIFFICULTY_LEVEL);

  Exercise ex2 =
      new Exercise(
          UUID.fromString("903ec9bd-aeb1-4b01-8fbd-a3ec2dce976f"),
          "8/8/8/4k1r1/8/2K5/1R6/8 w - - 0 1",
          "1. Rb5+ Ke6 2. Rxg5",
          TEST_EXERCISE_TYPE,
          TEST_DIFFICULTY_LEVEL);

  @Test
  void startGame_initialState_and_plyCounting() {

    TacticGame game = new TacticGame(anyPlayer(), anyTime(), TEST_DIFFICULTY_LEVEL, List.of(ex1));

    assertEquals(TacticGame.GameState.WAITING_TO_START, game.getGameState());

    game.startGame();

    assertEquals(TacticGame.GameState.AWAITING_USER_MOVE, game.getGameState());
    assertEquals(1, game.getCurrentPlyNumber());
    assertEquals(2, game.getTotalPliesInCurrentExercise()); // ceil(3/2)=2
    assertEquals("Re2+", game.getCurrentExpectedMove());
  }

  @Test
  void happyPath_withAutoMove_and_finalMove_noAuto_leadsToGameOver() {
    TacticGame game = new TacticGame(anyPlayer(), anyTime(), TEST_DIFFICULTY_LEVEL, List.of(ex1));
    game.startGame();

    // Ply 1 (usuario): "Re2+"
    assertTrue(game.submitMove("Re2+"));
    // Hay respuesta automática "Kf6"
    assertEquals(TacticGame.GameState.AWAITING_ENGINE_MOVE, game.getGameState());
    assertTrue(game.hasPendingAutoMove());
    assertEquals("Kf6", game.peekPendingAutoMove());

    // La UI aplica el auto-move y lo consume
    game.consumePendingAutoMove();
    assertEquals(TacticGame.GameState.AWAITING_USER_MOVE, game.getGameState());
    assertEquals(2, game.getCurrentPlyNumber());
    assertEquals("Rxe7", game.getCurrentExpectedMove());

    // Ply 2 (usuario): "Rxe7" (último SAN, sin respuesta automática)
    assertTrue(game.submitMove("Rxe7"));

    // Como no hay más ejercicios, el juego debería terminar
    assertEquals(TacticGame.GameState.GAME_OVER, game.getGameState());
    assertEquals(1, game.getCorrectExercises());
    assertEquals(0, game.getFailedExercises());
  }

  @Test
  void wrongFirstMove_marksFailed_and_readyForNextExercise() {

    TacticGame game =
        new TacticGame(anyPlayer(), anyTime(), TEST_DIFFICULTY_LEVEL, List.of(ex1, ex2));
    game.startGame();

    // Esperaba "Re2+", jugamos mal:
    assertFalse(game.submitMove("Re3"));


    // Hay más ejercicios, así que quedamos listos para cargar el siguiente
    assertEquals(TacticGame.GameState.READY_FOR_NEXT_EXERCISE, game.getGameState());
    assertEquals(0, game.getCorrectExercises());
    assertEquals(1, game.getFailedExercises());

    // La UI decide pasar al siguiente ejercicio:
    game.startNextExercise();
    assertEquals(TacticGame.GameState.AWAITING_USER_MOVE, game.getGameState());
    assertEquals(1, game.getCurrentPlyNumber());
    assertEquals("Rb5+", game.getCurrentExpectedMove());
  }

  @Test
  void finishingLastExercise_setsGameOver() {

    TacticGame game = new TacticGame(anyPlayer(), anyTime(), TEST_DIFFICULTY_LEVEL, List.of(ex1));
    game.startGame();

    assertTrue(game.submitMove("Re2+"));

    assertEquals(TacticGame.GameState.AWAITING_ENGINE_MOVE, game.getGameState());
    assertTrue(game.hasPendingAutoMove());
    assertEquals("Kf6", game.peekPendingAutoMove());

    // La UI aplica el auto-move y lo consume
    game.consumePendingAutoMove();
    assertTrue(game.submitMove("Rxe7"));


    assertEquals(TacticGame.GameState.GAME_OVER, game.getGameState());
    assertEquals(1, game.getCorrectExercises());
    assertEquals(0, game.getFailedExercises());
  }
}
