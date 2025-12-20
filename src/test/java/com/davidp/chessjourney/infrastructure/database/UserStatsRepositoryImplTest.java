package com.davidp.chessjourney.infrastructure.database;

import com.davidp.chessjourney.domain.userstats.StatsPoint;
import com.davidp.chessjourney.domain.userstats.UserMetric;
import com.davidp.chessjourney.domain.userstats.StatsAggregationLevel;
import com.davidp.chessjourney.domain.userstats.UserStatsQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UserStatsRepositoryImplTest {

  DataSource dataSource;
  Connection connection;
  PreparedStatement ps;
  ResultSet rs;

  UserStatsRepositoryImpl repository;

  private UserStatsQuery baseQuery;

  @BeforeEach
  void setUp() throws Exception {
    // Crear mocks para jdbc objects excepto DataSource
    connection = mock(Connection.class);
    ps = mock(PreparedStatement.class);
    rs = mock(ResultSet.class);

    // DataSource de prueba: no se instrumenta ni se mockea, simplemente delega a la conexión mock
    dataSource = new DataSource() {
      @Override
      public Connection getConnection() throws SQLException {
        return connection;
      }

      @Override
      public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("Not used in tests");
      }

      @Override
      public PrintWriter getLogWriter() throws SQLException { return null; }

      @Override
      public void setLogWriter(PrintWriter out) throws SQLException { }

      @Override
      public void setLoginTimeout(int seconds) throws SQLException { }

      @Override
      public int getLoginTimeout() throws SQLException { return 0; }

      @Override
      public Logger getParentLogger() throws SQLFeatureNotSupportedException { throw new SQLFeatureNotSupportedException(); }

      @Override
      public <T> T unwrap(Class<T> iface) throws SQLException { throw new SQLException("unwrap not supported"); }

      @Override
      public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
    };

    // PreparedStatement returned when preparing any SQL
    when(connection.prepareStatement(anyString())).thenReturn(ps);

    repository = new UserStatsRepositoryImpl(dataSource);

    baseQuery = new UserStatsQuery(
        123L,
        LocalDate.of(2025, 7, 1),
        LocalDate.of(2025, 7, 31),
        StatsAggregationLevel.DAILY,
        Optional.empty(),
        Optional.empty()
    );
  }

  @Test
  void returnsAccuracySeries() throws Exception {

    when(ps.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true, true, false);
    when(rs.getDate(eq("period_date"))).thenReturn(java.sql.Date.valueOf("2025-07-01"), java.sql.Date.valueOf("2025-07-02"));
    when(rs.getDouble(eq("metric_value"))).thenReturn(0.75, 0.5);

    List<StatsPoint> res = repository.getMetricSeries(UserMetric.ACCURACY, baseQuery);

    assertNotNull(res);
    assertEquals(2, res.size());
    assertEquals(LocalDate.of(2025,7,1), res.get(0).getPeriodStart());
    assertEquals(0.75, res.get(0).getValue());
    assertEquals(LocalDate.of(2025,7,2), res.get(1).getPeriodStart());
    assertEquals(0.5, res.get(1).getValue());

    verify(ps, times(1)).setLong(anyInt(), eq(123L));
    verify(ps, times(1)).executeQuery();
  }

  @Test
  void returnsAvgSolveTimeOnlyForSuccesses() throws Exception {

    when(ps.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true, false);
    when(rs.getDate(eq("period_date"))).thenReturn(java.sql.Date.valueOf("2025-07-01"));
    when(rs.getDouble(eq("metric_value"))).thenReturn(12.34);

    List<StatsPoint> res = repository.getMetricSeries(UserMetric.AVG_SOLVE_TIME_SUCCESS, baseQuery);

    assertEquals(1, res.size());
    assertEquals(12.34, res.getFirst().getValue());
  }

  @Test
  void returnsTrainingVolume() throws Exception {

    when(ps.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true, false);
    when(rs.getDate(eq("period_date"))).thenReturn(java.sql.Date.valueOf("2025-07-10"));
    when(rs.getDouble(eq("metric_value"))).thenReturn(42.0);

    List<StatsPoint> res = repository.getMetricSeries(UserMetric.TRAINING_VOLUME, baseQuery);

    assertEquals(1, res.size());
    assertEquals(42.0, res.getFirst().getValue());
  }

  @Test
  void wrapsSqlException() throws Exception {

    // Forcing the DataSource to throw: wrap the repository with a DataSource that throws
    DataSource bad = new DataSource() {
      @Override public Connection getConnection() throws SQLException { throw new SQLException("Simulated DeadLock Exception"); }
      @Override public Connection getConnection(String username, String password) { throw new UnsupportedOperationException(); }
      @Override public PrintWriter getLogWriter() throws SQLException { return null; }
      @Override public void setLogWriter(PrintWriter out) throws SQLException { }
      @Override public void setLoginTimeout(int seconds) throws SQLException { }
      @Override public int getLoginTimeout() throws SQLException { return 0; }
      @Override public Logger getParentLogger() throws SQLFeatureNotSupportedException { throw new SQLFeatureNotSupportedException(); }
      @Override public <T> T unwrap(Class<T> iface) throws SQLException { throw new SQLException("unwrap not supported"); }
      @Override public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
    };

    UserStatsRepositoryImpl damagedDataset = new UserStatsRepositoryImpl(bad);

    RuntimeException ex = assertThrows(RuntimeException.class, () ->
        damagedDataset.getMetricSeries(UserMetric.TRAINING_VOLUME, baseQuery)
    );

    assertTrue(ex.getMessage().contains("Error fetching user stats series"));
  }
}