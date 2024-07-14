package com.davidp.chessjourney.domain.common;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.davidp.chessjourney.domain.ChessGame;
import com.davidp.chessjourney.domain.ChessGameFactory;
import com.davidp.chessjourney.domain.Player;
import org.junit.jupiter.api.Test;

public class ChessGameTest {

  @Test
  public void gameTest() {

    Player whitePlayer = new Player("Alice", PieceColor.WHITE);
    Player blackPlayer = new Player("Bob", PieceColor.BLACK);

    ChessGame chessGame =
        ChessGameFactory.createFrom(Fen.createInitial(), whitePlayer, blackPlayer, TimeControl.fifteenPlusTen());

    // Print the initial board
    chessGame.printBoard();

    // Simulate some moves
    chessGame.move(whitePlayer, new Pos(Row.TWO, Col.E), new Pos(Row.FOUR, Col.E)); // e2 to e4
    chessGame.printBoard();


    assertFalse(chessGame.isCheck(PieceColor.WHITE));
    assertFalse(chessGame.isCheckmate(PieceColor.WHITE));
    assertFalse(chessGame.isStalemate(PieceColor.WHITE));
    assertFalse(chessGame.isPromoted(PieceColor.WHITE));

    chessGame.move(blackPlayer, new Pos(Row.SEVEN, Col.E), new Pos(Row.FIVE, Col.E)); // e7 to e5
    chessGame.printBoard();
  }
}
