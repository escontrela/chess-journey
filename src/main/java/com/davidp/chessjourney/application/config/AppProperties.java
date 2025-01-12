package com.davidp.chessjourney.application.config;

import java.io.*;
import java.util.Properties;

public class AppProperties {

  private static volatile AppProperties instance = null;

  // Ruta en el classpath para LEER las propiedades (dentro de resources)
  private static final String RESOURCE_FILE = "/application.properties";

  // Archivo de SALIDA para ESCRIBIR los cambios (fuera del .jar)
  private static final String OUTPUT_FILE = "application.properties";

  private final Properties props = new Properties();

  // Constructor privado
  private AppProperties() {
    loadProperties();
  }

  /** Patrón Singleton con double-checked locking. */
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

  /** Carga las propiedades desde el archivo en resources. */
  private void loadProperties() {
    try (InputStream in = getClass().getResourceAsStream(RESOURCE_FILE)) {
      if (in != null) {
        props.load(in);
      } else {
        System.err.println("No se encontró " + RESOURCE_FILE + " en el classpath.");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Devuelve el valor de la propiedad con la clave indicada, o null si no existe. */
  public String getProperty(String key) {
    return props.getProperty(key);
  }

  /** Asigna o sobreescribe una propiedad en memoria. */
  public void setProperty(String key, String value) {
    props.setProperty(key, value);
  }

  /** Guarda en disco (archivo externo) todos los cambios que haya en memoria. */
  public void saveProperties() {
    try (OutputStream out = new FileOutputStream(OUTPUT_FILE)) {
      props.store(out, "Aplicación actualizada");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Obtiene el ID del usuario activo. Si no existe o no es numérico, retorna 0. */
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

  /** Actualiza el ID del usuario activo en las propiedades. */
  public void setActiveUserId(long userId) {
    setProperty("myapp.user.active.id", String.valueOf(userId));
  }
}
