package com.davidp.chessjourney.application.service;

import com.davidp.chessjourney.domain.common.AggregatedStats;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataStatsServiceImpl implements DataStatsService {

  public DataStatsService.ChartSeriesResult prepareAlignedSeries(
      List<AggregatedStats> dataset1, List<AggregatedStats> dataset2, int maxEntries) {

    if ((dataset1 == null || dataset1.isEmpty()) && (dataset2 == null || dataset2.isEmpty())) {
      return new DataStatsService.ChartSeriesResult(
          Collections.emptyList(), Collections.emptyList());
    }

    // Mapear fecha -> valor (multiplicamos por 100 para porcentaje si es necesario)
    Map<LocalDate, Double> map1 = new HashMap<>();
    if (dataset1 != null) {
      for (AggregatedStats s : dataset1) {
        if (s != null && s.getDate() != null) {
          map1.put(s.getDate(), s.getValue() * 100.0);
        }
      }
    }

    Map<java.time.LocalDate, Double> map2 = new HashMap<>();
    boolean hasSecond = dataset2 != null && !dataset2.isEmpty();
    if (hasSecond) {
      for (AggregatedStats s : dataset2) {
        if (s != null && s.getDate() != null) {
          map2.put(s.getDate(), s.getValue() * 100.0);
        }
      }
    }

    // Unir todas las fechas y ordenarlas
    Set<LocalDate> allDates = new TreeSet<>();
    allDates.addAll(map1.keySet());
    if (hasSecond) allDates.addAll(map2.keySet());

    List<java.time.LocalDate> sortedDates = new ArrayList<>(allDates);
    if (sortedDates.isEmpty()) {
      return new DataStatsService.ChartSeriesResult(
          Collections.emptyList(), Collections.emptyList());
    }

    // Limitar a las últimas maxEntries fechas
    int start = Math.max(0, sortedDates.size() - Math.max(1, maxEntries));
    List<java.time.LocalDate> window = sortedDates.subList(start, sortedDates.size());

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

    List<String> labels = new ArrayList<>(window.size());
    List<Double> series1 = new ArrayList<>(window.size());
    List<Double> series2 = new ArrayList<>(window.size());

    for (java.time.LocalDate date : window) {
      labels.add(date.format(formatter));
      series1.add(map1.getOrDefault(date, 0.0));
      if (hasSecond) {
        series2.add(map2.getOrDefault(date, 0.0));
      }
    }

    List<List<Double>> series = new ArrayList<>();
    series.add(series1);
    if (hasSecond) series.add(series2);

    return new DataStatsService.ChartSeriesResult(series, labels);
  }

  public DataStatsService.ChartSeriesResult prepareAlignedSeries(
      List<AggregatedStats> dataset1, List<AggregatedStats> dataset2) {
    return prepareAlignedSeries(dataset1, dataset2, 31);
  }
}
