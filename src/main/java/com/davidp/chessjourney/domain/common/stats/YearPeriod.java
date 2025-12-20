package com.davidp.chessjourney.domain.common.stats;

import java.time.LocalDate;

public final class YearPeriod implements TimePeriod {

    private final int year;

    public YearPeriod(int year) {

        this.year = year;
    }

    @Override
    public LocalDate getStartDate() {

        return LocalDate.of(year, 1, 1);
    }

    @Override
    public String getLabel() {

        return String.valueOf(year);
    }

    @Override
    public PeriodType getType() {

        return PeriodType.YEAR;
    }
}