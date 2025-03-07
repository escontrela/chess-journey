package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.UserRepository;
import com.davidp.chessjourney.domain.common.UserExerciseStats;

public class SaveUserExerciseStatsUseCaseImpl implements SaveUserExerciseStatsUseCase {

  private final UserRepository userRepository;

  public SaveUserExerciseStatsUseCaseImpl(UserRepository userRepository) {

    this.userRepository = userRepository;
  }

  @Override
  public boolean execute(UserExerciseStats userExerciseStats) {

    //TODO make the validations that are needed
    System.out.println("Saving user exercise stats:" + userExerciseStats.toString());
    return userRepository.insertExerciseStats(userExerciseStats);
  }
}
