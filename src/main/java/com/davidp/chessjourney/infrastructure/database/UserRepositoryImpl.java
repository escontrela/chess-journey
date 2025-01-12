package com.davidp.chessjourney.infrastructure.database;

import com.davidp.chessjourney.domain.User;
import com.davidp.chessjourney.domain.UserRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class UserRepositoryImpl implements UserRepository {

  private final DataSource dataSource;

  public UserRepositoryImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public List<User> getAll() {

    List<User> result = new ArrayList<>();
    String sql = "SELECT id,email, firstname,lastname FROM users";

    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {

        long userId = rs.getLong("id");
        String email = rs.getString("email");
        String firstname = rs.getString("firstname");
        String lastname = rs.getString("lastname");

        User user = new User(userId, email, firstname, lastname);
        result.add(user);
      }
    } catch (Exception e) {

      e.printStackTrace();
      // Manejar excepción según tus necesidades (lanzarla, loguearla, etc.)
    }

    return result;
  }

  @Override
  public User getUserById(long id) {
    String sql = "SELECT id, email, firstname, lastname FROM users WHERE id = ?";

    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, id);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          long userId = rs.getLong("id");
          String email = rs.getString("email");
          String firstname = rs.getString("firstname");
          String lastname = rs.getString("lastname");

          // Construimos el objeto User con los datos de la fila
          return new User(userId, email, firstname, lastname);
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      // Manejar la excepción según tus necesidades (log, rethrow, etc.)
    }

    // Si no se encontró ningún registro con ese ID, retornamos null
    // TODO Fix it, sino se encuentra no deberíamos devolver null, valorar una exception o un
    // Optional
    return null;
  }

  @Override
  public boolean update(User user) {
    String sql = "UPDATE users SET email = ?, firstname = ?, lastname = ? WHERE id = ?";

    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, user.getEmail());
      ps.setString(2, user.getFirstname());
      ps.setString(3, user.getLastname());
      ps.setLong(4, user.getId());

      int rowsAffected = ps.executeUpdate();
      // Si rowsAffected > 0, significa que al menos una fila fue actualizada
      return rowsAffected > 0;

    } catch (Exception e) {
      e.printStackTrace();
      // Manejar excepción según tus necesidades (log, rethrow, etc.)
      return false;
    }
  }
}
