package com.davidp.chessjourney.application.factories;

import com.davidp.chessjourney.domain.UserRepository;
import com.davidp.chessjourney.infrastructure.database.DBHikariDataSource;
import com.davidp.chessjourney.infrastructure.database.UserRepositoryImpl;


public class  RepositoryFactory {

    private RepositoryFactory() {
        // Constructor privado para no instanciar la factoría
    }

    public static UserRepository createUserRepository() {

            return new UserRepositoryImpl(DBHikariDataSource.getInstance().getDataSource());
    }
}