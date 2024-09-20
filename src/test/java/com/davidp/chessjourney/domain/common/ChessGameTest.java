package com.davidp.chessjourney.domain.common;

import static org.junit.jupiter.api.Assertions.*;

import com.davidp.chessjourney.domain.ChessGame;
import com.davidp.chessjourney.domain.ChessGameFactory;
import com.davidp.chessjourney.domain.Player;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChessGameTest {

  Logger logger = LoggerFactory.getLogger(ChessGameTest.class);
  /**
   * <a href="https://www.chessgames.com/perl/chessgame?gid=1078626">Miniature Game for the test
   * (Bill Wall vs V Ortez)</a>
   *
   * <p>1.e4e5 2.f4Bd6 3.Bc4Qf6 4.Nf3Qxf4 5.O-OBc5+ 6.d4Bxd4+ 7.Nxd4Qxe4 8.Bxf7+Ke7 9.Nf5+Kf8
   * 10.Bxg8Kxg8 11.Ne7#1-0
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

      // TODO algebraic notation is need too

      chessGame.move(new Pos(Col.E, Row.TWO), new Pos(Col.E, Row.FOUR));
      chessGame.printBoard();
      verifyGameState(chessGame, PieceColor.BLACK, false, false, false, false);

      // Move 2: e7 to e5
      chessGame.move(new Pos(Col.E, Row.SEVEN), new Pos(Col.E, Row.FIVE));
      chessGame.printBoard();
      verifyGameState(chessGame, PieceColor.WHITE, false, false, false, false);

      // Move 3: g1 to f3
      chessGame.move(new Pos(Col.F, Row.TWO), new Pos(Col.F, Row.FOUR));
      chessGame.printBoard();
      verifyGameState(chessGame, PieceColor.BLACK, false, false, false, false);

      // Move 4: b8 to c6
      chessGame.move(new Pos(Col.F, Row.EIGHT), new Pos(Col.D, Row.SIX));
      chessGame.printBoard();
      verifyGameState(chessGame, PieceColor.WHITE, false, false, false, false);

      // Move 5: f1 to c4
      chessGame.move(new Pos(Col.F, Row.ONE), new Pos(Col.C, Row.FOUR));
      chessGame.printBoard();
      verifyGameState(chessGame, PieceColor.BLACK, false, false, false, false);

      // Move 6: d7 to f6
      chessGame.move(new Pos(Col.D, Row.EIGHT), new Pos(Col.F, Row.SIX));
      chessGame.printBoard();
      verifyGameState(chessGame, PieceColor.WHITE, false, false, false, false);

      // Move 7: G1 to F3
      chessGame.move(new Pos(Col.G, Row.ONE), new Pos(Col.F, Row.THREE));
      chessGame.printBoard();
      verifyGameState(chessGame, PieceColor.BLACK, false, false, false, false);

      // Move 8: F6 to F4 (QxF4)
      chessGame.move(new Pos(Col.F, Row.SIX), new Pos(Col.F, Row.FOUR));
      chessGame.printBoard();

      // TODO VERIFY THE CAPTURE!
      verifyGameState(chessGame, PieceColor.WHITE, false, false, false, false);

    } catch (IllegalMoveException e) {

      logger.error("Illegal move: {}", e.getMessage(), e);
      fail();
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
