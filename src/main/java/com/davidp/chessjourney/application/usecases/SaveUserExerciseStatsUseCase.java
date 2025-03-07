package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.common.UserExerciseStats;

public interface SaveUserExerciseStatsUseCase {

    boolean execute(UserExerciseStats userExerciseStats);
}
