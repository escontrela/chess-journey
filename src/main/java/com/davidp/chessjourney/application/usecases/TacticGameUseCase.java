package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.games.tactic.TacticGame2;

/**
 * Use case for creating and managing tactic games.
 */
public interface TacticGameUseCase {

    /**
     * Creates a tactic game with default number of exercises (10).
     * @param userId The user ID
     * @param difficulty The difficulty level (e.g., "easy", "medium", "hard")
     * @return A configured TacticGame instance
     */
    TacticGame2 execute(long userId, String difficulty);

    /**
     * Creates a tactic game with specified number of exercises.
     * @param userId The user ID
     * @param difficulty The difficulty level
     * @param numberOfExercises The number of exercises to include
     * @return A configured TacticGame instance
     */
    TacticGame2 execute(long userId, String difficulty, int numberOfExercises);
}
