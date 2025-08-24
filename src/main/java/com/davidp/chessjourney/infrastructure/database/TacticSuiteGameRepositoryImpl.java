package com.davidp.chessjourney.infrastructure.database;

import com.davidp.chessjourney.domain.common.DifficultyLevel;
import com.davidp.chessjourney.domain.common.Exercise;
import com.davidp.chessjourney.domain.common.ExerciseType;
import com.davidp.chessjourney.domain.games.tactic.TacticSuiteGame;
import com.davidp.chessjourney.domain.games.tactic.TacticSuiteGameRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of TacticSuiteGameRepository using PostgreSQL database.
 */
public class TacticSuiteGameRepositoryImpl implements TacticSuiteGameRepository {

    private final DataSource dataSource;

    public TacticSuiteGameRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public UUID save(TacticSuiteGame tacticSuiteGame) {
        String sql = "INSERT INTO tactic_suite_games (id, name, type, created_at, updated_at) VALUES (?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            UUID id = tacticSuiteGame.getId();
            stmt.setObject(1, id);
            stmt.setString(2, tacticSuiteGame.getName());
            stmt.setString(3, tacticSuiteGame.getType().name());
            stmt.setTimestamp(4, Timestamp.valueOf(tacticSuiteGame.getCreatedAt()));
            stmt.setTimestamp(5, Timestamp.valueOf(tacticSuiteGame.getUpdatedAt()));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return (UUID) rs.getObject("id");
            }
            throw new RuntimeException("Failed to save TacticSuiteGame");
            
        } catch (SQLException e) {
            throw new RuntimeException("Error saving TacticSuiteGame", e);
        }
    }

    @Override
    public TacticSuiteGame getById(UUID id) {
        String sql = "SELECT id, name, type, created_at, updated_at FROM tactic_suite_games WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapRowToTacticSuiteGame(rs);
            }
            return null;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching TacticSuiteGame by ID", e);
        }
    }

    @Override
    public List<TacticSuiteGame> getAll() {
        String sql = "SELECT id, name, type, created_at, updated_at FROM tactic_suite_games ORDER BY created_at DESC";
        List<TacticSuiteGame> suites = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                suites.add(mapRowToTacticSuiteGame(rs));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all TacticSuiteGames", e);
        }
        
        return suites;
    }

    @Override
    public List<TacticSuiteGame> getByType(TacticSuiteGame.Type type) {
        String sql = "SELECT id, name, type, created_at, updated_at FROM tactic_suite_games WHERE type = ? ORDER BY created_at DESC";
        List<TacticSuiteGame> suites = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                suites.add(mapRowToTacticSuiteGame(rs));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching TacticSuiteGames by type", e);
        }
        
        return suites;
    }

    @Override
    public List<TacticSuiteGame> getByUserId(long userId) {
        String sql = """
            SELECT tsg.id, tsg.name, tsg.type, tsg.created_at, tsg.updated_at 
            FROM tactic_suite_games tsg
            INNER JOIN tactic_suite_game_users tsgu ON tsg.id = tsgu.tactic_suite_game_id
            WHERE tsgu.user_id = ? 
            ORDER BY tsg.created_at DESC
            """;
        List<TacticSuiteGame> suites = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                suites.add(mapRowToTacticSuiteGame(rs));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching TacticSuiteGames by user ID", e);
        }
        
        return suites;
    }

    @Override
    public void associateUser(UUID tacticSuiteGameId, long userId) {
        String sql = "INSERT INTO tactic_suite_game_users (tactic_suite_game_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, tacticSuiteGameId);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error associating user with TacticSuiteGame", e);
        }
    }

    @Override
    public void removeUserAssociation(UUID tacticSuiteGameId, long userId) {
        String sql = "DELETE FROM tactic_suite_game_users WHERE tactic_suite_game_id = ? AND user_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, tacticSuiteGameId);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error removing user association with TacticSuiteGame", e);
        }
    }

    @Override
    public List<Exercise> getExercisesForSuite(UUID tacticSuiteGameId) {
        String sql = """
            SELECT e.id, e.fen, e.pgn, et.id as type_id, et.name as type_name, et.description as type_description,
                   dl.id as difficulty_id, dl.level_name as difficulty_name, dl.description as difficulty_description,
                   e.created_at, e.updated_at, tsge.sequence_order
            FROM exercises e
            INNER JOIN tactic_suite_game_exercises tsge ON e.id = tsge.exercise_id
            INNER JOIN exercise_types et ON e.type_id = et.id
            INNER JOIN difficulty_levels dl ON e.difficulty_id = dl.id
            WHERE tsge.tactic_suite_game_id = ?
            ORDER BY tsge.sequence_order ASC
            """;
        List<Exercise> exercises = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, tacticSuiteGameId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                exercises.add(mapRowToExercise(rs));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching exercises for TacticSuiteGame", e);
        }
        
        return exercises;
    }

    @Override
    public void addExerciseToSuite(UUID tacticSuiteGameId, UUID exerciseId, int sequenceOrder) {
        String sql = "INSERT INTO tactic_suite_game_exercises (tactic_suite_game_id, exercise_id, sequence_order) VALUES (?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, tacticSuiteGameId);
            stmt.setObject(2, exerciseId);
            stmt.setInt(3, sequenceOrder);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error adding exercise to TacticSuiteGame", e);
        }
    }

    @Override
    public void removeExerciseFromSuite(UUID tacticSuiteGameId, UUID exerciseId) {
        String sql = "DELETE FROM tactic_suite_game_exercises WHERE tactic_suite_game_id = ? AND exercise_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, tacticSuiteGameId);
            stmt.setObject(2, exerciseId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error removing exercise from TacticSuiteGame", e);
        }
    }

    private TacticSuiteGame mapRowToTacticSuiteGame(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String name = rs.getString("name");
        TacticSuiteGame.Type type = TacticSuiteGame.Type.valueOf(rs.getString("type"));
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
        
        return new TacticSuiteGame(id, name, type, createdAt, updatedAt);
    }

    private Exercise mapRowToExercise(ResultSet rs) throws SQLException {
        UUID exerciseId = (UUID) rs.getObject("id");
        String fen = rs.getString("fen");
        String pgn = rs.getString("pgn");
        
        // Map ExerciseType
        UUID typeId = (UUID) rs.getObject("type_id");
        String typeName = rs.getString("type_name");
        String typeDescription = rs.getString("type_description");
        ExerciseType exerciseType = new ExerciseType(typeId, typeName, typeDescription);
        
        // Map DifficultyLevel
        UUID difficultyId = (UUID) rs.getObject("difficulty_id");
        String difficultyName = rs.getString("difficulty_name");
        String difficultyDescription = rs.getString("difficulty_description");
        DifficultyLevel difficultyLevel = new DifficultyLevel(difficultyId, difficultyName, difficultyDescription);
        
        return new Exercise(exerciseId, fen, pgn, exerciseType, difficultyLevel);
    }
}