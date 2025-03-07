package com.davidp.chessjourney.application.usecases;

/**
 * Caso de uso para guardar el ID del usuario activo en el archivo de configuración.
 */
public interface SaveActiveUserUseCase {

    void execute(long userId);
}