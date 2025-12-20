package com.davidp.chessjourney.domain.userstats;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Punto agregado para pintar barras por periodo (día/semana/mes).
 * periodStart: fecha representativa del bucket (DATE_TRUNC(... )::date).
 * value: métrica agregada (porcentaje, segundos, conteo...).
 */
public final class StatsPoint {

  private final LocalDate periodStart;
  private final double value;

  public StatsPoint(LocalDate periodStart, double value) {

    this.periodStart = Objects.requireNonNull(periodStart, "periodStart must not be null");
    this.value = value;
  }

  public LocalDate getPeriodStart() {

      return periodStart;
  }

  public double getValue() {

      return value;
  }
}
