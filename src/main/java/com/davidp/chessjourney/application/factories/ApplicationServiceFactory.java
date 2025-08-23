package com.davidp.chessjourney.application.factories;

import com.davidp.chessjourney.application.service.UserService;
import com.davidp.chessjourney.application.service.UserServiceImpl;

/**
 * Factory for creating application services.
 * Provides centralized creation of application layer services.
 */
public class ApplicationServiceFactory {

    private ApplicationServiceFactory() {
        // Prevent instantiation
    }

    /**
     * Creates a UserService instance.
     *
     * @return UserService implementation
     */
    public static UserService createUserService() {
        return UserServiceImpl.getInstance();
    }
}