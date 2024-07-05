package com.davidp.chessjourney.domain.common;

public class PieceFactory {

    public static Piece createWhiteKing() {
        return new Piece(PieceType.KING, PieceColor.WHITE);
    }

    public static Piece createWhiteQueen() {
        return new Piece(PieceType.QUEEN, PieceColor.WHITE);
    }

    public static Piece createWhiteRook() {
        return new Piece(PieceType.ROOK, PieceColor.WHITE);
    }

    public static Piece createWhiteBishop() {
        return new Piece(PieceType.BISHOP, PieceColor.WHITE);
    }

    public static Piece createWhiteKnight() {
        return new Piece(PieceType.KNIGHT, PieceColor.WHITE);
    }

    public static Piece createWhitePawn() {
        return new Piece(PieceType.PAWN, PieceColor.WHITE);
    }

    public static Piece createBlackKing() {
        return new Piece(PieceType.KING, PieceColor.BLACK);
    }

    public static Piece createBlackQueen() {
        return new Piece(PieceType.QUEEN, PieceColor.BLACK);
    }

    public static Piece createBlackRook() {
        return new Piece(PieceType.ROOK, PieceColor.BLACK);
    }

    public static Piece createBlackBishop() {
        return new Piece(PieceType.BISHOP, PieceColor.BLACK);
    }

    public static Piece createBlackKnight() {
        return new Piece(PieceType.KNIGHT, PieceColor.BLACK);
    }

    public static Piece createBlackPawn() {
        return new Piece(PieceType.PAWN, PieceColor.BLACK);
    }
}
