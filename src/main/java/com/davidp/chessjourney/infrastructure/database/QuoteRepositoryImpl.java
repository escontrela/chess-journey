package com.davidp.chessjourney.infrastructure.database;

import com.davidp.chessjourney.domain.Quote;
import com.davidp.chessjourney.domain.QuoteRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the QuoteRepository interface using a relational database.
 * Provides methods to save quotes, retrieve a random quote, and fetch all quotes from the database.
 */
public class QuoteRepositoryImpl implements QuoteRepository {

    private final DataSource dataSource;

    public QuoteRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Quote save(Quote quote) {
        String sql = "INSERT INTO quotes (text, author) VALUES (?, ?) RETURNING id";
        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(sql)) {
            stmt.setString(1, quote.getText());
            stmt.setString(2, quote.getAuthor());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                quote.setId(rs.getLong("id"));
                return quote;
            }
            throw new RuntimeException("Failed to save quote");
        } catch (SQLException e) {
            throw new RuntimeException("Error saving quote", e);
        }
    }

    @Override
    public Optional<Quote> getRandomQuote() {
        String sql = "SELECT * FROM quotes ORDER BY RANDOM() LIMIT 1";
        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Quote(
                    rs.getLong("id"),
                    rs.getString("text"),
                    rs.getString("author")
                ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting random quote", e);
        }
    }

    @Override
    public List<Quote> getAllQuotes() {
        String sql = "SELECT * FROM quotes";
        List<Quote> quotes = new ArrayList<>();
        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                quotes.add(new Quote(
                    rs.getLong("id"),
                    rs.getString("text"),
                    rs.getString("author")
                ));
            }
            return quotes;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all quotes", e);
        }
    }
}