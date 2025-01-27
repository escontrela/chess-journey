package com.davidp.chessjourney.application.factories;

import com.davidp.chessjourney.application.usecases.*;
import com.davidp.chessjourney.domain.UserRepository;

/** Factoría para instanciar casos de uso (UseCases). */
public class UseCaseFactory {

  private UseCaseFactory() {
    // Evitar instancias
  }

  /** Crea el caso de uso GetUsersUseCase usando la implementación de repositorio adecuada. */
  public static GetUsersUseCase createGetUsersUseCase() {
    // 2) Crear el repositorio a través de la factoría de repositorios
    UserRepository userRepo = RepositoryFactory.createUserRepository();

    // 3) Instanciar la implementación del caso de uso, inyectando el repo
    return new GetUsersUseCaseImpl(userRepo);
  }

  public static GetUserByIdUseCase createGetUserByIdUseCase() {

    UserRepository userRepo = RepositoryFactory.createUserRepository();
    return new GetUserByIdUseCaseImpl(userRepo);
  }

  public static SaveUserUseCase createSaveUserUseCase() {

    UserRepository userRepo = RepositoryFactory.createUserRepository();
    return new SaveUserUseCaseImpl(userRepo);
  }

  public static MemoryGameUseCase createMemoryGameUseCase() {

    UserRepository userRepo = RepositoryFactory.createUserRepository();
    return new MemoryGameUseCaseImpl(userRepo);
  }
}
