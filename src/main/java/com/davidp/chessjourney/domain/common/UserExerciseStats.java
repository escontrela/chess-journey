package com.davidp.chessjourney.domain.common;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa las estadísticas de un ejercicio realizado por un usuario.
 */
public class UserExerciseStats {

    private final UUID id;
    private final long userId;
    private final UUID exerciseId;
    private final LocalDateTime attemptDate;
    private final boolean successful;
    private final int timeTakenSeconds;
    private final int attempts;
    private final UUID difficultyId;


    public UserExerciseStats(UUID id, long userId, UUID exerciseId, LocalDateTime attemptDate,
                             boolean successful, int timeTakenSeconds, int attempts, UUID difficultyId) {
        this.id = id;
        this.userId = userId;
        this.exerciseId = exerciseId;
        this.attemptDate = attemptDate;
        this.successful = successful;
        this.timeTakenSeconds = timeTakenSeconds;
        this.attempts = attempts;
        this.difficultyId = difficultyId;
    }

    public UUID getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public UUID getExerciseId() {
        return exerciseId;
    }

    public LocalDateTime getAttemptDate() {
        return attemptDate;
    }

    public boolean wasSuccessful() {
        return successful;
    }

    public int getTimeTakenSeconds() {
        return timeTakenSeconds;
    }

    public int getAttempts() {
        return attempts;
    }

    public UUID getDifficultyId() {
        return difficultyId;
    }

    @Override
    public String toString() {
        return "UserExerciseStats{" +
                "id=" + id +
                ", userId=" + userId +
                ", exerciseId=" + exerciseId +
                ", attemptDate=" + attemptDate +
                ", successful=" + successful +
                ", timeTakenSeconds=" + timeTakenSeconds +
                ", attempts=" + attempts +
                ", difficultyId=" + difficultyId +
                '}';
    }
}
