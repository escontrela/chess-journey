package com.davidp.chessjourney.application.usecases;


import com.davidp.chessjourney.domain.*;
import com.davidp.chessjourney.domain.common.*;
import com.davidp.chessjourney.domain.games.memory.MemoryGame;

import java.util.List;


public class GuessMemoryGameUseCaseImpl implements MemoryGameUseCase<MemoryGame<PiecePosition>> {

    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final DifficultyLevelRepository difficultyLevelRepository;
    private final ExerciseTypeRepository exerciseTypeRepository;

    public GuessMemoryGameUseCaseImpl(UserRepository userRepository,
                                 ExerciseRepository exerciseRepository,
                                 DifficultyLevelRepository difficultyLevelRepository,
                                      ExerciseTypeRepository exerciseTypeRepository) {

        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.difficultyLevelRepository = difficultyLevelRepository;
        this.exerciseTypeRepository = exerciseTypeRepository;
    }

    @Override
    public MemoryGame<PiecePosition> execute(long userId, String difficulty) {

        // TODO Recover from database 10 FEN positions of MemoryGame tagged as memory exercise
        User user = userRepository.getUserById(userId);
        Player player = new Player(user.getInitials(),userId);

        //TODO made a cache with difficulty levels
        DifficultyLevel level = difficultyLevelRepository.getByDifficulty(difficulty);
        ExerciseType type = exerciseTypeRepository.getByType("memory_game");

        List<Exercise> exercises =  exerciseRepository.getExercisesByDifficultyAndType(level.getId(),type.getId(),10);

        TimeControl timeControl = TimeControl.fivePlusThree();

        return  ChessGameFactory.createGuessMemoryGameFrom(player,timeControl,level,exercises);
    }
}