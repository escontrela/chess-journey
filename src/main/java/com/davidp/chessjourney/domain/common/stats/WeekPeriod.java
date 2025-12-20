package com.davidp.chessjourney.domain.common.stats;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Objects;

public final class WeekPeriod implements TimePeriod {

    private final LocalDate weekStart;

    public WeekPeriod(LocalDate weekStart) {

        this.weekStart = Objects.requireNonNull(weekStart);
    }

    @Override
    public LocalDate getStartDate() {

        return weekStart;
    }

    @Override
    public String getLabel() {

        int week = weekStart.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        return "Week " + week + " " + weekStart.getYear();
    }

    @Override
    public PeriodType getType() {

        return PeriodType.WEEK;
    }
}