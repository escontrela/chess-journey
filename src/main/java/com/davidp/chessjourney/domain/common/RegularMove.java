package com.davidp.chessjourney.domain.common;

import java.util.Collections;
import java.util.List;

/**
 * Movimiento estándar de una pieza (puede incluir captura, jaque o mate).
 */
public record RegularMove(
        BoardMove move,
        boolean capture,
        boolean check,
        boolean mate
) implements GameMove {
    @Override
    public List<BoardMove> getMoves() {
        return Collections.singletonList(move);
    }
    @Override public boolean isCapture() { return capture; }
    @Override public boolean isCheck()   { return check; }
    @Override public boolean isMate()    { return mate; }
}