package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.ChessGame;
import com.davidp.chessjourney.domain.UserRepository;

/** Implementación concreta del caso de uso para guardar (insertar/actualizar) un usuario. */
public class MemoryGameUseCaseImpl implements MemoryGameUseCase {

  private final UserRepository userRepository;

  public MemoryGameUseCaseImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public ChessGame execute(long userId) {

    // Set the memory game for the user

    return new ChessGame(null, null, null, null, null);
  }
}
