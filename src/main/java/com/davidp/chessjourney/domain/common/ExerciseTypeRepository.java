package com.davidp.chessjourney.domain.common;

import java.util.List;
import java.util.UUID;

public interface ExerciseTypeRepository {

    List<ExerciseType> getAll();
    ExerciseType getById(UUID id);
    ExerciseType getByType(String typeName);
}
