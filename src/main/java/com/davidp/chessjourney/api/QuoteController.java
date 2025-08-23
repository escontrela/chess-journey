package com.davidp.chessjourney.api;

import com.davidp.chessjourney.application.usecases.SaveQuoteUseCase;
import com.davidp.chessjourney.domain.Quote;
import io.javalin.http.Handler;

/**
 * Controlador encargado de gestionar las operaciones relacionadas con las citas (quotes).
 * Expone el endpoint para guardar una cita utilizando el caso de uso correspondiente.
 */
public class QuoteController {

    private SaveQuoteUseCase saveQuoteUseCase = null;

    public QuoteController(SaveQuoteUseCase saveQuoteUseCase) {

        this.saveQuoteUseCase = saveQuoteUseCase;
    }

    public Handler saveQuote = ctx -> {

        QuoteRequest request = ctx.bodyAsClass(QuoteRequest.class);
        Quote savedQuote = saveQuoteUseCase.execute(request.text, request.author);
        ctx.json(savedQuote);
    };

    private static class QuoteRequest {

        public String text;
        public String author;
    }
}
