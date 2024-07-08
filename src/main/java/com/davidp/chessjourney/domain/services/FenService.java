package com.davidp.chessjourney.domain.services;

import com.davidp.chessjourney.domain.common.*;

/** This class is responsible for creating a Fen object from a string or from a game status. */
public interface FenService {

  /**
   * Creates a Fen object from a string.
   *
   * @param fen
   * @return
   */
  GameState parseString(Fen fen);

  /**
   * Creates a Fen object from a game status.
   *
   * @param fenParserResponse
   * @return
   */
  Fen parseActualStatus(GameState fenParserResponse);
}
