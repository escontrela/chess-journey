package com.davidp.chessjourney.domain.common;

public enum PieceColor {
  WHITE,
  BLACK;

    public PieceColor opposite() {
        return switch (this) {
            case WHITE -> BLACK;
            case BLACK -> WHITE;
            default -> throw new IllegalArgumentException("Unknown piece color: " + this);
        };
    }
}
