package com.davidp.chessjourney.domain.common;

import java.time.LocalDate;

public class AggregatedStats {

    private final LocalDate date;
    private final double value;

    public AggregatedStats(LocalDate date, double value) {
        this.date = date;
        this.value = value;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getValue() {
        return value;
    }
}