package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.User;

import java.util.List;

/**
 * Caso de uso para obtener todos los usuarios.
 */
public interface GetUsersUseCase {

    List<User> execute();
}
