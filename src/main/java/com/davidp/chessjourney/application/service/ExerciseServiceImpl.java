package com.davidp.chessjourney.application.service;

import com.davidp.chessjourney.domain.common.DifficultyLevelRepository;
import com.davidp.chessjourney.domain.common.ExerciseTypeRepository;

import java.util.HashMap;
import java.util.UUID;

/**
 * This service provides methods to retrieve unique identifiers for different types of chess exercises.
 * Each method returns a UUID corresponding to a specific exercise type, such as memory games,
 * defend games, tactic games, and endgame exercises.
 */
public class ExerciseServiceImpl implements ExerciseService {

  protected static String MEMORY_GAME = "memory_game";
  protected static String DEFEND_MEMORY_GAME = "defend_memory_game";
  protected static String TACTIC_GAME = "tactic_game";
  protected static String ENDGAME = "endgame";
  protected static String EASY = "easy";
  protected static String MEDIUM = "medium";
  protected static String HARD = "hard";

  private final ExerciseTypeRepository exerciseTypeRepository;
  private final DifficultyLevelRepository difficultyLevelRepository;

  private final HashMap<String, UUID> cacheForExerciseType = new HashMap<>();
  private final HashMap<String, UUID> cacheForDifficultyLevel = new HashMap<>();

    public ExerciseServiceImpl(ExerciseTypeRepository exerciseTypeRepository,DifficultyLevelRepository difficultyLevelRepository) {

        this.exerciseTypeRepository = exerciseTypeRepository;
        this.difficultyLevelRepository = difficultyLevelRepository;
    }


    @Override
    public UUID getMemoryGameTypeId() {

        return cacheForExerciseType.computeIfAbsent(MEMORY_GAME, key -> {
            var type = exerciseTypeRepository.getByName(key);
            if (type == null) {
                throw new IllegalStateException("Can't find the exercise type name: " + key);
            }
            return type.getId();
        });
    }


    @Override
    public UUID getDefendGameTypeId() {

        return cacheForExerciseType.computeIfAbsent(DEFEND_MEMORY_GAME, key -> {
            var type = exerciseTypeRepository.getByName(key);
            if (type == null) {
                throw new IllegalStateException("Can't find the exercise type name: " + key);
            }
            return type.getId();
        });
    }

    @Override
    public UUID getTacticGameTypeId() {

        return cacheForExerciseType.computeIfAbsent(TACTIC_GAME, key -> {
            var type = exerciseTypeRepository.getByName(key);
            if (type == null) {
                throw new IllegalStateException("Can't find the exercise type name: " + key);
            }
            return type.getId();
        });
    }

    @Override
    public UUID getEndgameTypeId() {

        return cacheForExerciseType.computeIfAbsent(ENDGAME, key -> {
            var type = exerciseTypeRepository.getByName(key);
            if (type == null) {
                throw new IllegalStateException("Can't find the exercise type name: " + key);
            }
            return type.getId();
        });
    }

    @Override
    public UUID getEasyLevelId() {

        return cacheForDifficultyLevel.computeIfAbsent(EASY, key -> {
            var level = difficultyLevelRepository.getByDifficulty(key);
            if (level == null) {
                throw new IllegalStateException("Can't find the difficulty level name: " + key);
            }
            return level.getId();
        });
    }

    @Override
    public UUID getMediumLevelId() {

        return cacheForDifficultyLevel.computeIfAbsent(MEDIUM, key -> {
            var level = difficultyLevelRepository.getByDifficulty(key);
            if (level == null) {
                throw new IllegalStateException("Can't find the difficulty level name: " + key);
            }
            return level.getId();
        });
    }

    @Override
    public UUID getHardLevelId() {

        return cacheForDifficultyLevel.computeIfAbsent(HARD, key -> {
            var level = difficultyLevelRepository.getByDifficulty(key);
            if (level == null) {
                throw new IllegalStateException("Can't find the difficulty level name: " + key);
            }
            return level.getId();
        });
    }
}
