package com.davidp.chessjourney.application.config;

import java.io.*;
import java.util.Properties;

public class AppProperties {

  private static volatile AppProperties instance = null;

  //Path to edited file
  private static final String EXTERNAL_CONFIG_FILE = "config/application.properties";

  // Pat to resources file (read-only)
  private static final String CLASSPATH_CONFIG_FILE = "/application.properties";

  private final Properties props = new Properties();

  private AppProperties() {

    loadProperties();
  }

  public static AppProperties getInstance() {
    if (instance == null) {
      synchronized (AppProperties.class) {
        if (instance == null) {
          instance = new AppProperties();
        }
      }
    }
    return instance;
  }

  /**
   * 📥 Carga las propiedades desde `config/application.properties` si existe. Si no, las carga
   * desde `resources/application.properties` (solo lectura).
   */
  private void loadProperties() {
    File configFile = new File(EXTERNAL_CONFIG_FILE);

    if (configFile.exists()) {
      // Si hay un archivo externo, cargar desde ahí (permite edición)
      try (InputStream in = new FileInputStream(configFile)) {
        props.load(in);
        System.out.println(
            "✅ Configuración cargada desde archivo externo: " + configFile.getAbsolutePath());
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      // Si no hay archivo externo, cargar desde resources (solo lectura)
      try (InputStream in = getClass().getResourceAsStream(CLASSPATH_CONFIG_FILE)) {
        if (in != null) {
          props.load(in);
          System.out.println("📂 Configuración cargada desde resources.");
        } else {
          System.err.println("⚠️ No se encontró el archivo de configuración en resources.");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public String getProperty(String key) {
    return props.getProperty(key);
  }

  public void setProperty(String key, String value) {
    props.setProperty(key, value);
  }

  public void saveProperties() {

    File configFile = new File(EXTERNAL_CONFIG_FILE);

   if ( !configFile.getParentFile().mkdirs()){

     System.err.println("⚠️ No se pudo crear el directorio de configuración.");
   }

    try (OutputStream out = new FileOutputStream(configFile)) {

      props.store(out, "Configuración actualizada");
      System.out.println("✅ Configuración guardada en: " + configFile.getAbsolutePath());

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public long getActiveUserId() {
    String val = getProperty("myapp.user.active.id");
    if (val == null) {
      return 0L;
    }
    try {
      return Long.parseLong(val);
    } catch (NumberFormatException e) {
      e.printStackTrace();
      return 0L;
    }
  }

  public void setActiveUserId(long userId) {
    setProperty("myapp.user.active.id", String.valueOf(userId));
  }
}