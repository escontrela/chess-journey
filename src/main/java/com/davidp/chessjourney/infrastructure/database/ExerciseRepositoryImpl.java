package com.davidp.chessjourney.infrastructure.database;


import com.davidp.chessjourney.domain.common.Exercise;
import com.davidp.chessjourney.domain.common.ExerciseType;
import com.davidp.chessjourney.domain.common.Tag;
import com.davidp.chessjourney.domain.common.DifficultyLevel;
import com.davidp.chessjourney.domain.common.ExerciseRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Implementación JDBC de ExerciseRepository para CockroachDB.
 */
public class ExerciseRepositoryImpl implements ExerciseRepository {

    private final DataSource dataSource;

    public ExerciseRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Guarda un nuevo ejercicio en la base de datos.
     */
    @Override
    public UUID save(Exercise exercise) {
        String sql = "INSERT INTO exercises (id, fen, pgn, type_id, difficulty_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        UUID id = exercise.getId() != null ? exercise.getId() : UUID.randomUUID();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);
            ps.setString(2, exercise.getFen());
            ps.setString(3, exercise.getPgn());
            ps.setObject(4, exercise.getExerciseType().getId());
            ps.setObject(5, exercise.getDifficultyLevel().getId());
            ps.setTimestamp(6, Timestamp.valueOf(exercise.getCreatedAt()));
            ps.setTimestamp(7, Timestamp.valueOf(exercise.getUpdatedAt()));

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving exercise", e);
        }

        // Guardar las etiquetas asociadas
        saveTags(exercise, id);
        return id;
    }

    /**
     * Guarda las etiquetas asociadas a un ejercicio.
     */
    private void saveTags(Exercise exercise, UUID exerciseId) {
        String sql = "INSERT INTO exercise_tags (exercise_id, tag_id) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Tag tag : exercise.getTags()) {
                ps.setObject(1, exerciseId);
                ps.setObject(2, tag.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving exercise tags", e);
        }
    }

    /**
     * Recupera un ejercicio por su ID, incluyendo etiquetas y relaciones.
     */
    @Override
    public Exercise getById(UUID id) {
        String sql = "SELECT e.id, e.fen, e.pgn, e.created_at, e.updated_at, " +
                "et.id AS type_id, et.name AS type_name, et.description AS type_description, " +
                "dl.id AS difficulty_id, dl.level_name, dl.description AS difficulty_description " +
                "FROM exercises e " +
                "JOIN exercise_types et ON e.type_id = et.id " +
                "JOIN difficulty_levels dl ON e.difficulty_id = dl.id " +
                "WHERE e.id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ExerciseType type = new ExerciseType(
                            UUID.fromString(rs.getString("type_id")),
                            rs.getString("type_name"),
                            rs.getString("type_description")
                    );

                    DifficultyLevel difficulty = new DifficultyLevel(
                            UUID.fromString(rs.getString("difficulty_id")),
                            rs.getString("level_name"),
                            rs.getString("difficulty_description")
                    );

                    Exercise exercise = new Exercise(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("fen"),
                            rs.getString("pgn"),
                            type,
                            difficulty
                    );

                    exercise.getTags().addAll(getTagsForExercise(id));
                    return exercise;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching exercise by ID", e);
        }

        return null;
    }

    /**
     * Obtiene las etiquetas asociadas a un ejercicio.
     */
    private Set<Tag> getTagsForExercise(UUID exerciseId) {
        Set<Tag> tags = new HashSet<>();
        String sql = "SELECT t.id, t.name, t.description, t.created_at " +
                "FROM exercise_tags et " +
                "JOIN tags t ON et.tag_id = t.id " +
                "WHERE et.exercise_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, exerciseId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tags.add(new Tag(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching tags for exercise", e);
        }

        return tags;
    }

    /**
     * Recupera una lista de ejercicios por nivel de dificultad con límite de cantidad.
     */
    @Override
    public List<Exercise> getExercisesByDifficulty(UUID difficultyId, int limit) {
        List<Exercise> exercises = new ArrayList<>();
        String sql = "SELECT e.id, e.fen, e.pgn, e.created_at, e.updated_at, " +
                "et.id AS type_id, et.name AS type_name, et.description AS type_description " +
                "FROM exercises e " +
                "JOIN exercise_types et ON e.type_id = et.id " +
                "WHERE e.difficulty_id = ? LIMIT ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, difficultyId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ExerciseType type = new ExerciseType(
                            UUID.fromString(rs.getString("type_id")),
                            rs.getString("type_name"),
                            rs.getString("type_description")
                    );

                    DifficultyLevel difficulty = new DifficultyLevel(difficultyId, "", "");

                    Exercise exercise = new Exercise(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("fen"),
                            rs.getString("pgn"),
                            type,
                            difficulty
                    );

                    exercise.getTags().addAll(getTagsForExercise(exercise.getId()));
                    exercises.add(exercise);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching exercises by difficulty", e);
        }

        return exercises;
    }

    /**
     * Recupera todos los ejercicios.
     */
    @Override
    public List<Exercise> getAll() {
        List<Exercise> exercises = new ArrayList<>();
        String sql = "SELECT e.id FROM exercises e";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UUID exerciseId = UUID.fromString(rs.getString("id"));
                exercises.add(getById(exerciseId));  // Reutiliza el método getById
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all exercises", e);
        }

        return exercises;
    }

    @Override
    public void assignTag(UUID exerciseId, UUID tagId) {
        String sql = "INSERT INTO exercise_tags (exercise_id, tag_id) VALUES (?, ?) ON CONFLICT DO NOTHING";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, exerciseId);
            ps.setObject(2, tagId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error assigning tag to exercise", e);
        }
    }
}
