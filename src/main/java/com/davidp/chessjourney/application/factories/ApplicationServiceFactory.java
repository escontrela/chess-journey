package com.davidp.chessjourney.application.factories;

import com.davidp.chessjourney.domain.services.LichessService;
import com.davidp.chessjourney.infrastructure.LichessServiceImpl;
import com.davidp.chessjourney.application.service.UserService;
import com.davidp.chessjourney.application.service.UserServiceImpl;
import com.davidp.chessjourney.domain.UserRepository;

/**
 * Factory for creating application services.
 * Provides centralized creation of application layer services.
 */
public class ApplicationServiceFactory {

    private ApplicationServiceFactory() {

    }

    /**
     * Creates a UserService instance.
     *
     * @return UserService implementation
     */
    public static UserService createUserService() {

        UserRepository userRepository = RepositoryFactory.createUserRepository();
        return new UserServiceImpl(userRepository);
    }

    /**
     * Creates a LichessService instance.
     *
     * @return LichessService implementation
     */
    public static LichessService createLichessService() {
        return new LichessServiceImpl();
    }
}