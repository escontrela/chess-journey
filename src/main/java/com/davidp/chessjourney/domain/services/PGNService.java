package com.davidp.chessjourney.domain.services;

import com.davidp.chessjourney.domain.ChessBoard;
import com.davidp.chessjourney.domain.ChessRules;
import com.davidp.chessjourney.domain.common.*;
import java.util.List;

/**
 * This class is responsible for managing the PGN format.
 *
 * <p><a href="https://en.wikipedia.org/wiki/Portable_Game_Notation">...</a>
 */
public interface PGNService {

  /**
   * Converts a move in algebraic coordinates (e.g., e2, e4) into standard algebraic notation (e.g.,
   * e4, Nf3).
   *
   * @param from The starting position of the piece.
   * @param to The destination position of the piece.
   * @param board The current state of the chessboard.
   * @param chessRules The current state of the chessboard.
   * @return The move in standard algebraic notation.
   */
  String toAlgebraic(Pos from, Pos to, ChessBoard board, ChessRules chessRules);

  /**
   * Converts a move from standard algebraic notation (e.g., e4, Nf3) into algebraic coordinates
   * (e.g., e2, e4).
   *
   * @param move The move in standard algebraic notation.
   * @param board The current state of the chessboard.
   * @return A Move object representing the move in algebraic coordinates.
   */
  List<Pos> fromAlgebraic(String move, ChessBoard board);
}
