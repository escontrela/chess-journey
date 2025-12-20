package com.davidp.chessjourney.application.usecases.userstats;

import com.davidp.chessjourney.domain.UserStatsRepository;
import com.davidp.chessjourney.domain.common.stats.TimeSeriesDataset;
import com.davidp.chessjourney.domain.common.stats.TimeSeriesPoint;
import com.davidp.chessjourney.domain.common.stats.TimeSeries;
import com.davidp.chessjourney.domain.userstats.StatsPoint;
import com.davidp.chessjourney.domain.userstats.UserMetric;
import com.davidp.chessjourney.domain.userstats.StatsAggregationLevel;
import com.davidp.chessjourney.domain.userstats.UserStatsQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetUserMetricTimeSeriesDatasetUseCaseImplTest {

  private UserStatsRepository userStatsRepository;
  private GetUserMetricTimeSeriesDatasetUseCaseImpl useCase;

  private final long userId = 42L;
  private final UUID typeA = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private final UUID typeB = UUID.fromString("22222222-2222-2222-2222-222222222222");

  private final LocalDate from = LocalDate.of(2025, 1, 1);
  private final LocalDate to = LocalDate.of(2025, 3, 31);

  @BeforeEach
  void setUp() {
    userStatsRepository = mock(UserStatsRepository.class);
    useCase = new GetUserMetricTimeSeriesDatasetUseCaseImpl(userStatsRepository);
  }

  @Test
  void accuracyProducesDataset() {
    // Preparar datos simulados (por defecto el use case pedirá por tipo si se pasan tipos)
    when(userStatsRepository.getMetricSeries(eq(UserMetric.ACCURACY), any(UserStatsQuery.class)))
        .thenReturn(List.of(
            new StatsPoint(LocalDate.of(2025,1,31), 0.8),
            new StatsPoint(LocalDate.of(2025,2,28), 0.75)
        ));

    GetUserMetricTimeSeriesDatasetRequest req = new GetUserMetricTimeSeriesDatasetRequest(
        userId,
        UserMetric.ACCURACY,
        List.of(typeA),
        Optional.empty(),
        from,
        to,
        StatsAggregationLevel.MONTHLY
    );

    TimeSeriesDataset ds = useCase.execute(req);

    assertNotNull(ds);
    assertFalse(ds.isEmpty());
    assertEquals(1, ds.getSeries().size());

    TimeSeries s = ds.getSeries().get(0);
    assertEquals("Type-" + typeA, s.getName());
    List<TimeSeriesPoint> pts = s.getPoints();
    assertEquals(2, pts.size());
    assertEquals(0.8, pts.get(0).getValue());
    assertEquals(0.75, pts.get(1).getValue());
  }

  @Test
  void avgSolveTimeProducesDataset() {
    when(userStatsRepository.getMetricSeries(eq(UserMetric.AVG_SOLVE_TIME_SUCCESS), any(UserStatsQuery.class)))
        .thenReturn(List.of(
            new StatsPoint(LocalDate.of(2025,1,31), 10.5),
            new StatsPoint(LocalDate.of(2025,2,28), 12.0)
        ));

    GetUserMetricTimeSeriesDatasetRequest req = new GetUserMetricTimeSeriesDatasetRequest(
        userId,
        UserMetric.AVG_SOLVE_TIME_SUCCESS,
        List.of(typeA, typeB),
        Optional.empty(),
        from,
        to,
        StatsAggregationLevel.MONTHLY
    );

    TimeSeriesDataset ds = useCase.execute(req);

    assertNotNull(ds);
    assertEquals(2, ds.getSeries().size());

    TimeSeries s0 = ds.getSeries().get(0);
    TimeSeries s1 = ds.getSeries().get(1);

    assertEquals("Type-" + typeA, s0.getName());
    assertEquals("Type-" + typeB, s1.getName());

    assertEquals(2, s0.getPoints().size());
    assertEquals(2, s1.getPoints().size());
  }

  @Test
  void volumeProducesDataset() {
    when(userStatsRepository.getMetricSeries(eq(UserMetric.TRAINING_VOLUME), any(UserStatsQuery.class)))
        .thenReturn(List.of(
            new StatsPoint(LocalDate.of(2025,1,31), 100),
            new StatsPoint(LocalDate.of(2025,2,28), 150)
        ));

    GetUserMetricTimeSeriesDatasetRequest req = new GetUserMetricTimeSeriesDatasetRequest(
        userId,
        UserMetric.TRAINING_VOLUME,
        List.of(), // empty list should result in series named "All"
        Optional.empty(),
        from,
        to,
        StatsAggregationLevel.MONTHLY
    );

    TimeSeriesDataset ds = useCase.execute(req);

    assertNotNull(ds);
    assertEquals(1, ds.getSeries().size());
    assertEquals("All", ds.getSeries().get(0).getName());
    assertEquals(2, ds.getSeries().get(0).getPoints().size());
  }
}
