package com.davidp.chessjourney.domain.common;

import java.util.Collections;
import java.util.List;

/**
 * Movimiento de captura al paso.
 */
public record EnPassantMove(
        BoardMove move,
        boolean check,
        boolean mate
) implements GameMove {
    @Override public List<BoardMove> getMoves()         { return Collections.singletonList(move); }
    @Override public boolean isCapture()                 { return true; }
    @Override public boolean isCheck()                   { return check; }
    @Override public boolean isMate()                    { return mate; }
    @Override public boolean isEnPassant()               { return true; }
}