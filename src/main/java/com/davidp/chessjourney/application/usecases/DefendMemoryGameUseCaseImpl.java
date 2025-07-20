package com.davidp.chessjourney.application.usecases;


import com.davidp.chessjourney.domain.ChessGameFactory;
import com.davidp.chessjourney.domain.Player;
import com.davidp.chessjourney.domain.User;
import com.davidp.chessjourney.domain.UserRepository;
import com.davidp.chessjourney.domain.common.*;
import com.davidp.chessjourney.domain.games.memory.MemoryGame;

import java.util.List;

public class DefendMemoryGameUseCaseImpl implements  MemoryGameUseCase<MemoryGame<String>>  {

    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final DifficultyLevelRepository difficultyLevelRepository;
    private final ExerciseTypeRepository exerciseTypeRepository;

    public DefendMemoryGameUseCaseImpl(UserRepository userRepository,
                                      ExerciseRepository exerciseRepository,
                                      DifficultyLevelRepository difficultyLevelRepository,
                                        ExerciseTypeRepository exerciseTypeRepository) {

        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.difficultyLevelRepository = difficultyLevelRepository;
        this.exerciseTypeRepository = exerciseTypeRepository;
    }

    @Override
    public MemoryGame<String> execute(long userId, String difficulty) {

        User user = userRepository.getUserById(userId);
        Player player = new Player(user.getInitials(),userId);

        DifficultyLevel level = difficultyLevelRepository.getByDifficulty(difficulty);
        ExerciseType type = exerciseTypeRepository.getByType("defend_memory_game");
        //TODO parameterize the number of exercises (limit)
        List<Exercise> exercises =  exerciseRepository.getExercisesByDifficultyAndType(level.getId(),type.getId(),10);

        TimeControl timeControl = TimeControl.fivePlusThree();

        return  ChessGameFactory.createDefendMemoryGameFrom(player,timeControl,level,exercises);
    }


}
