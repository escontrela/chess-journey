package com.davidp.chessjourney.domain;

import java.util.Optional;

public class User {

  private final long id; // autogenerado en la BBDD
  private final String email; // unique
  private final String firstname; // NOT NULL
  private final String lastname; // NOT NULL

  public User(long id, String email, String firstname, String lastname) {
    this.id = id;
    this.email = email;
    this.firstname = firstname;
    this.lastname = lastname;
  }

  // Getters
  public long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstname() {
    return firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public String getInitials() {

    return Optional.ofNullable(firstname)
            .map(e -> e.substring(0, 1).toUpperCase())
            .orElseGet(() -> "")
        + Optional.ofNullable(lastname)
            .map(e -> e.substring(0, 1).toUpperCase())
            .orElseGet(() -> "");
  }

  @Override
  public String toString() {
    return "User{"
        + "id="
        + id
        + ", email='"
        + email
        + '\''
        + ", firstname='"
        + firstname
        + '\''
        + ", lastname='"
        + lastname
        + '\''
        + '}';
  }

  public static int calculateNewElo(int currentElo, int opponentElo, boolean successful, int kFactor) {

    double expectation = 1.0 / (1.0 + Math.pow(10, (opponentElo - currentElo) / 400.0));
    int result = successful ? 1 : 0;
    return currentElo + (int) (kFactor * (result - expectation));
  }

  /**
   * Calcula el nuevo ELO después de resolver un ejercicio.
   *
   * @param currentElo          ELO actual del jugador
   * @param exerciseDifficultyElo ELO asignado al ejercicio
   * @param successful           Si el ejercicio fue resuelto correctamente
   * @param timeTakenSeconds     Tiempo que tardó el jugador en resolver el ejercicio
   * @param expectedTimeSeconds  Tiempo esperado para resolver el ejercicio
   * @param kFactor              Factor K de actualización
   * @return El nuevo ELO actualizado
   */
  public static int calculateNewEloForExercise(int currentElo, int exerciseDifficultyElo, boolean successful,
                                    int timeTakenSeconds, int expectedTimeSeconds, int kFactor) {

    // Calcula la expectativa del resultado
    double expectation = 1.0 / (1.0 + Math.pow(10, (exerciseDifficultyElo - currentElo) / 400.0));

    // Resultado: 1 si resuelto correctamente, 0 si fallido
    int result = successful ? 1 : 0;

    // Factor de penalización o recompensa basado en el tiempo
    double timeFactor = 1.0;
    if (successful) {
      timeFactor = Math.max(0, 1 - ((double) timeTakenSeconds / expectedTimeSeconds));
    }

    // Cálculo del nuevo ELO
    int newElo = (int) (currentElo + kFactor * ((result + timeFactor) - expectation));

    // El ELO nunca puede ser menor a 100
    return Math.max(newElo, 100);
  }

}
