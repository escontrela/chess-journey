package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.Quote;
import com.davidp.chessjourney.domain.QuoteRepository;

public class SaveQuoteUseCase {
    private final QuoteRepository quoteRepository;

    public SaveQuoteUseCase(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    public Quote execute(String text, String author) {
        Quote quote = new Quote(text, author);
        return quoteRepository.save(quote);
    }
}
