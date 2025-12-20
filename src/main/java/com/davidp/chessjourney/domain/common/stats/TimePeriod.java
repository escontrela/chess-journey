package com.davidp.chessjourney.domain.common.stats;

import java.time.LocalDate;

/** This interface represents a time period used for aggregating statistics,
 * such as days, weeks, months, or years.
 **/
public interface TimePeriod {

    /** Fecha representativa del periodo (inicio del bucket) */
    LocalDate getStartDate();

    /** Etiqueta legible para UI: "Jan 2025", "Week 12", "08/01/2025" */
    String getLabel();

    /** Nivel del periodo */
    PeriodType getType();
}