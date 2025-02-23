package com.davidp.chessjourney.application.ui.board;

import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.domain.common.PieceColor;
import com.davidp.chessjourney.domain.common.Pos;

import java.awt.*;

public class PromoteViewInputScreenData extends InputScreenData {

    private final PieceColor pieceColor;
    private final Pos piecePos;

    public PromoteViewInputScreenData(double layoutX, double layoutY, final Pos pos ,final PieceColor pieceColor) {

        super(layoutX, layoutY);
        this.pieceColor = pieceColor;
        this.piecePos = pos;
    }

    public PieceColor getPieceColor() {

        return pieceColor;
    }

    public Pos getPiecePos() {

        return piecePos;
    }

    public static PromoteViewInputScreenData from(final Point point, final Pos pos, final PieceColor pieceColor) {

        return new PromoteViewInputScreenData(point.getX(), point.getY(), pos ,pieceColor);
    }
}