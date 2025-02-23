package com.davidp.chessjourney.domain;

import com.davidp.chessjourney.domain.common.UserExerciseStats;

import java.util.List;

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
}
