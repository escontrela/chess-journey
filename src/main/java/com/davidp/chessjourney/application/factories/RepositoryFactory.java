package com.davidp.chessjourney.application.factories;

import com.davidp.chessjourney.domain.UserRepository;
import com.davidp.chessjourney.domain.common.TagRepository;
import com.davidp.chessjourney.infrastructure.database.DBHikariDataSource;
import com.davidp.chessjourney.infrastructure.database.TagRepositoryImpl;
import com.davidp.chessjourney.infrastructure.database.UserRepositoryImpl;

public class RepositoryFactory {

  private RepositoryFactory() {

  }

  public static UserRepository createUserRepository() {

    return new UserRepositoryImpl(DBHikariDataSource.getInstance().getDataSource());
  }

  public static TagRepository createTagRepository() {

    return new TagRepositoryImpl(DBHikariDataSource.getInstance().getDataSource());
  }
}
