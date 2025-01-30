package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.ChessGame;
import com.davidp.chessjourney.domain.MemoryGame;

public interface MemoryGameUseCase {

  MemoryGame execute(long userId);
}
