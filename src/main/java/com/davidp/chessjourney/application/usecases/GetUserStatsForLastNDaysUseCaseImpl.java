package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.UserRepository;
import com.davidp.chessjourney.domain.common.AggregatedStats;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Implementación del caso de uso que obtiene estadísticas de usuario en los últimos N días. */
public class GetUserStatsForLastNDaysUseCaseImpl implements GetUserStatsForLastNDaysUseCase {

  private final UserRepository statsRepository;

  public GetUserStatsForLastNDaysUseCaseImpl(UserRepository statsRepository) {

    this.statsRepository = statsRepository;
  }

  @Override
  public List<AggregatedStats> execute(long userId, UUID gameType, UUID difficultyId, int days) {

    if (days <= 0) {
      throw new IllegalArgumentException("El número de días debe ser mayor a 0.");
    }

    LocalDate endDate = LocalDate.now();
    LocalDate startDate = endDate.minusDays(days);

    System.out.println(
        "Getting stats for user "
            + userId
            + " with game type "
            + gameType
            + " and difficulty "
            + difficultyId
            + " for the last "
            + days
            + " days.");

    List<AggregatedStats> stats =
        statsRepository.getSuccessRateByPeriod(
            userId, gameType, difficultyId, startDate, endDate, "daily");

    stats.forEach(e -> System.out.println(e.toString()));
    return stats;
  }
}
