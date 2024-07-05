package com.davidp.chessjourney.domain.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class FenTest {

    @Test
    public void testFenParsing() {
        String fenString = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e3 0 1";
        Fen fen = new Fen(fenString);

        // Verificar piezas
        List<Fen.PiecePosition> pieces = fen.getPieces();
        assertEquals(32, pieces.size());

        // Verificar color activo
        assertEquals(PieceColor.WHITE, fen.getActiveColor());

        // Verificar disponibilidad de enroque
        CastlingAvailability castlingAvailability = fen.getCastlingAvailability();
        assertTrue(castlingAvailability.canWhiteCastleKingside());
        assertTrue(castlingAvailability.canWhiteCastleQueenside());
        assertTrue(castlingAvailability.canBlackCastleKingside());
        assertTrue(castlingAvailability.canBlackCastleQueenside());

        // Verificar en passant
        EnPassantTargetSquare enPassant = fen.getEnPassantTargetSquare();
        assertTrue(enPassant.isAvailable());
        assertEquals("E3", enPassant.getPosition().toString());

        // Verificar halfmove clock
        assertEquals(0, fen.getHalfmoveClock());

        // Verificar fullmove number
        assertEquals(1, fen.getFullmoveNumber());
    }


    @Test
    public void testFenParsing2() {

        String fenString = "rnbqkb1r/5ppp/pp2p2n/2pP4/2p5/5NP1/PP2PPBP/RNBQ1RK1 w kq c6 0 8";
        Fen fen = new Fen(fenString);

        // Verificar piezas
        List<Fen.PiecePosition> pieces = fen.getPieces();
        assertEquals(31, pieces.size());

        // Verificar color activo
        assertEquals(PieceColor.WHITE, fen.getActiveColor());

        // Verificar disponibilidad de enroque
        CastlingAvailability castlingAvailability = fen.getCastlingAvailability();
        assertFalse(castlingAvailability.canWhiteCastleKingside());
        assertFalse(castlingAvailability.canWhiteCastleQueenside());
        assertTrue(castlingAvailability.canBlackCastleKingside());
        assertTrue(castlingAvailability.canBlackCastleQueenside());

        // Verificar en passant
        EnPassantTargetSquare enPassant = fen.getEnPassantTargetSquare();
        assertTrue(enPassant.isAvailable());
        assertEquals("C6", enPassant.getPosition().toString());

        // Verificar halfmove clock
        assertEquals(0, fen.getHalfmoveClock());

        // Verificar fullmove number
        assertEquals(8, fen.getFullmoveNumber());

    }
}