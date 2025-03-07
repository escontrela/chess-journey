package com.davidp.chessjourney.application.usecases;


import com.davidp.chessjourney.application.config.AppProperties;

/**
 * Save active user on the application.properties file.
 */
public class SaveActiveUserUseCaseImpl implements SaveActiveUserUseCase {

    @Override
    public void execute(long userId) {
        if (userId <= 0) {
            System.err.println("❌ Error: El userId no es válido.");
            return;
        }

        AppProperties.getInstance().setActiveUserId(userId);
        AppProperties.getInstance().saveProperties();

        System.out.println("✅ Usuario activo guardado correctamente: " + userId);
    }
}
