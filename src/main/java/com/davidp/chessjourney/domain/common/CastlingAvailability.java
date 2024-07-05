package com.davidp.chessjourney.domain.common;

public class CastlingAvailability {

    private final boolean whiteKingside;
    private final boolean whiteQueenside;
    private final boolean blackKingside;
    private final boolean blackQueenside;

    public CastlingAvailability(boolean whiteKingside, boolean whiteQueenside, boolean blackKingside, boolean blackQueenside) {
        this.whiteKingside = whiteKingside;
        this.whiteQueenside = whiteQueenside;
        this.blackKingside = blackKingside;
        this.blackQueenside = blackQueenside;
    }

    public boolean canWhiteCastleKingside() {
        return whiteKingside;
    }

    public boolean canWhiteCastleQueenside() {
        return whiteQueenside;
    }

    public boolean canBlackCastleKingside() {
        return blackKingside;
    }

    public boolean canBlackCastleQueenside() {
        return blackQueenside;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (whiteKingside) sb.append("K");
        if (whiteQueenside) sb.append("Q");
        if (blackKingside) sb.append("k");
        if (blackQueenside) sb.append("q");
        return sb.length() > 0 ? sb.toString() : "-";
    }
}