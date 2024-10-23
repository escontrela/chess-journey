package com.davidp.chessjourney.domain;

import chesspresso.Chess;
import chesspresso.move.Move;
import chesspresso.position.Position;
import com.davidp.chessjourney.domain.common.Fen;
import com.davidp.chessjourney.domain.common.IllegalMoveException;
import com.davidp.chessjourney.domain.common.Pos;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This class is responsible for implementing the rules of chess.
 */
public class ChessRules {
  // Implement using chess-tempo library (assumed)

  private final Logger logger = LoggerFactory.getLogger(ChessRules.class);

  public List<Pos> getAttackedPositions(Pos position) {
    // Dummy implementation
    return List.of();
  }

  public boolean isValidMove(Pos from, Pos to, Fen actualFen) {

    Position position = new Position(actualFen.getStringValue());

    try {

      // Convert Pos to ChessTempo format
      int fromIndex = Chess.strToSqi(from.toString().toLowerCase());
      int toIndex = Chess.strToSqi(to.toString().toLowerCase());
      boolean isCapture = position.getStone(toIndex) != Chess.NO_STONE;

      short move = Move.getRegularMove(fromIndex, toIndex, isCapture);
      // position.doMove(move);

      return isLegalMove(position, move);

    } catch (Exception e) {

      logger.error("Error trying to validate move from {} to {}", from, to, e);
      throw new IllegalMoveException(e);
    }
  }

  private static boolean isLegalMove(Position position, short move) {
    short[] legalMoves = position.getAllMoves();
    // TODO improve this code with a hashmap!
    for (short legalMove : legalMoves) {
      if (move == legalMove) {
        return true;
      }
    }
    return false;
  }
}
