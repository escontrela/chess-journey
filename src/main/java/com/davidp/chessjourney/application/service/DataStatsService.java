package com.davidp.chessjourney.application.service;

import com.davidp.chessjourney.domain.common.AggregatedStats;

import java.util.*;

/**
 * This service provides utilities to prepare chart data series for UI consumption.
 * It can align up to two time series by date, fill missing values with zeros,
 * and produce labels suitable for X axis rendering.
 */
public interface DataStatsService {

    /**
     * Prepare and align up to two series (dataset1 and dataset2) for the X axis:
     * - Merge the dates present in both datasets.
     * - Sort and take the last maxEntries entries (default 31).
     * - Fill missing values with 0.0 so both series have the same length.
     *
     * Returns the series as List<List<Double>> (each list are the values in the same order as labels)
     * and labels as List<String> with format "dd/MM".
     */
    ChartSeriesResult prepareAlignedSeries(List<AggregatedStats> dataset1, List<AggregatedStats> dataset2, int maxEntries);

    /**
     * Overload with default of 31 entries.
     */
    ChartSeriesResult prepareAlignedSeries(List<AggregatedStats> dataset1, List<AggregatedStats> dataset2);

    /**
     * Auxiliary result containing the aligned series and the labels.
     */
    record ChartSeriesResult(List<List<Double>> series, List<String> labels) {}

}
