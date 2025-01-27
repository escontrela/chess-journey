package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.ChessGame;

public interface MemoryGameUseCase {

  ChessGame execute(long userId);
}
