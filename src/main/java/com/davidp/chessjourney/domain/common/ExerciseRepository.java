package com.davidp.chessjourney.domain.common;

import java.util.List;
import java.util.UUID;

public interface ExerciseRepository {

    UUID save(Exercise exercise);
    Exercise getById(UUID id);
    List<Exercise> getAll();
    void assignTag(UUID exerciseId, UUID tagId);
    List<Exercise> getExercisesByDifficulty(UUID difficultyId, int limit);

}