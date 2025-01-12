package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.User;
import com.davidp.chessjourney.domain.UserRepository;

public class GetUserByIdUseCaseImpl implements GetUserByIdUseCase {

  private final UserRepository userRepository;

  /** Constructor que recibe la interfaz del repositorio (inyección de dependencias). */
  public GetUserByIdUseCaseImpl(UserRepository userRepository) {

    this.userRepository = userRepository;
  }

  @Override
  public User execute(long id) {

    return userRepository.getUserById(id);
  }
}
