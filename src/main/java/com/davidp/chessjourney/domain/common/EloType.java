package com.davidp.chessjourney.domain.common;


import java.util.UUID;

/**
 * Representa un tipo de ELO (Standard, Rapid, Blitz, Exercises).
 */
public class EloType {

    private final UUID id;
    private final String typeName;
    private final String description;

    public EloType(UUID id, String typeName, String description) {
        this.id = id;
        this.typeName = typeName;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "EloType{" +
                "id=" + id +
                ", typeName='" + typeName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EloType)) return false;
        EloType eloType = (EloType) o;
        return id.equals(eloType.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
