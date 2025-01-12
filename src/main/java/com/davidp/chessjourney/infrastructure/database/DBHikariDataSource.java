package com.davidp.chessjourney.infrastructure.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class DBHikariDataSource {

  // Instancia única (volatile para el double-checked locking)
  private static volatile DBHikariDataSource instance = null;

  // El pool de conexiones Hikari
  private final HikariDataSource dataSource;

  /** Constructor privado: carga las propiedades y crea el dataSource. */
  private DBHikariDataSource() {
    // Cargar la configuración desde el classpath (por ejemplo /hikari.properties)
    HikariConfig config = new HikariConfig("/dbconfig.properties");

    // Inicializar el pool de HikariCP
    this.dataSource = new HikariDataSource(config);
  }

  /** Método de acceso al Singleton con double-checked locking. */
  public static DBHikariDataSource getInstance() {
    if (instance == null) {
      synchronized (DBHikariDataSource.class) {
        if (instance == null) {
          instance = new DBHikariDataSource();
        }
      }
    }
    return instance;
  }

  /** Obtiene el DataSource (pool de conexiones). */
  public DataSource getDataSource() {
    return dataSource;
  }

  /** Obtiene una conexión del pool. */
  public Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
}
