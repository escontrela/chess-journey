package com.davidp.chessjourney.domain.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.davidp.chessjourney.domain.ChessBoardFactory;
import com.davidp.chessjourney.domain.ChessBoard;
import org.junit.jupiter.api.Test;

public class ChessBoardTest {

  @Test
  public void createChessBoardTest() {

    ChessBoard chessBoard = ChessBoardFactory.createEmpty();
    chessBoard.addPiece(PieceFactory.createWhiteQueen(), Pos.parseString("e2"));
    assertTrue(chessBoard.isThereAnyPiece(Pos.parseString("e2")).isPresent());
    assertEquals(
        PieceFactory.createWhiteQueen(),
        chessBoard.isThereAnyPiece(Pos.parseString("e2")).get().getPiece());
  }

  @Test
  public void createChessBoardFromFenTest() {

    ChessBoard chessBoard = ChessBoardFactory.createFromFEN(Fen.createInitial());

    assertTrue(chessBoard.isThereAnyPiece(Pos.parseString("e1")).isPresent());
    assertEquals(
        PieceFactory.createWhiteKing(),
        chessBoard.isThereAnyPiece(Pos.parseString("e1")).get().getPiece());
  }
}
