package com.davidp.chessjourney.infrastructure.database;

import com.davidp.chessjourney.domain.common.Tag;
import com.davidp.chessjourney.domain.common.TagRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TagRepositoryImpl implements TagRepository {

    private final DataSource dataSource;

    public TagRepositoryImpl(DataSource dataSource) {

        this.dataSource = dataSource;
    }


    @Override
    public List<Tag> getAll() {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT id, name, description, created_at FROM tags";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Tag tag = new Tag(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                tags.add(tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener tags de la base de datos", e);
        }
        return tags;
    }


    @Override
    public Tag getTagById(UUID id) {
        String sql = "SELECT id, name, description, created_at FROM tags WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Tag(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener tag con ID: " + id, e);
        }
        return null; // Devuelve null si no se encuentra el tag.
    }


    @Override
    public UUID save(Tag tag) {
        String sql = "INSERT INTO tags (id, name, description, created_at) VALUES (?, ?, ?, ?)";

        UUID generatedId = (tag.getId() != null) ? tag.getId() : UUID.randomUUID();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, generatedId);
            ps.setString(2, tag.getName());
            ps.setString(3, tag.getDescription());
            ps.setTimestamp(4, Timestamp.valueOf(tag.getCreatedAt()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al insertar el tag en la base de datos", e);
        }
        return generatedId;
    }
}