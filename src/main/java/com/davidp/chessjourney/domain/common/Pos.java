package com.davidp.chessjourney.domain.common;

public class Pos {

    private final Row row;
    private final Col col;

    public Pos(Row row, Col col) {

        this.row = row;
        this.col = col;
    }

    public Row getRow() {

        return row;
    }

    public Col getCol() {

        return col;
    }

    @Override
    public String toString() {

        return col.name() + row.getValue();
    }
}