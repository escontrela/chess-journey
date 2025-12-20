package com.davidp.chessjourney.application.usecases.userstats;


import com.davidp.chessjourney.domain.userstats.UserMetric;
import com.davidp.chessjourney.domain.userstats.UserStatsAggregationLevel;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Request del caso de uso para una gráfica (una métrica).
 */
public final class GetUserMetricTimeSeriesDatasetRequest {

  private final long userId;
  private final UserMetric metric;

  // array de tipos de ejercicio (puede ser vacío para "All")
  private final List<UUID> exerciseTypeIds;

  // dificultad seleccionada (opcional)
  private final Optional<UUID> difficultyId;

  private final LocalDate from;
  private final LocalDate to;

  private final UserStatsAggregationLevel aggregationLevel;

  public GetUserMetricTimeSeriesDatasetRequest(
      long userId,
      UserMetric metric,
      List<UUID> exerciseTypeIds,
      Optional<UUID> difficultyId,
      LocalDate from,
      LocalDate to,
      UserStatsAggregationLevel aggregationLevel
  ) {
    this.userId = userId;
    this.metric = metric;
    this.exerciseTypeIds = exerciseTypeIds;
    this.difficultyId = difficultyId == null ? Optional.empty() : difficultyId;
    this.from = from;
    this.to = to;
    this.aggregationLevel = aggregationLevel;
  }

  public long getUserId() {
      return userId;
  }
  public UserMetric getMetric() {
      return metric;
  }
  public List<UUID> getExerciseTypeIds() {
      return exerciseTypeIds;
  }
  public Optional<UUID> getDifficultyId() {
      return difficultyId;
  }
  public LocalDate getFrom() {
      return from;
  }

    public LocalDate getTo() {

      return to;
  }
  public UserStatsAggregationLevel getAggregationLevel() {

      return aggregationLevel;
  }
}
