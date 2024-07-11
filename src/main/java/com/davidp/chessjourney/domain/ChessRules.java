package com.davidp.chessjourney.domain;

import com.davidp.chessjourney.domain.common.Pos;
import java.util.List;

/*
 * This class is responsible for implementing the rules of chess.
 */
public class ChessRules {
  // Implement using chesstempo library (assumed)

  public List<Pos> getAttackedPositions(Pos position) {
    // Dummy implementation
    return List.of();
  }

  public boolean isValidMove(Pos from, Pos to) {
    // Dummy implementation
    return true;
  }
}
