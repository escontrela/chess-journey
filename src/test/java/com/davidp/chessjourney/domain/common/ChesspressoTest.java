package com.davidp.chessjourney.domain.common;

import static org.junit.jupiter.api.Assertions.*;

import chesspresso.Chess;
import chesspresso.game.Game;
import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;
import chesspresso.position.Position;
import org.junit.jupiter.api.Test;

/**
 * Some util information about the chesspresso library. <a
 * href="http://javaen.blogspot.com/2016/05/what-about-chesspresso.html">...</a> <a
 * href="http://www.chesspresso.org">...</a> <a
 * href="http://www.chesspresso.org/javadoc/index.html">...</a>
 */
public class ChesspressoTest {

  @Test
  public void baseGameWithSquareMovesTest() {

    Game chessGame = new Game();

    addMove(chessGame, Chess.E2, Chess.E4, false, "Controlando el centro");
    addMove(chessGame, Chess.E7, Chess.E5, false, "Clasica respuesta");

    addMove(chessGame, Chess.G1, Chess.F3, false, null);
    addMove(chessGame, Chess.B8, Chess.C6, false, null);

    addMove(chessGame, Chess.F1, Chess.B5, false, "Apertura Ruy Lopez");
    addMove(chessGame, Chess.A7, Chess.A6, false, null);

    assertEquals(
        "r1bqkbnr/1ppp1ppp/p1n5/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 4",
        chessGame.getPosition().toString());
  }

  @Test
  public void basicGameTest() {

    Game game = new Game();
    Position position = game.getPosition();
    try {

      int e2 = Chess.strToSqi("e2");
      int e4 = Chess.strToSqi("e4");
      short move = Move.getRegularMove(e2, e4, false);
      position.doMove(move);

      assertEquals("e2-e4", Move.getString(move));
      assertTrue(position.isLegal());
      assertTrue(Move.isValid(move));

      for (short mv : position.getAllMoves()) {

        System.out.println("Valid next  move:" + Move.getString(mv));
      }

    } catch (IllegalMoveException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void invalidMoveTest() {

    Game game = new Game();
    Position position = game.getPosition();
    try {

      int e2 = Chess.strToSqi("e2");
      int e4 = Chess.strToSqi("e8");
      short move = Move.getRegularMove(e2, e4, false);
      position.doMove(move);
      assertFalse(isLegalMove(position, move));

    } catch (IllegalMoveException e) {
      throw new RuntimeException(e);
    }
  }

  private void addMove(
      Game chessGame,
      int fromSquareIndex,
      int toSquareIndex,
      boolean isCapturingMove,
      String comment) {
    try {
      short move = Move.getRegularMove(fromSquareIndex, toSquareIndex, isCapturingMove);
      if (Move.isValid(move)) {
        chessGame.getPosition().doMove(move);
        if (comment != null && !comment.isEmpty()) chessGame.addComment(comment);
      }
    } catch (IllegalMoveException e) {
      e.printStackTrace();
    }
  }

  private static boolean isLegalMove(Position position, short move) {

    short[] legalMoves = position.getAllMoves();
    for (short legalMove : legalMoves) {
      if (move == legalMove) {
        return true;
      }
    }

    return false;
  }
}
