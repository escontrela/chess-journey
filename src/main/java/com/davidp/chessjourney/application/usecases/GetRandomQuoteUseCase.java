package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.Quote;
import com.davidp.chessjourney.domain.QuoteRepository;

public class GetRandomQuoteUseCase {
    private final QuoteRepository quoteRepository;

    public GetRandomQuoteUseCase(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    public Quote execute() {
        return quoteRepository.getRandomQuote()
                .orElse(new Quote("Chess is imagination", "Anonymous"));
    }
}
