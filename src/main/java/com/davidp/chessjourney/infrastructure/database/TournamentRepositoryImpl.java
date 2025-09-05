package com.davidp.chessjourney.infrastructure.database;

import com.davidp.chessjourney.domain.Tournament;
import com.davidp.chessjourney.domain.TournamentRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of TournamentRepository.
 */
public class TournamentRepositoryImpl implements TournamentRepository {

    private final DataSource dataSource;

    public TournamentRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Tournament save(Tournament tournament) {
        String sql = """
            INSERT INTO tournaments (hash_id, provincia, concejo, torneo, inicio, fin, ritmo, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
            ON CONFLICT (hash_id) DO NOTHING
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tournament.getHashId());
            ps.setString(2, tournament.getProvincia());
            ps.setString(3, tournament.getConcejo());
            ps.setString(4, tournament.getTorneo());
            ps.setDate(5, Date.valueOf(tournament.getInicio()));
            ps.setDate(6, Date.valueOf(tournament.getFin()));
            ps.setString(7, tournament.getRitmo());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving tournament", e);
        }

        return tournament;
    }

    @Override
    public List<Tournament> saveAll(List<Tournament> tournaments) {
        List<Tournament> savedTournaments = new ArrayList<>();
        for (Tournament tournament : tournaments) {
            try {
                save(tournament);
                savedTournaments.add(tournament);
            } catch (Exception e) {
                // Log error but continue with other tournaments
                System.err.println("Error saving tournament: " + tournament.getTorneo() + " - " + e.getMessage());
            }
        }
        return savedTournaments;
    }

    @Override
    public Optional<Tournament> findByHashId(String hashId) {
        String sql = "SELECT hash_id, provincia, concejo, torneo, inicio, fin, ritmo FROM tournaments WHERE hash_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hashId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToTournament(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding tournament by hash ID", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Tournament> findAll() {
        String sql = "SELECT hash_id, provincia, concejo, torneo, inicio, fin, ritmo FROM tournaments ORDER BY inicio ASC";
        List<Tournament> tournaments = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tournaments.add(mapRowToTournament(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all tournaments", e);
        }

        return tournaments;
    }

    @Override
    public List<Tournament> findUpcomingTournaments(LocalDate fromDate, int limit) {
        String sql = """
            SELECT hash_id, provincia, concejo, torneo, inicio, fin, ritmo 
            FROM tournaments 
            WHERE inicio >= ? 
            ORDER BY inicio ASC 
            LIMIT ?
            """;
        List<Tournament> tournaments = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(fromDate));
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tournaments.add(mapRowToTournament(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching upcoming tournaments", e);
        }

        return tournaments;
    }

    @Override
    public boolean existsByHashId(String hashId) {
        String sql = "SELECT 1 FROM tournaments WHERE hash_id = ? LIMIT 1";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hashId);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking tournament existence", e);
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM tournaments";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all tournaments", e);
        }
    }

    private Tournament mapRowToTournament(ResultSet rs) throws SQLException {
        return new Tournament(
            rs.getString("hash_id"),
            rs.getString("provincia"),
            rs.getString("concejo"),
            rs.getString("torneo"),
            rs.getDate("inicio").toLocalDate(),
            rs.getDate("fin").toLocalDate(),
            rs.getString("ritmo")
        );
    }
}