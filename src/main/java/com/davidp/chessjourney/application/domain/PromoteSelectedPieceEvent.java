package com.davidp.chessjourney.application.domain;

import com.davidp.chessjourney.domain.common.Piece;
import com.davidp.chessjourney.domain.common.PiecePosition;
import com.davidp.chessjourney.domain.common.Pos;

public class PromoteSelectedPieceEvent {

    Piece selectedPiece;
    Pos pos;

    public PromoteSelectedPieceEvent(final Pos pos,final Piece piece) {

        this.selectedPiece = piece;
        this.pos = pos;
    }

    public Piece getSelectedPiece() {

        return selectedPiece;
    }

    public Pos getPos() {

        return pos;
    }
}
