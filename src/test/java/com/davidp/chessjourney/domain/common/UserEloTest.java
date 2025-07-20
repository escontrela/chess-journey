package com.davidp.chessjourney.domain.common;

import com.davidp.chessjourney.domain.User;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class UserEloTest {

  @Test
  public void testCalculateElo() {

    // Crear un tipo de ELO
    EloType blitzType = new EloType(UUID.randomUUID(), "blitz", "ELO para partidas Blitz");

    // Crear un ELO para un usuario
    UserElo userElo =
        new UserElo(
            UUID.randomUUID(),
            123456789L, // ID de usuario (INT8)
            blitzType,
            1200, // ELO inicial
            LocalDateTime.now());

    // Actualizar el ELO después de una partida
    int newElo = User.calculateNewElo(userElo.getCurrentElo(), 1400, true, 32);
    userElo.updateElo(newElo);

    // Assert
    Assertions.assertEquals(1224, newElo);
  }

  @Test
  public void testCalculateEloForExercise() {

    // Crear un tipo de ELO
    EloType blitzType = new EloType(UUID.randomUUID(), "blitz", "ELO para partidas Blitz");

    // Crear un ELO para un usuario
    UserElo userElo =
            new UserElo(
                    UUID.randomUUID(),
                    123456789L, // ID de usuario (INT8)
                    blitzType,
                    1200, // ELO inicial
                    LocalDateTime.now());

    int exerciseElo = 1400;  // Dificultad del ejercicio
    boolean solvedCorrectly = true;
    int timeTaken = 90;  // Tiempo que tardó el jugador (en segundos)
    int expectedTime = 120;  // Tiempo esperado para resolver el ejercicio
    int kFactor = 32;

    int newElo = User.calculateNewEloForExercise(
            userElo.getCurrentElo(), exerciseElo, solvedCorrectly, timeTaken, expectedTime, kFactor
    );

    Assertions.assertEquals(1232, newElo);
  }
}
