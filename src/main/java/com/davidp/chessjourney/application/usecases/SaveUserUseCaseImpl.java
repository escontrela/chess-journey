package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.User;
import com.davidp.chessjourney.domain.UserRepository;

/**
 * Implementación concreta del caso de uso para guardar (insertar/actualizar) un usuario.
 */
public class SaveUserUseCaseImpl implements SaveUserUseCase {

    private final UserRepository userRepository;

    /**
     * Constructor que recibe la interfaz del repositorio (inyección de dependencias).
     */
    public SaveUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Llama al repositorio para salvar los datos del usuario.
     */
    @Override
    public boolean execute(User user) {
        // Simplemente delega en el repositorio
        return userRepository.update(user);
    }
}
