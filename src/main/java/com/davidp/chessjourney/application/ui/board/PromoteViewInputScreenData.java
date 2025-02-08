package com.davidp.chessjourney.application.ui.board;

import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.domain.common.PieceColor;

import java.awt.*;

public class PromoteViewInputScreenData extends InputScreenData {

    private final PieceColor pieceColor;

    public PromoteViewInputScreenData(double layoutX, double layoutY, PieceColor pieceColor) {

        super(layoutX, layoutY);
        this.pieceColor = pieceColor;
    }

    public PieceColor getPieceColor() {

        return pieceColor;
    }

    public static PromoteViewInputScreenData from(final Point point, final PieceColor pieceColor) {

        return new PromoteViewInputScreenData(point.getX(), point.getY(),pieceColor);
    }
}