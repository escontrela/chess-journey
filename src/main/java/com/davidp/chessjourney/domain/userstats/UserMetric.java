package com.davidp.chessjourney.domain.userstats;

/** Las 3 métricas que acordamos (casos 1, 2 y 5). */
public enum UserMetric {

  ACCURACY,              // AVG(successful::int) -> % si lo multiplicas por 100 en UI
  AVG_SOLVE_TIME_SUCCESS, // AVG(time_taken_seconds) WHERE successful=true
  TRAINING_VOLUME        // COUNT(*) intentados
}
