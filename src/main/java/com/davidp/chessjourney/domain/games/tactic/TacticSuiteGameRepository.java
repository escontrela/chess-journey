package com.davidp.chessjourney.domain.games.tactic;

import com.davidp.chessjourney.domain.common.Exercise;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for TacticSuiteGame operations.
 */
public interface TacticSuiteGameRepository {

    /**
     * Save a TacticSuiteGame and return its ID.
     */
    UUID save(TacticSuiteGame tacticSuiteGame);

    /**
     * Get a TacticSuiteGame by its ID.
     */
    TacticSuiteGame getById(UUID id);

    /**
     * Get all TacticSuiteGames.
     */
    List<TacticSuiteGame> getAll();

    /**
     * Get TacticSuiteGames by type.
     */
    List<TacticSuiteGame> getByType(TacticSuiteGame.Type type);

    /**
     * Get TacticSuiteGames associated with a specific user.
     */
    List<TacticSuiteGame> getByUserId(long userId);

    /**
     * Associate a user with a TacticSuiteGame.
     */
    void associateUser(UUID tacticSuiteGameId, long userId);

    /**
     * Remove user association with a TacticSuiteGame.
     */
    void removeUserAssociation(UUID tacticSuiteGameId, long userId);

    /**
     * Get exercises for a FIXED type TacticSuiteGame in the predefined sequence.
     */
    List<Exercise> getExercisesForSuite(UUID tacticSuiteGameId);

    /**
     * Add an exercise to a FIXED type TacticSuiteGame at a specific sequence position.
     */
    void addExerciseToSuite(UUID tacticSuiteGameId, UUID exerciseId, int sequenceOrder);

    /**
     * Remove an exercise from a FIXED type TacticSuiteGame.
     */
    void removeExerciseFromSuite(UUID tacticSuiteGameId, UUID exerciseId);
}