package com.davidp.chessjourney.infrastructure.database;

import com.davidp.chessjourney.domain.UserStatsRepository;
import com.davidp.chessjourney.domain.userstats.*;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio EXCLUSIVO para estadísticas de usuario (user_exercise_stats).
 * Soporta:
 * - Rango de fechas (from/to)
 * - Agrupación: DAILY/WEEKLY/MONTHLY
 * - Filtros opcionales: gameType (exercise.type_id) y difficulty (ues.difficulty_id)
 *
 * NOTA: Este repo asume Postgres por DATE_TRUNC y casting ::date.
 */
public class UserStatsRepositoryImpl implements UserStatsRepository {

  private final DataSource dataSource;

  public UserStatsRepositoryImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public List<StatsPoint> getMetricSeries(UserMetric metric, UserStatsQuery query) {
    validate(query);

    String periodExpr = periodExpression(query.getAggregationLevel());
    MetricSql metricSql = metricSql(metric);

    // Build SQL with optional filters (gameTypeId, difficultyId)
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT ")
        .append(periodExpr).append(" AS period_date, ")
        .append(metricSql.selectExpr).append(" AS metric_value ")
        .append("FROM user_exercise_stats ues ")
        .append("JOIN exercises e ON ues.exercise_id = e.id ")
        .append("WHERE ues.user_id = ? ")
        .append("AND ues.attempt_date BETWEEN ? AND ? ");

    // Optional filters
    if (query.getGameTypeId().isPresent()) {
      sql.append("AND e.type_id = ? ");
    }
    if (query.getDifficultyId().isPresent()) {
      // Importante: según tu modelo, la dificultad en stats está en ues.difficulty_id
      // (no usamos e.difficulty_id)
      sql.append("AND ues.difficulty_id = ? ");
    }

    // Metric-specific WHERE clauses (e.g., only successes for avg solve time)
    if (metricSql.extraWhereClause != null && !metricSql.extraWhereClause.isBlank()) {
      sql.append("AND ").append(metricSql.extraWhereClause).append(" ");
    }

    sql.append("GROUP BY period_date ")
        .append("ORDER BY period_date;");

    List<StatsPoint> result = new ArrayList<>();

    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql.toString())) {

      int idx = 1;
      ps.setLong(idx++, query.getUserId());

      // Inclusive range: [from 00:00:00, to 23:59:59]
      LocalDateTime fromTs = query.getFrom().atStartOfDay();
      LocalDateTime toTs = query.getTo().plusDays(1).atStartOfDay().minusSeconds(1);

      ps.setTimestamp(idx++, Timestamp.valueOf(fromTs));
      ps.setTimestamp(idx++, Timestamp.valueOf(toTs));

      if (query.getGameTypeId().isPresent()) {
        ps.setObject(idx++, query.getGameTypeId().get());
      }
      if (query.getDifficultyId().isPresent()) {
        ps.setObject(idx++, query.getDifficultyId().get());
      }

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          LocalDate periodDate = rs.getDate("period_date").toLocalDate();
          double value = rs.getDouble("metric_value");
          result.add(new StatsPoint(periodDate, value));
        }
      }

    } catch (SQLException e) {
      throw new RuntimeException("Error fetching user stats series (" + metric + ")", e);
    }

    return result;
  }

  // -------------------------
  // Helpers
  // -------------------------

  private void validate(UserStatsQuery query) {
    if (query == null) throw new IllegalArgumentException("query must not be null");
    if (query.getFrom() == null || query.getTo() == null) {
      throw new IllegalArgumentException("from/to must not be null");
    }
    if (query.getAggregationLevel() == null) {
      throw new IllegalArgumentException("aggregationLevel must not be null");
    }
    if (query.getTo().isBefore(query.getFrom())) {
      throw new IllegalArgumentException("to must be >= from");
    }
  }

  /**
   * Importante:
   * - Postgres date_trunc('week', ...) empieza en lunes (ISO-ish).
   * - Devolvemos ::date para tener un bucket consistente.
   */
  private String periodExpression(UserStatsAggregationLevel level) {
    return switch (level) {
      case DAILY -> "DATE_TRUNC('day', ues.attempt_date)::date";
      case WEEKLY -> "DATE_TRUNC('week', ues.attempt_date)::date";
      case MONTHLY -> "DATE_TRUNC('month', ues.attempt_date)::date";
    };
  }

  private MetricSql metricSql(UserMetric metric) {
    return switch (metric) {
      case ACCURACY ->
          // AVG(successful::int) retorna 0..1 (ideal para UI: *100)
          new MetricSql("AVG(ues.successful::int)", null);

      case AVG_SOLVE_TIME_SUCCESS ->
          // Solo éxitos, promedio de segundos
          new MetricSql("AVG(ues.time_taken_seconds)", "ues.successful = true");

      case TRAINING_VOLUME ->
          // Intentados = todos los registros (éxito o no)
          new MetricSql("COUNT(*)", null);
    };
  }

  private static final class MetricSql {
    private final String selectExpr;
    private final String extraWhereClause;

    private MetricSql(String selectExpr, String extraWhereClause) {
      this.selectExpr = selectExpr;
      this.extraWhereClause = extraWhereClause;
    }
  }
}