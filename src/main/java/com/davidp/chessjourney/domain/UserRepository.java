package com.davidp.chessjourney.domain;

import com.davidp.chessjourney.domain.common.AggregatedStats;
import com.davidp.chessjourney.domain.common.UserElo;
import com.davidp.chessjourney.domain.common.UserExerciseStats;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

  /**
   * Obtiene el ELO actual de un usuario para un tipo específico de ELO.
   *
   * @param userId el ID del usuario
   * @param eloTypeId el ID del tipo de ELO
   * @return el UserElo si existe, Optional.empty() si no existe
   */
  Optional<UserElo> getUserEloByType(long userId, UUID eloTypeId);

  /**
   * Obtiene todos los ELOs de un usuario.
   *
   * @param userId el ID del usuario
   * @return lista de UserElo para el usuario, puede estar vacía si no tiene ELOs
   */
  List<UserElo> getAllUserElos(long userId);
}
