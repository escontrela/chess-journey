package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.User;

/** Caso de uso para obtener un usuario por su ID. */
public interface GetUserByIdUseCase {

  /** Dado un ID de usuario, retorna el objeto User correspondiente o null si no existe. */
  User execute(long id);
}
