package com.davidp.chessjourney.domain.common;

import static com.davidp.chessjourney.domain.services.FenService.*;
import static org.junit.jupiter.api.Assertions.*;

import com.davidp.chessjourney.domain.services.FenService;
import com.davidp.chessjourney.domain.services.FenServiceFactory;
import java.util.List;
import org.junit.jupiter.api.Test;

public class FenTest {

  FenService fenService = FenServiceFactory.getFenService();

  @Test
  public void testFenParsing() {

    final FenParserResponse fenParserResponse =
        fenService.parseString(
            Fen.createCustom("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e3 0 1"));

    // Verificar piezas
    List<PiecePosition> pieces = fenParserResponse.getPieces();
    assertEquals(32, pieces.size());

    pieces.forEach(
        piece -> {
          System.out.println(
              piece.getPiece().getColor()
                  + " - "
                  + piece.getPiece().getType()
                  + " - "
                  + piece.getPosition());
        });

    // Verificar color activo
    assertEquals(PieceColor.WHITE, fenParserResponse.getActiveColor());

    // Verificar disponibilidad de enroque
    CastlingAvailability castlingAvailability = fenParserResponse.getCastlingAvailability();
    assertTrue(castlingAvailability.canWhiteCastleKingside());
    assertTrue(castlingAvailability.canWhiteCastleQueenside());
    assertTrue(castlingAvailability.canBlackCastleKingside());
    assertTrue(castlingAvailability.canBlackCastleQueenside());

    // Verificar en passant
    EnPassantTargetSquare enPassant = fenParserResponse.getEnPassantTargetSquare();
    assertTrue(enPassant.isAvailable());
    assertEquals("E3", enPassant.getPosition().toString());

    // Verificar halfmove clock
    assertEquals(0, fenParserResponse.getHalfMoveClock());

    // Verificar fullmove number
    assertEquals(1, fenParserResponse.getFullMoveNumber());
  }

  @Test
  public void testFenParsing2() {

    final FenParserResponse fenParserResponse =
        fenService.parseString(
            Fen.createCustom("rnbqkb1r/5ppp/pp2p2n/2pP4/2p5/5NP1/PP2PPBP/RNBQ1RK1 w kq c6 0 8"));

    // Verificar piezas
    List<PiecePosition> pieces = fenParserResponse.getPieces();
    assertEquals(31, pieces.size());

    // Verificar color activo
    assertEquals(PieceColor.WHITE, fenParserResponse.getActiveColor());

    // Verificar disponibilidad de enroque
    CastlingAvailability castlingAvailability = fenParserResponse.getCastlingAvailability();
    assertFalse(castlingAvailability.canWhiteCastleKingside());
    assertFalse(castlingAvailability.canWhiteCastleQueenside());
    assertTrue(castlingAvailability.canBlackCastleKingside());
    assertTrue(castlingAvailability.canBlackCastleQueenside());

    // Verificar en passant
    EnPassantTargetSquare enPassant = fenParserResponse.getEnPassantTargetSquare();
    assertTrue(enPassant.isAvailable());
    assertEquals("C6", enPassant.getPosition().toString());

    // Verificar halfmove clock
    assertEquals(0, fenParserResponse.getHalfMoveClock());

    // Verificar fullmove number
    assertEquals(8, fenParserResponse.getFullMoveNumber());
  }

  @Test()
  public void invalidFenObject() {

    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              Fen.createCustom("rnbqkbnr/pppppppp/");
            });
  }
}
