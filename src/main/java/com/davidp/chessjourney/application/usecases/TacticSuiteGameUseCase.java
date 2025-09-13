package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.games.tactic.TacticGame;
import com.davidp.chessjourney.domain.games.tactic.TacticSuiteGame;

import java.util.UUID;

/**
 * Use case for creating and managing tactic suite games.
 */
public interface TacticSuiteGameUseCase {

    TacticGame execute(long userId, UUID tacticSuiteGameId, String difficulty, int numberOfExercises);

    /**
     * Creates a tactic game from a random type suite with default number of exercises (10).
     * For random suites, exercises are generated on the fly.
     * 
     * @param userId The user ID
     * @param difficulty The difficulty level (e.g., "easy", "medium", "hard")
     * @return A configured TacticGame instance with random exercises
     */
    TacticGame executeRandom(long userId, String difficulty);

    /**
     * Creates a tactic game from a random type suite with specified number of exercises.
     * For random suites, exercises are generated on the fly.
     * 
     * @param userId The user ID
     * @param difficulty The difficulty level
     * @param numberOfExercises The number of exercises to include
     * @return A configured TacticGame instance with random exercises
     */
    TacticGame executeRandom(long userId, String difficulty, int numberOfExercises);

    /**
     * Creates a tactic game from a fixed type suite.
     * For fixed suites, exercises come from the predefined sequence in the database.
     * 
     * @param userId The user ID
     * @param tacticSuiteGameId The ID of the fixed TacticSuiteGame
     * @return A configured TacticGame instance with the suite's predefined exercises
     */
    TacticGame executeFixed(long userId, java.util.UUID tacticSuiteGameId);

    /**
     * Creates a new TacticSuiteGame.
     * 
     * @param name The name of the suite
     * @param type The type of the suite (RANDOM or FIXED)
     * @return The created TacticSuiteGame
     */
    TacticSuiteGame createSuite(String name, TacticSuiteGame.Type type);

    /**
     * Get a TacticSuiteGame by ID.
     * 
     * @param suiteId The ID of the suite
     * @return The TacticSuiteGame
     */
    TacticSuiteGame getSuite(java.util.UUID suiteId);

    /**
     * Associate a user with a TacticSuiteGame.
     * 
     * @param suiteId The suite ID
     * @param userId The user ID
     */
    void associateUserWithSuite(java.util.UUID suiteId, long userId);
}