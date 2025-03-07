package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.common.AggregatedStats;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso para obtener estadísticas de un usuario en los últimos N días.
 */
public interface GetUserStatsForLastNDaysUseCase {

    List<AggregatedStats> execute(long userId, UUID gameType, UUID difficultyId, int days);
}