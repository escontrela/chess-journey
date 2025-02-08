package com.davidp.chessjourney.application.domain;

import com.davidp.chessjourney.domain.common.Piece;

public class PromoteSelectedPieceEvent {

    Piece selectedPiece;

    public PromoteSelectedPieceEvent(Piece piece) {

        this.selectedPiece = piece;
    }

    public Piece getSelectedPiece() {

        return selectedPiece;
    }
}
