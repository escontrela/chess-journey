package com.davidp.chessjourney.domain.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.davidp.chessjourney.domain.ChessBoard;
import com.davidp.chessjourney.domain.ChessBoardFactory;
import com.davidp.chessjourney.domain.ChessRules;
import com.davidp.chessjourney.domain.services.PGNService;
import com.davidp.chessjourney.domain.services.PGNServiceFactory;
import org.junit.jupiter.api.Test;

public class PGNTest {

  PGNService pgnService = PGNServiceFactory.getPGNService();

  @Test
  public void toAlgebraicTest() {

    ChessBoard chessBoard =
        ChessBoardFactory.createFromFEN(
            Fen.createCustom("rnbqkb1r/2pppp1p/p4np1/1pP1P3/8/2N1N3/PP1P1PPP/R1BQKB1R w KQkq b6 0 6"));
    ChessRules chessRules = new ChessRules();

    assertEquals(
        "d4", pgnService.toAlgebraic(Pos.parseString(
                "d2"), Pos.parseString("d4"), chessBoard,chessRules));
    assertEquals(
        "Bc4", pgnService.toAlgebraic(Pos.parseString("f1"), Pos.parseString("c4"), chessBoard,chessRules));
    assertEquals(
        "exf6", pgnService.toAlgebraic(Pos.parseString("e5"), Pos.parseString("f6"), chessBoard,chessRules));


    //The c3 Knight or the e3 ninth, both  can move to d5, the service should have known Nc it's the right one
    assertEquals(
            "Ncd5", pgnService.toAlgebraic(Pos.parseString("c3"), Pos.parseString("d5"), chessBoard,chessRules));

    //En passant test
    assertEquals(
            "cxb6 e.p.", pgnService.toAlgebraic(Pos.parseString("c5"), Pos.parseString("b6"), chessBoard,chessRules));

    // TODO more test with castling and rooks on the same rank
    // TODO more test promoted pieces
  }
}