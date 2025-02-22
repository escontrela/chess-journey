package com.davidp.chessjourney.domain.common;
import java.util.UUID;

/**
 * Representa el nivel de dificultad de un ejercicio (easy, medium, hard).
 */
public class DifficultyLevel {

    private final UUID id;
    private final String levelName;
    private final String description;

    public DifficultyLevel(UUID id, String levelName, String description) {
        this.id = id;
        this.levelName = levelName;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public String getLevelName() {
        return levelName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "DifficultyLevel{" +
                "id=" + id +
                ", levelName='" + levelName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DifficultyLevel)) return false;
        DifficultyLevel that = (DifficultyLevel) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
