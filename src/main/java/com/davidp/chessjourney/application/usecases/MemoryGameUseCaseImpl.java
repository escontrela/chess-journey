package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.*;
import com.davidp.chessjourney.domain.common.*;
import com.davidp.chessjourney.domain.games.memory.MemoryGame;

import java.util.ArrayList;
import java.util.List;

/** Implementación concreta del caso de uso para guardar (insertar/actualizar) un usuario. */
public class MemoryGameUseCaseImpl implements MemoryGameUseCase {

  private final UserRepository userRepository;
  private final ExerciseRepository exerciseRepository;
  private final DifficultyLevelRepository difficultyLevelRepository;

  public MemoryGameUseCaseImpl(UserRepository userRepository,
                               ExerciseRepository exerciseRepository,
                               DifficultyLevelRepository difficultyLevelRepository) {

    this.userRepository = userRepository;
    this.exerciseRepository = exerciseRepository;
    this.difficultyLevelRepository = difficultyLevelRepository;
  }

  @Override
  public MemoryGame execute(long userId,String difficulty) {

    // TODO Recover from database 10 FEN positions of MemoryGame tagged as memory exercise
    User user = userRepository.getUserById(userId);
    Player player = new Player(user.getInitials(),userId);

    //TODO made a cache with difficulty levels
    DifficultyLevel level = difficultyLevelRepository.getByDifficulty(difficulty);

    List<Exercise> exercises =  exerciseRepository.getExercisesByDifficulty(level.getId(),10);

    TimeControl timeControl = TimeControl.fivePlusThree();

    return  ChessGameFactory.createMemoryGameFrom(player,timeControl,level,exercises);
  }


  private List<Fen> getPositionsForMemoryGame(long userId, String difficulty){

    List<Fen> positions = List.of(
            Fen.createCustom("r3n1k1/pp1bR1q1/5pNp/3p1P1Q/P2B4/2PP4/1P4PP/6K1 b - - 5 26"),
            Fen.createCustom("r1bqk2r/ppp2pp1/7p/2bnN3/2Bn4/3PB3/PPP2PPP/RN1Q1RK1 w kq - 0 9"),
            Fen.createCustom("3k4/4r3/1n4p1/8/4PP2/2Q4P/5R2/1K6 w - - 0 1"),
            Fen.createCustom("1k1r4/7p/3b4/8/3P4/8/6B1/1K1R4 w - - 0 1"),
            Fen.createCustom("6k1/ppp5/6P1/5PK1/1P6/2r5/8/5q2 w - - 2 42")
            );
    return positions;
  }

}