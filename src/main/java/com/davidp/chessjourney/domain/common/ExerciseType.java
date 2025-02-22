package com.davidp.chessjourney.domain.common;

import java.util.UUID;

/**
 * Representa el tipo de ejercicio (memory_game, tactic, endgame, etc.).
 */
public class ExerciseType {

    private final UUID id;
    private final String name;
    private final String description;

    public ExerciseType(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "ExerciseType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExerciseType)) return false;
        ExerciseType that = (ExerciseType) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
