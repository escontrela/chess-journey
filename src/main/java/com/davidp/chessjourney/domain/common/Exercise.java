package com.davidp.chessjourney.domain.common;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Representa un ejercicio de ajedrez con su posición inicial (FEN), movimientos en PGN,
 * su tipo, nivel de dificultad y etiquetas.
 */
public class Exercise {

    private final UUID id;
    private String fen;
    private String pgn;
    private ExerciseType exerciseType;
    private DifficultyLevel difficultyLevel;
    private Set<Tag> tags; // Relación muchos-a-muchos
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Exercise(UUID id, String fen, String pgn, ExerciseType exerciseType, DifficultyLevel difficultyLevel) {
        this.id = id;
        this.fen = fen;
        this.pgn = pgn;
        this.exerciseType = exerciseType;
        this.difficultyLevel = difficultyLevel;
        this.tags = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Métodos de acceso (getters y setters)
    public UUID getId() {
        return id;
    }

    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
        updateTimestamp();
    }

    public String getPgn() {
        return pgn;
    }

    public void setPgn(String pgn) {
        this.pgn = pgn;
        updateTimestamp();
    }

    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(ExerciseType exerciseType) {
        this.exerciseType = exerciseType;
        updateTimestamp();
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
        updateTimestamp();
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
        updateTimestamp();
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        updateTimestamp();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", fen='" + fen + '\'' +
                ", pgn='" + pgn + '\'' +
                ", exerciseType=" + exerciseType +
                ", difficultyLevel=" + difficultyLevel +
                ", tags=" + tags +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exercise)) return false;
        Exercise exercise = (Exercise) o;
        return id.equals(exercise.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
