package com.davidp.chessjourney.domain.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.davidp.chessjourney.domain.ChessBoard;
import com.davidp.chessjourney.domain.ChessBoardFactory;
import com.davidp.chessjourney.domain.services.PGNService;
import com.davidp.chessjourney.domain.services.PGNServiceFactory;
import org.junit.jupiter.api.Test;

public class PGNTest {

  PGNService pgnService = PGNServiceFactory.getPGNService();

  @Test
  public void toAlgebraicTest() {

    ChessBoard chessBoard =
        ChessBoardFactory.createFromFEN(
            Fen.createCustom("rnbqkb1r/pppppp1p/5np1/4P3/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3"));

    assertEquals(
        "d4", pgnService.toAlgebraic(Pos.parseString("d2"), Pos.parseString("d4"), chessBoard));
    assertEquals(
        "Bc4", pgnService.toAlgebraic(Pos.parseString("f1"), Pos.parseString("c4"), chessBoard));
    assertEquals(
        "exf6", pgnService.toAlgebraic(Pos.parseString("e5"), Pos.parseString("f6"), chessBoard));
    // TODO test with en passant and castling and rooks on the same rank
    // TODO test promoted pieces
  }
}
