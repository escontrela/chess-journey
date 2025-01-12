package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.User;
import com.davidp.chessjourney.domain.UserRepository;
import java.util.List;

/** Implementación del caso de uso para recuperar todos los usuarios. */
public class GetUsersUseCaseImpl implements GetUsersUseCase {

  private final UserRepository userRepository;

  /** Constructor que recibe la interfaz del repositorio (inyección de dependencias). */
  public GetUsersUseCaseImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public List<User> execute() {
    // Simplemente delega en el repositorio
    return userRepository.getAll();
  }
}
