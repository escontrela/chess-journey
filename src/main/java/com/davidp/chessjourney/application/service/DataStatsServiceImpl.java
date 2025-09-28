package com.davidp.chessjourney.application.service;

import com.davidp.chessjourney.domain.common.AggregatedStats;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataStatsServiceImpl implements DataStatsService {

  @Override
  public ChartSeriesResult prepareAlignedSeries(List<List<AggregatedStats>> datasets, int maxEntries) {
    if (datasets == null || datasets.isEmpty()) {
      return new ChartSeriesResult(Collections.emptyList(), Collections.emptyList());
    }

    // Normalizamos a una lista de mapas fecha -> valor*100
    List<Map<LocalDate, Double>> maps = new ArrayList<>();
    for (List<AggregatedStats> ds : datasets) {
      Map<LocalDate, Double> map = new HashMap<>();
      if (ds != null) {
        for (AggregatedStats s : ds) {
          if (s != null && s.getDate() != null) {
            map.put(s.getDate(), s.getValue() * 100.0);
          }
        }
      }
      maps.add(map);
    }

    // Unimos todas las fechas
    Set<LocalDate> allDates = new TreeSet<>();
    for (Map<LocalDate, Double> map : maps) {
      allDates.addAll(map.keySet());
    }

    List<LocalDate> sortedDates = new ArrayList<>(allDates);
    if (sortedDates.isEmpty()) {
      return new ChartSeriesResult(Collections.emptyList(), Collections.emptyList());
    }

    int safeMax = Math.max(1, maxEntries);
    int start = Math.max(0, sortedDates.size() - safeMax);
    List<LocalDate> window = sortedDates.subList(start, sortedDates.size());

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

    List<String> labels = new ArrayList<>(window.size());
    for (LocalDate d : window) {
      labels.add(d.format(formatter));
    }

    List<List<Double>> series = new ArrayList<>(maps.size());
    for (Map<LocalDate, Double> map : maps) {
      List<Double> values = new ArrayList<>(window.size());
      for (LocalDate d : window) {
        values.add(map.getOrDefault(d, 0.0));
      }
      series.add(values);
    }

    return new ChartSeriesResult(series, labels);
  }

  @Override
  public ChartSeriesResult prepareAlignedSeries(List<List<AggregatedStats>> datasets) {
    return prepareAlignedSeries(datasets, 31);
  }

}
