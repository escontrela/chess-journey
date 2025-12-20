package com.davidp.chessjourney.domain.common.stats;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class MonthPeriod implements TimePeriod {

    private final YearMonth yearMonth;

    public MonthPeriod(int month, int year) {

        this.yearMonth = YearMonth.of(year, month);
    }

    public MonthPeriod(YearMonth yearMonth) {

        this.yearMonth = Objects.requireNonNull(yearMonth);
    }

    @Override
    public LocalDate getStartDate() {

        return yearMonth.atDay(1);
    }

    @Override
    public String getLabel() {

        return yearMonth.format(DateTimeFormatter.ofPattern("MMM yyyy"));
    }

    @Override
    public PeriodType getType() {

        return PeriodType.MONTH;
    }
}
