package com.davidp.chessjourney.domain.common;

/**
 * Factoría para crear instancias de GameMove y mantener un registro de todas ellas.
 */
public class GameMoveFactory {

    private GameMoveFactory() {
    }

    public static RegularMove createNormalMove(BoardMove move,
                                               boolean capture,
                                               boolean check,
                                               boolean mate) {
        RegularMove m = new RegularMove(move, capture, check, mate);
        return m;
    }

    public static CastlingMove createCastlingMove(BoardMove kingMove,
                                                  BoardMove rookMove,
                                                  boolean check,
                                                  boolean mate) {
        CastlingMove m = new CastlingMove(kingMove, rookMove, check, mate);

        return m;
    }

    public static PromotionMove createPromotionMove(BoardMove move,
                                                    PieceType promotionPiece,
                                                    boolean capture,
                                                    boolean check,
                                                    boolean mate) {
        PromotionMove m = new PromotionMove(move, promotionPiece, capture, check, mate);

        return m;
    }

    public static EnPassantMove createEnPassantMove(BoardMove move,
                                                    boolean check,
                                                    boolean mate) {
        EnPassantMove m = new EnPassantMove(move, check, mate);

        return m;
    }
}