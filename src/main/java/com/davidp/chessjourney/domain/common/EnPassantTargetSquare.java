package com.davidp.chessjourney.domain.common;


public class EnPassantTargetSquare {
    private final Pos position;
    private final boolean isAvailable;

    public EnPassantTargetSquare(Pos position, boolean isAvailable) {
        this.position = position;
        this.isAvailable = isAvailable;
    }

    public Pos getPosition() {
        return position;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public String toString() {
        return isAvailable ? position.toString() : "-";
    }
}