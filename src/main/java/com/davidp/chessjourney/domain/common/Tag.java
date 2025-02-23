package com.davidp.chessjourney.domain.common;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa una etiqueta (tag) utilizada para clasificar ejercicios por dificultad u otras categorías.
 */
public class Tag {

    private UUID id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    /**
     * Constructor vacío (para frameworks como Hibernate o serialización).
     */
    public Tag() {

        this.id = UUID.randomUUID(); // Genera un UUID si se instancia manualmente
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Constructor con parámetros.
     */
    public Tag(UUID id, String name, String description, LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    /**
     * Constructor sin ID (para creación manual de tags).
     */
    public Tag(String name, String description) {

        this(UUID.randomUUID(), name, description, LocalDateTime.now());
    }

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}