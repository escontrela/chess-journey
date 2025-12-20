package com.davidp.chessjourney.application.usecases.userstats;


import com.davidp.chessjourney.domain.common.stats.TimeSeriesDataset;

public interface GetUserMetricTimeSeriesDatasetUseCase {

  TimeSeriesDataset execute(GetUserMetricTimeSeriesDatasetRequest request);
}