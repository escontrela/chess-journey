package com.davidp.chessjourney.domain;

import com.davidp.chessjourney.domain.common.AggregatedStats;
import com.davidp.chessjourney.domain.common.UserExerciseStats;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface UserRepository {

  List<User> getAll();

  User getUserById(long id);

  boolean update(User user);

  /**
   * Inserta una estadística de ejercicio en la base de datos.
   *
   * @param stats Las estadísticas del ejercicio a registrar.
   * @return true si la operación fue exitosa, false en caso contrario.
   */
  boolean insertExerciseStats(UserExerciseStats stats);

  /**
   * Obtiene la media de éxito de los ejercicios por día/mes/año para un usuario, tipo de juego y dificultad.
   */
  List<AggregatedStats> getSuccessRateByPeriod(long userId, UUID gameType, UUID difficultyId,
                                               LocalDate startDate, LocalDate endDate, String period);

  /**
   * Obtiene el tiempo total consumido en ejercicios exitosos por día/mes/año para un usuario.
   */
  List<AggregatedStats> getTotalTimeSpentByPeriod(long userId, UUID gameType, UUID difficultyId,
                                                  LocalDate startDate, LocalDate endDate, String period);
}
