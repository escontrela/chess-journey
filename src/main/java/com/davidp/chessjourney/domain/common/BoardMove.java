package com.davidp.chessjourney.domain.common;

public class BoardMove {

    private final Pos from;
    private final Pos to;

    public BoardMove(Pos from, Pos to) {
        this.from = from;
        this.to = to;
    }

    public Pos getFrom() {
        return from;
    }

    public Pos getTo() {
        return to;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", from, to);
    }
}