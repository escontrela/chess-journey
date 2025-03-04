package com.davidp.chessjourney.infrastructure.database;

import com.davidp.chessjourney.domain.User;
import com.davidp.chessjourney.domain.UserRepository;
import com.davidp.chessjourney.domain.common.AggregatedStats;
import com.davidp.chessjourney.domain.common.UserExerciseStats;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;

public class UserRepositoryImpl implements UserRepository {

  private final DataSource dataSource;

  public UserRepositoryImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public List<User> getAll() {

    List<User> result = new ArrayList<>();
    String sql = "SELECT id,email, firstname,lastname FROM users";

    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {

        long userId = rs.getLong("id");
        String email = rs.getString("email");
        String firstname = rs.getString("firstname");
        String lastname = rs.getString("lastname");

        User user = new User(userId, email, firstname, lastname);
        result.add(user);
      }
    } catch (Exception e) {

      e.printStackTrace();
      // Manejar excepción según tus necesidades (lanzarla, loguearla, etc.)
    }

    return result;
  }

  @Override
  public User getUserById(long id) {
    String sql = "SELECT id, email, firstname, lastname FROM users WHERE id = ?";

    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, id);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          long userId = rs.getLong("id");
          String email = rs.getString("email");
          String firstname = rs.getString("firstname");
          String lastname = rs.getString("lastname");

          // Construimos el objeto User con los datos de la fila
          return new User(userId, email, firstname, lastname);
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      // Manejar la excepción según tus necesidades (log, rethrow, etc.)
    }

    // Si no se encontró ningún registro con ese ID, retornamos null
    // TODO Fix it, sino se encuentra no deberíamos devolver null, valorar una exception o un
    // Optional
    return null;
  }

  @Override
  public boolean update(User user) {
    String sql = "UPDATE users SET firstname = ?, lastname = ? WHERE id = ?";

    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, user.getFirstname());
      ps.setString(2, user.getLastname());
      ps.setLong(3, user.getId());

      int rowsAffected = ps.executeUpdate();
      return rowsAffected > 0;

    } catch (Exception e) {
      e.printStackTrace();
      // Manejar excepción según tus necesidades (log, rethrow, etc.)
      // TODO fix IT!!!
      return false;
    }
  }

  @Override
  public boolean insertExerciseStats(UserExerciseStats stats) {

    String sql = "INSERT INTO user_exercise_stats " +
            "(user_id, exercise_id, attempt_date, successful, time_taken_seconds, attempts, difficulty_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setLong(1, stats.getUserId());
      ps.setObject(2, stats.getExerciseId());
      ps.setTimestamp(3, Timestamp.valueOf(stats.getAttemptDate()));
      ps.setBoolean(4, stats.wasSuccessful());
      ps.setInt(5, stats.getTimeTakenSeconds());
      ps.setInt(6, stats.getAttempts());
      ps.setObject(7, stats.getDifficultyId());

      int rowsInserted = ps.executeUpdate();
      return rowsInserted > 0;

    } catch (Exception e) {
      throw new RuntimeException("Error inserting exercise stats", e);
    }
  }

  @Override
  public List<AggregatedStats> getSuccessRateByPeriod(long userId, UUID gameType, UUID difficultyId,
                                                      LocalDate startDate, LocalDate endDate, String period) {
    String periodColumn = getPeriodColumn(period);
    List<AggregatedStats> result = new ArrayList<>();

    String sql = "SELECT " + periodColumn + " AS period_date, AVG(successful::int) AS success_rate " +
            "FROM user_exercise_stats ues " +
            "JOIN exercises e ON ues.exercise_id = e.id " +
            "WHERE ues.user_id = ? AND e.type_id = ? AND ues.difficulty_id = ? " +
            "AND ues.attempt_date BETWEEN ? AND ? " +
            "GROUP BY period_date ORDER BY period_date";

    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setLong(1, userId);
      ps.setObject(2, gameType);
      ps.setObject(3, difficultyId);
      ps.setDate(4, Date.valueOf(startDate));
      ps.setTimestamp(5, Timestamp.valueOf(endDate.plusDays(1).atStartOfDay().minusSeconds(1))); // 23:59:59

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          result.add(new AggregatedStats(
                  rs.getDate("period_date").toLocalDate(),
                  rs.getDouble("success_rate")
          ));
        }
      }

    } catch (Exception e) {
      throw new RuntimeException("Error inserting exercise stats", e);
    }
    return result;
  }

  @Override
  public List<AggregatedStats> getTotalTimeSpentByPeriod(long userId, UUID gameType, UUID difficultyId,
                                                         LocalDate startDate, LocalDate endDate, String period) {
    String periodColumn = getPeriodColumn(period);
    List<AggregatedStats> result = new ArrayList<>();

    String sql = "SELECT " + periodColumn + " AS period_date, SUM(time_taken_seconds) AS total_time " +
            "FROM user_exercise_stats ues " +
            "JOIN exercises e ON ues.exercise_id = e.id " +
            "WHERE ues.user_id = ? AND e.type_id = ? AND ues.difficulty_id = ? " +
            "AND ues.successful = true " +
            "AND ues.attempt_date BETWEEN ? AND ? " +
            "GROUP BY period_date ORDER BY period_date";

    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setLong(1, userId);
      ps.setObject(2, gameType);
      ps.setObject(3, difficultyId);
      ps.setDate(4, Date.valueOf(startDate));
      ps.setTimestamp(5, Timestamp.valueOf(endDate.plusDays(1).atStartOfDay().minusSeconds(1))); // 23:59:59

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          result.add(new AggregatedStats(
                  rs.getDate("period_date").toLocalDate(),
                  rs.getDouble("total_time")
          ));
        }
      }

    } catch (Exception e) {
      throw new RuntimeException("Error inserting exercise stats", e);
    }
    return result;
  }

  /**
   * 🔄 Determina la columna de agrupación según el periodo ("daily", "monthly", "yearly").
   */
  private String getPeriodColumn(String period) {
    switch (period.toLowerCase()) {
      case "daily":
        return "DATE_TRUNC('day', ues.attempt_date)::date";
      case "monthly":
        return "DATE_TRUNC('month', ues.attempt_date)::date";
      case "yearly":
        return "DATE_TRUNC('year', ues.attempt_date)::date";
      default:
        throw new IllegalArgumentException("Periodo inválido: " + period);
    }
  }
}