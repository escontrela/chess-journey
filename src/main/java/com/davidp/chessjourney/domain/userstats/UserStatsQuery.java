package com.davidp.chessjourney.domain.userstats;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Filtros para estadísticas de ejercicios.
 * gameTypeId: e.type_id (exercise_types)
 * difficultyId: ues.difficulty_id (difficulty_levels)
 */
public final class UserStatsQuery {

  private final long userId;
  private final LocalDate from;
  private final LocalDate to;
  private final UserStatsAggregationLevel userStatsAggregationLevel;

  private final Optional<UUID> gameTypeId;
  private final Optional<UUID> difficultyId;

  public UserStatsQuery(

      long userId,
      LocalDate from,
      LocalDate to,
      UserStatsAggregationLevel userStatsAggregationLevel,
      Optional<UUID> gameTypeId,
      Optional<UUID> difficultyId
  ) {
    this.userId = userId;
    this.from = from;
    this.to = to;
    this.userStatsAggregationLevel = userStatsAggregationLevel;
    this.gameTypeId = gameTypeId == null ? Optional.empty() : gameTypeId;
    this.difficultyId = difficultyId == null ? Optional.empty() : difficultyId;
  }

  public long getUserId() { return userId; }
  public LocalDate getFrom() { return from; }
  public LocalDate getTo() { return to; }
  public UserStatsAggregationLevel getAggregationLevel() { return userStatsAggregationLevel; }

  public Optional<UUID> getGameTypeId() { return gameTypeId; }
  public Optional<UUID> getDifficultyId() { return difficultyId; }
}
