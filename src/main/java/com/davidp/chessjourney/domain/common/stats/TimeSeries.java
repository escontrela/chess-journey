package com.davidp.chessjourney.domain.common.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class TimeSeries {

    private final String name;
    private final List<TimeSeriesPoint> points = new ArrayList<>();

    public TimeSeries(String name) {

        this.name = Objects.requireNonNull(name);
    }

    public String getName() {

        return name;
    }

    public void add(TimePeriod period, double value) {

        points.add(new TimeSeriesPoint(period, value));
    }

    public List<TimeSeriesPoint> getPoints() {

        return Collections.unmodifiableList(points);
    }
}