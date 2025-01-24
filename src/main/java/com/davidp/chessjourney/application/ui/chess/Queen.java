package com.davidp.chessjourney.application.ui.chess;

import java.util.ArrayList;

public class Queen extends PieceView {

    public Queen(String color) {

        super(color);
        this.type = "Queen";
        setImage();
    }
}
