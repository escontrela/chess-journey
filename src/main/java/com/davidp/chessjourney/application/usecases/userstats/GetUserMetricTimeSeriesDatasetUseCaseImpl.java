package com.davidp.chessjourney.application.usecases.userstats;


import com.davidp.chessjourney.domain.UserStatsRepository;

import java.time.LocalDate;

import com.davidp.chessjourney.domain.common.stats.*;
import com.davidp.chessjourney.domain.userstats.StatsPoint;
import com.davidp.chessjourney.domain.userstats.UserMetric;
import com.davidp.chessjourney.domain.userstats.StatsAggregationLevel;
import com.davidp.chessjourney.domain.userstats.UserStatsQuery;

import java.util.*;

/**
 * Caso de uso: devuelve un TimeSeriesDataset (con 1..N series) para UNA gráfica.
 *
 * Por cada gráfica (accuracy / avgTimeSuccess / volume) lanzarás una instancia de este caso de uso
 * con el UserMetric correspondiente.
 *
 * Entradas:
 * - metric: qué gráfica quieres (ACCURACY, AVG_SOLVE_TIME_SUCCESS, TRAINING_VOLUME)
 * - exerciseTypeIds: array de tipos de ejercicio (si viene vacío => una sola serie "All")
 * - difficultyId: dificultad seleccionada (opcional, puede ser null)
 * - from/to
 * - aggregationLevel (DAILY/WEEKLY/MONTHLY)
 *
 * Salida:
 * - TimeSeriesDataset con 1 serie (All) o múltiples (una por tipo de ejercicio).
 *
 * Ejemplo:
 *  // Accuracy
 * TimeSeriesDataset accuracyDs = useCase.execute(
 *   new GetUserMetricTimeSeriesDatasetRequest(
 *     userId,
 *     UserMetric.ACCURACY,
 *     selectedExerciseTypeIds,               // List<UUID>
 *     Optional.ofNullable(selectedDifficultyId),
 *     fromDate,
 *     toDate,
 *     AggregationLevel.MONTHLY
 *   )
 * );
 */
public class GetUserMetricTimeSeriesDatasetUseCaseImpl
    implements GetUserMetricTimeSeriesDatasetUseCase {

  private final UserStatsRepository userStatsRepository;

  public GetUserMetricTimeSeriesDatasetUseCaseImpl(UserStatsRepository userStatsRepository) {

    this.userStatsRepository = userStatsRepository;
  }

  @Override
  public TimeSeriesDataset execute(GetUserMetricTimeSeriesDatasetRequest request) {

    Objects.requireNonNull(request, "request must not be null");

    // Si no hay tipos -> una sola serie "All" sin filtro por tipo
    List<UUID> typeIds = request.getExerciseTypeIds() == null
        ? List.of()
        : request.getExerciseTypeIds();

    TimeSeriesDataset dataset = new TimeSeriesDataset();

    if (typeIds.isEmpty()) {
      // 1 sola serie agregada
      TimeSeries series = buildSeries(
          "All",
          request.getUserId(),
          Optional.empty(),
          request.getDifficultyId(),
          request.getFrom(),
          request.getTo(),
          request.getAggregationLevel(),
          request.getMetric()
      );
      dataset.addSeries(series);
      return dataset;
    }

    // Varias series (una por tipo de ejercicio)
    for (UUID typeId : typeIds) {
      String seriesName = "Type-" + typeId; // Si quieres nombre humano, se resolvería en otro caso de uso/servicio.
      TimeSeries series = buildSeries(
          seriesName,
          request.getUserId(),
          Optional.of(typeId),
          request.getDifficultyId(),
          request.getFrom(),
          request.getTo(),
          request.getAggregationLevel(),
          request.getMetric()
      );
      dataset.addSeries(series);
    }

    return dataset;
  }

  private TimeSeries buildSeries(
          String seriesName,
          long userId,
          Optional<UUID> gameTypeId,
          Optional<UUID> difficultyId,
          LocalDate from,
          LocalDate to,
          StatsAggregationLevel aggregationLevel,
          UserMetric metric
  ) {

    UserStatsQuery query = new UserStatsQuery(
        userId,
        from,
        to,
        aggregationLevel,
        gameTypeId,
        difficultyId
    );

    List<StatsPoint> points = userStatsRepository.getMetricSeries(metric, query);

    TimeSeries series = new TimeSeries(seriesName);

    for (StatsPoint p : points) {
      TimePeriod period = toPeriod(p.getPeriodStart(), aggregationLevel);
      series.add(period, p.getValue());
    }

    return series;
  }

  private TimePeriod toPeriod(LocalDate periodStart, StatsAggregationLevel aggregationLevel) {
    return switch (aggregationLevel) {
      case DAILY -> new DayPeriod(periodStart);
      case WEEKLY -> new WeekPeriod(periodStart);
      case MONTHLY -> new MonthPeriod(periodStart.getMonthValue(), periodStart.getYear());
    };
  }
}
