package com.davidp.chessjourney.domain.common;

import java.util.List;

/**
 * Movimiento de enroque, compuesto por dos sub-movimientos: rey y torre.
 */
public record CastlingMove(
        BoardMove kingMove,
        BoardMove rookMove,
        boolean check,
        boolean mate
) implements GameMove {
    @Override public List<BoardMove> getMoves()    { return List.of(kingMove, rookMove); }
    @Override public boolean isCapture()            { return false; }
    @Override public boolean isCheck()              { return check; }
    @Override public boolean isMate()               { return mate; }
    @Override public boolean isCastling()           { return true; }
}