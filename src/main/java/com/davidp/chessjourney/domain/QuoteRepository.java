package com.davidp.chessjourney.domain;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Quote entities.
 * Provides methods to save a quote, retrieve a random quote, and get all quotes.
 */
public interface QuoteRepository {

  /**
   * Saves a quote entity to the repository.
   *
   * @param quote the Quote to be saved
   * @return the saved Quote entity
   */
  Quote save(Quote quote);

  /**
   * Retrieves a random quote from the repository.
   *
   * @return an Optional containing a random Quote, or empty if none exist
   */
  Optional<Quote> getRandomQuote();

  /**
   * Retrieves all quotes from the repository.
   *
   * @return a list of all Quote entities
   */
  List<Quote> getAllQuotes();
}
