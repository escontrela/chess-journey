package com.davidp.chessjourney.application.factories;

import com.davidp.chessjourney.domain.UserRepository;
import com.davidp.chessjourney.domain.common.DifficultyLevelRepository;
import com.davidp.chessjourney.domain.common.ExerciseRepository;
import com.davidp.chessjourney.domain.common.TagRepository;
import com.davidp.chessjourney.infrastructure.database.*;

public class RepositoryFactory {

  private RepositoryFactory() {

  }

  public static UserRepository createUserRepository() {

    return new UserRepositoryImpl(DBHikariDataSource.getInstance().getDataSource());
  }

  public static TagRepository createTagRepository() {

    return new TagRepositoryImpl(DBHikariDataSource.getInstance().getDataSource());
  }

  public static ExerciseRepository createExerciseRepository() {
    return new ExerciseRepositoryImpl(DBHikariDataSource.getInstance().getDataSource());
  }

  public static DifficultyLevelRepository createDifficultyLevelRepository() {
    return new DifficultyLevelRepositoryImpl(DBHikariDataSource.getInstance().getDataSource());
  }
}
