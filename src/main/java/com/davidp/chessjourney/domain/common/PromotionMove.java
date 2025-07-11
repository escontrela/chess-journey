package com.davidp.chessjourney.domain.common;

import java.util.Collections;
import java.util.List;

/**
 * Movimiento de peón que incluye promoción en la casilla de destino.
 */
public record PromotionMove(
        BoardMove move,
        PieceType promotionPiece,
        boolean capture,
        boolean check,
        boolean mate
) implements GameMove {
    @Override public List<BoardMove> getMoves()         { return Collections.singletonList(move); }
    @Override public boolean isCapture()                 { return capture; }
    @Override public boolean isCheck()                   { return check; }
    @Override public boolean isMate()                    { return mate; }
    @Override public boolean isPromotion()               { return true; }
    @Override public PieceType getPromotionPiece()       { return promotionPiece; }
}