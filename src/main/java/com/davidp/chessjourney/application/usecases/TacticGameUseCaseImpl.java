package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.Player;
import com.davidp.chessjourney.domain.User;
import com.davidp.chessjourney.domain.UserRepository;
import com.davidp.chessjourney.domain.common.*;
import com.davidp.chessjourney.domain.games.tactic.TacticGame;

import java.util.List;

public class TacticGameUseCaseImpl implements TacticGameUseCase {

    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final DifficultyLevelRepository difficultyLevelRepository;
    private final ExerciseTypeRepository exerciseTypeRepository;

    public TacticGameUseCaseImpl(UserRepository userRepository,
                                ExerciseRepository exerciseRepository,
                                DifficultyLevelRepository difficultyLevelRepository,
                                ExerciseTypeRepository exerciseTypeRepository) {

        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.difficultyLevelRepository = difficultyLevelRepository;
        this.exerciseTypeRepository = exerciseTypeRepository;
    }

    @Override
    public TacticGame execute(long userId, String difficulty) {
        return execute(userId, difficulty, 10); // Default 10 exercises
    }

    @Override
    public TacticGame execute(long userId, String difficulty, int numberOfExercises) {

        User user = userRepository.getUserById(userId);
        Player player = new Player(user.getInitials(), userId);

        DifficultyLevel level = difficultyLevelRepository.getByDifficulty(difficulty);
        ExerciseType type = exerciseTypeRepository.getByType("tactic_game");

        List<Exercise> exercises = exerciseRepository.getExercisesByDifficultyAndType(
            level.getId(), type.getId(), numberOfExercises);

        TimeControl timeControl = TimeControl.fivePlusThree();

        return new TacticGame(player, timeControl, level, exercises);
    }
}
