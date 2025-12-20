package com.davidp.chessjourney.domain;

import com.davidp.chessjourney.domain.userstats.StatsPoint;
import com.davidp.chessjourney.domain.userstats.UserMetric;
import com.davidp.chessjourney.domain.userstats.UserStatsQuery;

import java.util.List;

public interface UserStatsRepository {

  /**
   * Devuelve serie de barras por periodo para una métrica concreta.
   * La serie ya viene agrupada por día/semana/mes en función del query.
   */
  List<StatsPoint> getMetricSeries(UserMetric metric, UserStatsQuery query);
}
