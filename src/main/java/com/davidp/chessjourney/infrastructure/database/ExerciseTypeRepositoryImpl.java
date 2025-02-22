package com.davidp.chessjourney.infrastructure.database;


import com.davidp.chessjourney.domain.common.ExerciseType;
import com.davidp.chessjourney.domain.common.ExerciseTypeRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementación JDBC de ExerciseTypeRepository.
 */
public class ExerciseTypeRepositoryImpl implements ExerciseTypeRepository {

    private final DataSource dataSource;

    public ExerciseTypeRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<ExerciseType> getAll() {
        List<ExerciseType> types = new ArrayList<>();
        String sql = "SELECT id, name, description FROM exercise_types";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                types.add(
                        new ExerciseType(
                                UUID.fromString(rs.getString("id")),
                                rs.getString("name"),
                                rs.getString("description")
                        )
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching exercise types", e);
        }
        return types;
    }

    @Override
    public ExerciseType getById(UUID id) {
        String sql = "SELECT id, name, description FROM exercise_types WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ExerciseType(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("name"),
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching exercise type by ID", e);
        }
        return null;
    }
}
