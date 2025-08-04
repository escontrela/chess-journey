package com.davidp.chessjourney.domain.common;

import static com.davidp.chessjourney.domain.common.Pos.parseString;
import static com.davidp.chessjourney.domain.services.PGNServiceImpl.extractAllValuableGroups;
import static com.davidp.chessjourney.domain.services.PGNServiceImpl.extractValuableGroups;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.davidp.chessjourney.domain.ChessBoard;
import com.davidp.chessjourney.domain.ChessBoardFactory;
import com.davidp.chessjourney.domain.ChessRules;
import com.davidp.chessjourney.domain.services.PGNService;
import com.davidp.chessjourney.domain.services.PGNServiceFactory;
import com.davidp.chessjourney.domain.services.PGNServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

public class PGNServiceTest {

  PGNService pgnService = PGNServiceFactory.getPGNService();

  /**
   * This test converts standard algebraic notation (e.g., e4, Nf3) into algebraic coordinates
   * (e.g., e2, e4)
   */
  @Test
  public void fromAlgebraicTest() {

    ChessBoard chessBoard =
        ChessBoardFactory.createFromFEN(
            Fen.createCustom(
                "r3k2r/ppp1qppp/2n1bn2/2bpp3/2B1P3/1PNPBN2/P1P1QPPP/R3K2R w KQkq - 0 9"));

    ChessRules chessrules = new ChessRules();

    // Castling test
    GameMove posChessMove = pgnService.fromAlgebraic("O-O", chessBoard);

    /*
    * Se podría hacer también (si bien se opta por el método en la interfaz) :
    * if (posChessMove instanceof CastlingMove castle) {
    *     // dentro de este bloque `castle` ya está tipado
    *     assertEquals("E1", castle.kingMove().from().toString());
    *     assertEquals("G1", castle.kingMove().to().toString());
    *     assertEquals("H1", castle.rookMove().from().toString());
    *     assertEquals("F1", castle.rookMove().to().toString());
    * } else {
    *     fail("Se esperaba un CastlingMove");
    * }
    *
    * También se aceptar switch:
    * GameMove mv = Factory.createCastlingMove(CastlingType.KINGSIDE, true, false);
       switch (mv) {
         case CastlingMove cm -> handleCastling(cm);
         case NormalMove nm   -> handleNormal(nm);
    * }
    */

    CastlingMove castle =
        posChessMove.asCastling().orElseThrow(() -> new AssertionError("No es CastlingMove"));

    assertEquals("E1", castle.kingMove().getFrom().toString());
    assertEquals("G1", castle.kingMove().getTo().toString());
    assertEquals("H1", castle.rookMove().getFrom().toString());
    assertEquals("F1", castle.rookMove().getTo().toString());

    // TODO verificar todos los castling moves desdde distintos FEN positions (Quuenside, kingside
    // con white y con black).
  }

  /** Test for groups 3 and 6 */
  @Test
  public void fromAlgebraicTest2() {

    ChessBoard chessBoard =
        ChessBoardFactory.createFromFEN(
            Fen.createCustom(
                "r3k2r/ppp1qppp/2n1bn2/2bpp3/2B1P3/1PNPBN2/P1P1QPPP/R3K2R w KQkq - 0 9"));

    ChessRules chessrules = new ChessRules();

    // Castling test
    GameMove posChessMove = pgnService.fromAlgebraic("Ng5", chessBoard);

    RegularMove regular =
        posChessMove.asRegular().orElseThrow(() -> new AssertionError("No es Regular move"));

    assertEquals("F3", regular.getMoves().getFirst().getFrom().toString());
    assertEquals("G5", regular.getMoves().getFirst().getTo().toString());
  }

  @Test
  public void testExtractSpecificGroupsOld() {
    Pattern regularNotationPattern =
        Pattern.compile(
            "^(?:(O-O(?:-O)?)([+#])?|([NBRQK])?([a-h1-8])?(x)?([a-h][1-8])(?:=([NBRQK]))?([+#])?)$");

    // Extract only piece and destination groups
    Map<PGNServiceImpl.PGNRegExprGroups, String> groups =
        extractValuableGroups(
            "Nf3",
            regularNotationPattern,
            PGNServiceImpl.PGNRegExprGroups.PIECE_GROUP_3,
            PGNServiceImpl.PGNRegExprGroups.DESTINATION_GROUP_6);

    assertEquals("N", groups.get(PGNServiceImpl.PGNRegExprGroups.PIECE_GROUP_3));
    assertEquals("f3", groups.get(PGNServiceImpl.PGNRegExprGroups.DESTINATION_GROUP_6));
    assertEquals(2, groups.size());
  }

  /**
   * | Movimiento | Grupo 3 (pieza) | Grupo 4 (desambiguación) | Grupo 5 (captura) | Grupo 6
   * (destino) | Grupo 7 (promoción) | Grupo 8 (check/mate) |
   * |-------------|-----------------|--------------------------|-------------------|-------------------|---------------------|----------------------|
   * | c3 | — | — | — | c3 | — | — | | Nf3 | N | — | — | f3 | — | — | | Nxd4 | N | — | x | d4 | — |
   * — | | Qa4+ | Q | — | — | a4 | — | + | | Rhf1 | R | h | — | f1 | — | — | | R1h4 | R | 1 | — | h4
   * | — | — | | gxf3 | — | g | x | f3 | — | — | | fxe6+ | — | f | x | e6 | — | + | | gxf8=Q+ | — |
   * g | x | f8 | Q | + | | N5xf4 | N | 5 | x | f4 | — | — | | Ndxf4 | N | d | x | f4 | — | — | |
   * Rhxe4 | R | h | x | e4 | — | — | | R2xf4 | R | 2 | x | f4 | — | — |
   */

  /**
   * | Movimiento | Grupo 1 (Enroque) | Grupo 2 (check/mate) |
   * |------------|-------------------|----------------------| | O-O | O-O | — | | O-O+ | O-O | + |
   * | O-O-O | O-O-O | — | | O-O-O# | O-O-O | # |
   */
  @Test
  public void testExtractValuableGroups() {
    Pattern regularNotationPattern =
        Pattern.compile(
            "^(?:(O-O(?:-O)?)([+#])?|([NBRQK])?([a-h1-8])?(x)?([a-h][1-8])(?:=([NBRQK]))?([+#])?)$");

    // Test case: "Nf3"
    Map<PGNServiceImpl.PGNRegExprGroups, String> groups =
        extractAllValuableGroups("Nf3", regularNotationPattern);

    assertEquals("N", groups.get(PGNServiceImpl.PGNRegExprGroups.PIECE_GROUP_3));
    assertEquals("f3", groups.get(PGNServiceImpl.PGNRegExprGroups.DESTINATION_GROUP_6));
    assertEquals(2, groups.size()); // Only 2 groups should have content

    // Test case: "Nxd4"
    groups = extractAllValuableGroups("Nxd4", regularNotationPattern);

    assertEquals("N", groups.get(PGNServiceImpl.PGNRegExprGroups.PIECE_GROUP_3));
    assertEquals("x", groups.get(PGNServiceImpl.PGNRegExprGroups.CAPTURE_GROUP_5));
    assertEquals("d4", groups.get(PGNServiceImpl.PGNRegExprGroups.DESTINATION_GROUP_6));
    assertEquals(3, groups.size());

    // Test case: "O-O+"
    groups = extractAllValuableGroups("O-O+", regularNotationPattern);

    assertEquals("O-O", groups.get(PGNServiceImpl.PGNRegExprGroups.CASTLING_GROUP_1));
    assertEquals("+", groups.get(PGNServiceImpl.PGNRegExprGroups.CASTLING_CHECK_OR_MATE_GROUP_2));
    assertEquals(2, groups.size());

    // Test case: "gxf8=Q+"
    groups = extractAllValuableGroups("gxf8=Q+", regularNotationPattern);

    assertEquals("g", groups.get(PGNServiceImpl.PGNRegExprGroups.DISAMBIGUATION_GROUP_4));
    assertEquals("x", groups.get(PGNServiceImpl.PGNRegExprGroups.CAPTURE_GROUP_5));
    assertEquals("f8", groups.get(PGNServiceImpl.PGNRegExprGroups.DESTINATION_GROUP_6));
    assertEquals("Q", groups.get(PGNServiceImpl.PGNRegExprGroups.PROMOTION_GROUP_7));
    assertEquals("+", groups.get(PGNServiceImpl.PGNRegExprGroups.CHECK_OR_MATE_GROUP_8));
    assertEquals(5, groups.size());
  }

  @Test
  public void testExtractSpecificGroups() {
    Pattern regularNotationPattern =
        Pattern.compile(
            "^(?:(O-O(?:-O)?)([+#])?|([NBRQK])?([a-h1-8])?(x)?([a-h][1-8])(?:=([NBRQK]))?([+#])?)$");

    // Extract only piece and destination groups
    Map<PGNServiceImpl.PGNRegExprGroups, String> groups =
        extractValuableGroups(
            "Nf3",
            regularNotationPattern,
            PGNServiceImpl.PGNRegExprGroups.PIECE_GROUP_3,
            PGNServiceImpl.PGNRegExprGroups.DESTINATION_GROUP_6);

    assertEquals("N", groups.get(PGNServiceImpl.PGNRegExprGroups.PIECE_GROUP_3));
    assertEquals("f3", groups.get(PGNServiceImpl.PGNRegExprGroups.DESTINATION_GROUP_6));
    assertEquals(2, groups.size());
  }

  @Test
  public void fromAlgebraicTestRegularExpressionUseCases_2() {

    Pattern regularNotationPatter =
        Pattern.compile("^([NBRQK])?([a-h1-8])?(x)?([a-h][1-8])(?:=([NBRQK]))?([+#])?$");

    // 2. Case Nf3
    Matcher matcher = regularNotationPatter.matcher("Nf3");

    if (matcher.matches()) {

      List<String> matcherGroups = getMatcherGroups(matcher, 6);
      Pos position = Pos.parseString(matcherGroups.get(3));
      String piece = matcherGroups.get(0);

      assertEquals("f3", matcherGroups.get(3));
      assertEquals("N", piece); // TODO cloud I recover the piece instead of the piece string?
      assertEquals(Row.THREE, position.getRow());
      assertEquals(Col.F, position.getCol());
      assertEmptyGroups(matcherGroups, List.of(1, 2, 4, 5));
    }
  }

  @Test
  public void fromAlgebraicTestRegularExpressionUseCases_3() {

    Pattern regularNotationPatter =
        Pattern.compile("^([NBRQK])?([a-h1-8])?(x)?([a-h][1-8])(?:=([NBRQK]))?([+#])?$");

    // 3. Case Nxd4
    Matcher matcher = regularNotationPatter.matcher("Nxd4");

    if (matcher.matches()) {

      List<String> matcherGroups = getMatcherGroups(matcher, 6);
      Pos position = Pos.parseString(matcherGroups.get(3));
      String piece = matcherGroups.get(0);

      String capture = matcherGroups.get(2);

      assertEquals("d4", matcherGroups.get(3));
      assertEquals("N", piece); // TODO cloud I recover the piece instead of the piece string?
      assertEquals(Row.FOUR, position.getRow());
      assertEquals(Col.D, position.getCol());
      assertEquals("x", capture);
      assertEmptyGroups(matcherGroups, List.of(1, 4, 5));
    }
  }

  private List<String> getMatcherGroups(Matcher matcher, int numberOfGroups) {

    List<String> toret = new ArrayList<>();

    IntStream.range(1, numberOfGroups + 1)
        .forEach(
            i -> {
              toret.add(matcher.group(i));
            });

    return toret;
  }

  private void assertEmptyGroups(final List<String> groups, final List<Integer> nonContentGroups) {

    nonContentGroups.forEach(
        groupIndex -> {
          assertTrue(
              Objects.isNull(groups.get(groupIndex))
                  || "".equalsIgnoreCase(groups.get(groupIndex)));
        });
  }

  // TODO split this test on each case...
  @Test
  public void toAlgebraicTest() {

    ChessBoard chessBoard =
        ChessBoardFactory.createFromFEN(
            Fen.createCustom(
                "rnbqkb1r/2pppp1p/p4np1/1pP1P3/8/2N1N3/PP1P1PPP/R1BQKB1R w KQkq b6 0 6"));

    ChessRules chessRules = new ChessRules();

    assertEquals(
        "d4",
        pgnService.toAlgebraic(parseString("d2"), parseString("d4"), chessBoard, chessRules, null));
    assertEquals(
        "Bc4",
        pgnService.toAlgebraic(parseString("f1"), parseString("c4"), chessBoard, chessRules, null));
    assertEquals(
        "exf6",
        pgnService.toAlgebraic(parseString("e5"), parseString("f6"), chessBoard, chessRules, null));

    // The c3 Knight or the e3 Knight, both can move to d5, the service should have known Nc it's
    // the right one
    assertEquals(
        "Ncd5",
        pgnService.toAlgebraic(parseString("c3"), parseString("d5"), chessBoard, chessRules, null));

    // En passant test
    assertEquals(
        "cxb6 e.p.",
        pgnService.toAlgebraic(parseString("c5"), parseString("b6"), chessBoard, chessRules, null));

    // Rooks on the same rank test
    ChessBoard chessBoardPos2 =
        ChessBoardFactory.createFromFEN(
            Fen.createCustom("4k2r/1r3n2/3p4/8/8/2N2P2/R6R/2K5 w - - 0 1"));

    assertEquals(
        "Rad2",
        pgnService.toAlgebraic(
            parseString("a2"), parseString("d2"), chessBoardPos2, chessRules, null));
    assertEquals(
        "Rhg2",
        pgnService.toAlgebraic(
            parseString("h2"), parseString("g2"), chessBoardPos2, chessRules, null));

    // Castle test
    ChessBoard chessBoardPos3 =
        ChessBoardFactory.createFromFEN(
            Fen.createCustom("r2qk2r/8/8/8/8/8/3Q4/R3K2R w KQkq - 0 1"));

    assertEquals(
        "O-O",
        pgnService.toAlgebraic(
            parseString("e1"), parseString("g1"), chessBoardPos3, chessRules, null));
    assertEquals(
        "O-O-O",
        pgnService.toAlgebraic(
            parseString("e1"), parseString("c1"), chessBoardPos3, chessRules, null));

    // Promote piece test
    ChessBoard chessBoardPos4 =
        ChessBoardFactory.createFromFEN(Fen.createCustom("1r6/P2k2P1/8/8/8/8/8/4K3 w - - 0 1"));

    assertEquals(
        "g8=Q",
        pgnService.toAlgebraic(
            parseString("g7"), parseString("g8"), chessBoardPos4, chessRules, PieceType.QUEEN));
    assertEquals(
        "axb8=B",
        pgnService.toAlgebraic(
            parseString("a7"), parseString("b8"), chessBoardPos4, chessRules, PieceType.BISHOP));
    // TODO en esta posicion anterior se puede coronar con jaque, porque lo podría ser axb8=N+, con
    // un caballo por ejemplo, probar este caso....

    // TODO test para check!!!

    // TODO test para jaque mate///

    // TODO test para ... tablas??

  }
}
