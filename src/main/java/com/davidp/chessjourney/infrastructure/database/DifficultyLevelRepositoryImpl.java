package com.davidp.chessjourney.infrastructure.database;


import com.davidp.chessjourney.domain.common.DifficultyLevel;
import com.davidp.chessjourney.domain.common.DifficultyLevelRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementación JDBC de DifficultyLevelRepository para CockroachDB.
 */
public class DifficultyLevelRepositoryImpl implements DifficultyLevelRepository {

    private final DataSource dataSource;

    public DifficultyLevelRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<DifficultyLevel> getAll() {
        List<DifficultyLevel> levels = new ArrayList<>();
        String sql = "SELECT id, level_name, description FROM difficulty_levels";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                levels.add(
                        new DifficultyLevel(
                                UUID.fromString(rs.getString("id")),
                                rs.getString("level_name"),
                                rs.getString("description")
                        )
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching difficulty levels", e);
        }
        return levels;
    }

    @Override
    public DifficultyLevel getById(UUID id) {
        String sql = "SELECT id, level_name, description FROM difficulty_levels WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DifficultyLevel(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("level_name"),
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching difficulty level by ID", e);
        }
        return null;
    }

    @Override
    public DifficultyLevel getByDifficulty(String difficulty) {
        String sql = "SELECT id, level_name, description FROM difficulty_levels WHERE level_name = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Configurar el parámetro de búsqueda
            ps.setString(1, difficulty);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DifficultyLevel(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("level_name"),
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching difficulty level by name: " + difficulty, e);
        }

        // Si no se encuentra el nivel de dificultad, devolver null o lanzar una excepción
        return null;
    }
}