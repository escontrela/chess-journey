package com.davidp.chessjourney.domain.common;
 

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa el ELO actual de un usuario para un tipo específico de ELO.
 */
public class UserElo {

    private final UUID id;
    private final long userId;  // Usamos long porque la tabla `users` tiene claves INT8
    private final EloType eloType;
    private int currentElo;
    private LocalDateTime lastUpdated;

    public UserElo(UUID id, long userId, EloType eloType, int currentElo, LocalDateTime lastUpdated) {
        this.id = id;
        this.userId = userId;
        this.eloType = eloType;
        this.currentElo = currentElo;
        this.lastUpdated = lastUpdated;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public EloType getEloType() {
        return eloType;
    }

    public int getCurrentElo() {
        return currentElo;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    // Métodos para actualizar el ELO
    public void updateElo(int newElo) {
        this.currentElo = newElo;
        this.lastUpdated = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "UserElo{" +
                "id=" + id +
                ", userId=" + userId +
                ", eloType=" + eloType.getTypeName() +
                ", currentElo=" + currentElo +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserElo)) return false;
        UserElo userElo = (UserElo) o;
        return id.equals(userElo.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
