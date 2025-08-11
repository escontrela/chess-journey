package com.davidp.chessjourney.domain;

/**
 * Represents a quote entity with an id, text, and author.
 * Used to encapsulate quote data within the application that
 * represents a quote from a chess player or related figure.
 */
public class Quote {
    private Long id;
    private String text;
    private String author;

    public Quote(Long id, String text, String author) {
        this.id = id;
        this.text = text;
        this.author = author;
    }

    public Quote(String text, String author) {
        this.text = text;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }

    public void setId(Long id) {
        this.id = id;
    }
}