package com.davidp.chessjourney.domain.games.tactic;

import com.davidp.chessjourney.domain.common.Exercise;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Represents a Tactic Suite Game that groups multiple exercises together.
 * Can be of type RANDOM (exercises generated on the fly) or FIXED (predefined sequence).
 */
public class TacticSuiteGame {

    public enum Type {
        RANDOM, FIXED
    }

    private final UUID id;
    private final String name;
    private final Type type;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // For FIXED type, exercises are loaded from database relations
    // For RANDOM type, exercises are generated on the fly
    private List<Exercise> exercises;

    public TacticSuiteGame(UUID id, String name, Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public TacticSuiteGame(UUID id, String name, Type type, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    @Override
    public String toString() {
        return "TacticSuiteGame{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", exercises=" + (exercises != null ? exercises.size() + " exercises" : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TacticSuiteGame)) return false;
        TacticSuiteGame that = (TacticSuiteGame) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}