package com.davidp.chessjourney.application.usecases;


import com.davidp.chessjourney.domain.User;

/**
 * Caso de uso para guardar la información de un usuario.
 * Podría ser una inserción o actualización según lo que haga el repositorio.
 */
public interface SaveUserUseCase {

    /**
     * Guarda los datos de 'user' en la base de datos.
     *
     * @param user Objeto con los datos a persistir
     * @return El objeto User resultante (podría incluir un ID autogenerado, etc.)
     */
    boolean execute(User user);
}
