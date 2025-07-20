package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.games.memory.MemoryGame;


public interface MemoryGameUseCase<T extends MemoryGame<?>> {

  T execute(long userId, String difficulty);
}