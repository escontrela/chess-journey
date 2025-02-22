package com.davidp.chessjourney.domain.common;


import java.util.List;
import java.util.UUID;

public interface DifficultyLevelRepository {

    List<DifficultyLevel> getAll();
    DifficultyLevel getById(UUID id);
}
