package com.davidp.chessjourney.domain.common.stats;

import java.util.Objects;

public final class TimeSeriesPoint {

    private final TimePeriod period;
    private final double value;

    public TimeSeriesPoint(TimePeriod period, double value) {

        this.period = Objects.requireNonNull(period);
        this.value = value;
    }

    public TimePeriod getPeriod() {

        return period;
    }

    public double getValue() {

        return value;
    }
}