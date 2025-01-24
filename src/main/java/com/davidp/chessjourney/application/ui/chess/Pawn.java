package com.davidp.chessjourney.application.ui.chess;


public class Pawn extends PieceView {

    public Pawn(String color) {
        super(color);
        this.type = "Pawn";
        setImage();
    }
}