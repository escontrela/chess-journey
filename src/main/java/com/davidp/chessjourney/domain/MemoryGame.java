package com.davidp.chessjourney.domain;

import com.davidp.chessjourney.domain.common.Fen;
import com.davidp.chessjourney.domain.common.TimeControl;

import java.util.List;

/** clase base para un juego de ajedrez de memoria. */
public class MemoryGame extends Game {

  private Player player;
  private TimeControl timeControl;
  private List<Fen> positions;


  public MemoryGame(
          Player player,
          ChessBoard board,
          TimeControl timeControl,
          List<Fen> positions) {

    super();

    this.player = player;
    this.chessBoard = board;
    this.timeControl = timeControl;
    this.positions = positions;
  }

  @Override
  public Fen getFen() {

    return chessBoard.getFen();
  }

  // Additional methods specific to a memory training game
}
