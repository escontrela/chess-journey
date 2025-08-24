package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.Player;
import com.davidp.chessjourney.domain.User;
import com.davidp.chessjourney.domain.UserRepository;
import com.davidp.chessjourney.domain.common.*;
import com.davidp.chessjourney.domain.games.tactic.TacticGame;
import com.davidp.chessjourney.domain.games.tactic.TacticSuiteGame;
import com.davidp.chessjourney.domain.games.tactic.TacticSuiteGameRepository;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of TacticSuiteGameUseCase.
 */
public class TacticSuiteGameUseCaseImpl implements TacticSuiteGameUseCase {

    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final DifficultyLevelRepository difficultyLevelRepository;
    private final ExerciseTypeRepository exerciseTypeRepository;
    private final TacticSuiteGameRepository tacticSuiteGameRepository;

    public TacticSuiteGameUseCaseImpl(UserRepository userRepository,
                                      ExerciseRepository exerciseRepository,
                                      DifficultyLevelRepository difficultyLevelRepository,
                                      ExerciseTypeRepository exerciseTypeRepository,
                                      TacticSuiteGameRepository tacticSuiteGameRepository) {
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.difficultyLevelRepository = difficultyLevelRepository;
        this.exerciseTypeRepository = exerciseTypeRepository;
        this.tacticSuiteGameRepository = tacticSuiteGameRepository;
    }

    @Override
    public TacticGame executeRandom(long userId, String difficulty) {
        return executeRandom(userId, difficulty, 10); // Default 10 exercises
    }

    @Override
    public TacticGame executeRandom(long userId, String difficulty, int numberOfExercises) {
        // For RANDOM type, we generate exercises on the fly, just like the original TacticGameUseCase
        User user = userRepository.getUserById(userId);
        Player player = new Player(user.getInitials(), userId);

        DifficultyLevel level = difficultyLevelRepository.getByDifficulty(difficulty);
        ExerciseType type = exerciseTypeRepository.getByType("tactic_game");

        List<Exercise> exercises = exerciseRepository.getExercisesByDifficultyAndType(
            level.getId(), type.getId(), numberOfExercises);

        TimeControl timeControl = TimeControl.fivePlusThree();

        return new TacticGame(player, timeControl, level, exercises);
    }

    @Override
    public TacticGame executeFixed(long userId, UUID tacticSuiteGameId) {
        // For FIXED type, we get exercises from the predefined sequence in the database
        User user = userRepository.getUserById(userId);
        Player player = new Player(user.getInitials(), userId);

        TacticSuiteGame suite = tacticSuiteGameRepository.getById(tacticSuiteGameId);
        if (suite == null) {
            throw new IllegalArgumentException("TacticSuiteGame not found with ID: " + tacticSuiteGameId);
        }

        if (suite.getType() != TacticSuiteGame.Type.FIXED) {
            throw new IllegalArgumentException("TacticSuiteGame must be of type FIXED for executeFixed method");
        }

        List<Exercise> exercises = tacticSuiteGameRepository.getExercisesForSuite(tacticSuiteGameId);
        if (exercises.isEmpty()) {
            throw new IllegalStateException("No exercises found for TacticSuiteGame: " + suite.getName());
        }

        // Use the difficulty level from the first exercise
        DifficultyLevel level = exercises.getFirst().getDifficultyLevel();
        TimeControl timeControl = TimeControl.fivePlusThree();

        return new TacticGame(player, timeControl, level, exercises);
    }

    @Override
    public TacticSuiteGame createSuite(String name, TacticSuiteGame.Type type) {
        UUID id = UUID.randomUUID();
        TacticSuiteGame suite = new TacticSuiteGame(id, name, type);
        tacticSuiteGameRepository.save(suite);
        return suite;
    }

    @Override
    public TacticSuiteGame getSuite(UUID suiteId) {
        TacticSuiteGame suite = tacticSuiteGameRepository.getById(suiteId);
        if (suite == null) {
            throw new IllegalArgumentException("TacticSuiteGame not found with ID: " + suiteId);
        }
        return suite;
    }

    @Override
    public void associateUserWithSuite(UUID suiteId, long userId) {
        // Verify that both the suite and user exist
        TacticSuiteGame suite = tacticSuiteGameRepository.getById(suiteId);
        if (suite == null) {
            throw new IllegalArgumentException("TacticSuiteGame not found with ID: " + suiteId);
        }

        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        tacticSuiteGameRepository.associateUser(suiteId, userId);
    }
}