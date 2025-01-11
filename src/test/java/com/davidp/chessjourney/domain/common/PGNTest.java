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

  //TODO split this test on each case...
  @Test
  public void toAlgebraicTest() {

    ChessBoard chessBoard =
        ChessBoardFactory.createFromFEN(
            Fen.createCustom("rnbqkb1r/2pppp1p/p4np1/1pP1P3/8/2N1N3/PP1P1PPP/R1BQKB1R w KQkq b6 0 6"));

    ChessRules chessRules = new ChessRules();

    assertEquals(
        "d4", pgnService.toAlgebraic(Pos.parseString(
                "d2"), Pos.parseString("d4"), chessBoard,chessRules,null));
    assertEquals(
        "Bc4", pgnService.toAlgebraic(Pos.parseString("f1"), Pos.parseString("c4"), chessBoard,chessRules,null));
    assertEquals(
        "exf6", pgnService.toAlgebraic(Pos.parseString("e5"), Pos.parseString("f6"), chessBoard,chessRules,null));


    //The c3 Knight or the e3 Knight, both can move to d5, the service should have known Nc it's the right one
    assertEquals(
            "Ncd5", pgnService.toAlgebraic(Pos.parseString("c3"), Pos.parseString("d5"), chessBoard,chessRules,null));

    //En passant test
    assertEquals(
            "cxb6 e.p.", pgnService.toAlgebraic(Pos.parseString("c5"), Pos.parseString("b6"), chessBoard,chessRules,null));


    //Rooks on the same rank test
    ChessBoard chessBoardPos2 =
            ChessBoardFactory.createFromFEN(Fen.createCustom("4k2r/1r3n2/3p4/8/8/2N2P2/R6R/2K5 w - - 0 1"));

    assertEquals( "Rad2" , pgnService.toAlgebraic(Pos.parseString("a2"),Pos.parseString("d2"),chessBoardPos2,chessRules,null));
    assertEquals( "Rhg2" , pgnService.toAlgebraic(Pos.parseString("h2"),Pos.parseString("g2"),chessBoardPos2,chessRules,null));

    //Castle test
    ChessBoard chessBoardPos3 =
            ChessBoardFactory.createFromFEN(Fen.createCustom("r2qk2r/8/8/8/8/8/3Q4/R3K2R w KQkq - 0 1"));

    assertEquals( "O-O" , pgnService.toAlgebraic(Pos.parseString("e1"),Pos.parseString("g1"),chessBoardPos3,chessRules,null));
    assertEquals( "O-O-O" , pgnService.toAlgebraic(Pos.parseString("e1"),Pos.parseString("c1"),chessBoardPos3,chessRules,null));


    //Promote piece test
    ChessBoard chessBoardPos4 =
            ChessBoardFactory.createFromFEN(Fen.createCustom("1r6/P2k2P1/8/8/8/8/8/4K3 w - - 0 1"));

    assertEquals( "g8=Q" , pgnService.toAlgebraic(Pos.parseString("g7"),Pos.parseString("g8"),chessBoardPos4,chessRules,PieceType.QUEEN));
    assertEquals( "axb8=B" , pgnService.toAlgebraic(Pos.parseString("a7"),Pos.parseString("b8"),chessBoardPos4,chessRules,PieceType.BISHOP));
    //TODO en esta posicion anterior se puede coronar con jaque, porque lo podría ser axb8=N+, con un caballo por ejemplo, probar este caso....

    //TODO test para check!!!

    //TODO test para jaque mate///

    //TODO test para ... tablas??


  }
}