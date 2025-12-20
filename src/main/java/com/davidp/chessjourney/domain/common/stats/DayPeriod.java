package com.davidp.chessjourney.domain.common.stats;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class DayPeriod implements TimePeriod {

    private final LocalDate date;

    public DayPeriod(LocalDate date) {

        this.date = Objects.requireNonNull(date);
    }

    @Override
    public LocalDate getStartDate() {

        return date;
    }

    @Override
    public String getLabel() {

        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Override
    public PeriodType getType() {

        return PeriodType.DAY;
    }
}