package com.davidp.chessjourney.domain.common;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que representa cualquier tipo de movimiento de ajedrez.
 */
public interface GameMove {

    /**
     * Lista de movimientos individuales (from-to) que componen este movimiento.
     */
    List<BoardMove> getMoves();

    /**
     * Indica si este movimiento implica captura.
     */
    boolean isCapture();

    /**
     * Indica si este movimiento genera jaque.
     */
    boolean isCheck();

    /**
     * Indica si este movimiento genera mate.
     */
    boolean isMate();

    /**
     * Indica si es un enroque.
     */
    default boolean isCastling() {
        return false;
    }

    /**
     * Indica si es una promoción.
     */
    default boolean isPromotion() {
        return false;
    }

    /**
     * Indica si es captura al paso.
     */
    default boolean isEnPassant() {
        return false;
    }

    /**
     * Pieza de promoción; sólo si isPromotion()==true.
     */
    default PieceType getPromotionPiece() {
        return null;
    }

    /**
     * Devuelve un CastlingMove si este GameMove es castling, o vacío.
     */
    default Optional<CastlingMove> asCastling() {
        return this instanceof CastlingMove cm
                ? Optional.of(cm)
                : Optional.empty();
    }

    /**
     * Devuelve un NormalMove si este GameMove es regular, o vacío.
     */
    default Optional<RegularMove> asRegular() {
        return this instanceof RegularMove rm
                ? Optional.of(rm)
                : Optional.empty();
    }

}