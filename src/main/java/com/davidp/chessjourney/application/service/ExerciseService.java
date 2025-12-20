package com.davidp.chessjourney.application.service;

import java.util.UUID;

/**
 * This service provides methods to retrieve unique identifiers for different types of chess exercises.
 * Each method returns a UUID corresponding to a specific exercise type, such as memory games,
 * defend games, tactic games, and endgame exercises.
 */
public interface ExerciseService {

    UUID getMemoryGameTypeId();
    UUID getDefendGameTypeId();
    UUID getTacticGameTypeId();
    UUID getEndgameTypeId();

    UUID getEasyLevelId();
    UUID getMediumLevelId();
    UUID getHardLevelId();
}
