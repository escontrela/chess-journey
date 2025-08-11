package com.davidp.chessjourney.api;

import io.javalin.Javalin;
import com.davidp.chessjourney.application.factories.UseCaseFactory;
import com.davidp.chessjourney.application.usecases.GetUserByIdUseCase;
import com.davidp.chessjourney.application.usecases.SaveQuoteUseCase;

public class ApiConfig {

    private static volatile ApiConfig instance = null;

    private final Javalin app;
    private final QuoteController quoteController;
    private final ActiveUserController activeUserController;

    private ApiConfig(SaveQuoteUseCase saveQuoteUseCase, GetUserByIdUseCase getUserByIdUseCase) {

        this.app = Javalin.create();
        this.quoteController = new QuoteController(saveQuoteUseCase);
        this.activeUserController = new ActiveUserController(getUserByIdUseCase);
        configureRoutes();
    }

    public static ApiConfig getInstance() {

        if (instance == null) {
            synchronized (ApiConfig.class) {
                if (instance == null) {

                    SaveQuoteUseCase saveQuoteUseCase = UseCaseFactory.createSaveQuoteUseCase();
                    GetUserByIdUseCase getUserByIdUseCase = UseCaseFactory.createGetUserByIdUseCase();
                    instance = new ApiConfig(saveQuoteUseCase, getUserByIdUseCase);
                }
            }
        }
        return instance;
    }

    private void configureRoutes() {
        app.post("/chessjourney/quote", quoteController.saveQuote);
        app.get("/chessjourney/activeUser", activeUserController.getActiveUser);
    }

    public void start(int port) {
        app.start(port);
    }

    public void stop() {

        app.stop();
    }
}