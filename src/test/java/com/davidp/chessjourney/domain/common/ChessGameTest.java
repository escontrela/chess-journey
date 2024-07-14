package com.davidp.chessjourney.domain.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.davidp.chessjourney.domain.ChessGame;
import com.davidp.chessjourney.domain.ChessGameFactory;
import com.davidp.chessjourney.domain.Player;
import org.junit.jupiter.api.Test;

public class ChessGameTest {

  /**
   * <a href="https://www.chessgames.com/perl/chessgame?gid=1078626">Miniature Game for the test (Bill Wall vs V Ortez)</a>
   *
   * 1.e4e5 2.f4Bd6 3.Bc4Qf6 4.Nf3Qxf4 5.O-OBc5+ 6.d4Bxd4+ 7.Nxd4Qxe4 8.Bxf7+Ke7 9.Nf5+Kf8 10.Bxg8Kxg8 11.Ne7#1-0
   */
  @Test
  public void gameTest() {

    Player whitePlayer = new Player("Alice");
    Player blackPlayer = new Player("Bob");

    ChessGame chessGame =
        ChessGameFactory.createFrom(
            Fen.createInitial(), whitePlayer, blackPlayer, TimeControl.fifteenPlusTen());

    // Print the initial board
    chessGame.printBoard();

    // Verify initial turn is for white
    assertEquals(PieceColor.WHITE, chessGame.getCurrentTurnColor());

    try {

      //TODO algebraic notation is need too
      //TODO invert pos y row first col , after row

      chessGame.move(new Pos(Row.TWO, Col.E), new Pos(Row.FOUR, Col.E));
      chessGame.printBoard();
      verifyGameState(chessGame, PieceColor.BLACK, false, false, false, false);

      // Move 2: e7 to e5
      chessGame.move(new Pos(Row.SEVEN, Col.E), new Pos(Row.FIVE, Col.E));
      chessGame.printBoard();
      verifyGameState(chessGame, PieceColor.WHITE, false, false, false, false);

      // Move 3: g1 to f3
      chessGame.move(new Pos(Row.TWO, Col.F), new Pos(Row.FOUR, Col.F));
      chessGame.printBoard();
      verifyGameState(chessGame, PieceColor.BLACK, false, false, false, false);

      // Move 4: b8 to c6
      chessGame.move(new Pos(Row.EIGHT, Col.F), new Pos(Row.SIX, Col.D));
      chessGame.printBoard();
      verifyGameState(chessGame, PieceColor.WHITE, false, false, false, false);

      // Move 5: f1 to c4
      chessGame.move(new Pos(Row.ONE, Col.F), new Pos(Row.FOUR, Col.C));
      chessGame.printBoard();
      verifyGameState(chessGame, PieceColor.BLACK, false, false, false, false);

      // Move 6: d7 to f6
      chessGame.move(new Pos(Row.EIGHT, Col.D), new Pos(Row.SIX, Col.F));
      chessGame.printBoard();
      verifyGameState(chessGame, PieceColor.WHITE, false, false, false, false);

      // Move 7: G1 to F3
      chessGame.move(new Pos(Row.ONE, Col.G), new Pos(Row.THREE, Col.F));
      chessGame.printBoard();
      verifyGameState(chessGame, PieceColor.BLACK, false, false, false, false);

      // Move 8: F6 to F4
      chessGame.move(new Pos(Row.SIX, Col.F), new Pos(Row.FOUR, Col.F));
      chessGame.printBoard();
      //TODO VERIFY THE CAPTURE!
      verifyGameState(chessGame, PieceColor.WHITE, false, false, false, false);

    } catch (IllegalMoveException e) {

      System.out.println("Illegal move: " + e.getMessage());
      // More assertions can be added as needed...
    }
  }

  private void verifyGameState(
      ChessGame chessGame,
      PieceColor expectedCurrentPlayer,
      boolean isCheck,
      boolean isCheckmate,
      boolean isStalemate,
      boolean isPromotion) {
    assertEquals(expectedCurrentPlayer, chessGame.getCurrentTurnColor());
    assertEquals(isCheck, chessGame.isCheck());
    assertEquals(isCheckmate, chessGame.isCheckmate());
    assertEquals(isStalemate, chessGame.isStalemate());
    assertEquals(isPromotion, chessGame.isPromoted());
  }
}
