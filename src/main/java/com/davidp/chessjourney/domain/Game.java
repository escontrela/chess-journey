package com.davidp.chessjourney.domain;

import com.davidp.chessjourney.domain.common.Fen;

/*

Clase base para un juego de ajedrez. Contiene un tablero de ajedrez (ChessBoard) y reglas de ajedrez (ChessRules). Proporciona métodos para obtener la notación FEN y la historia del juego.
 */
public class Game {

  protected ChessBoard chessBoard;
  protected ChessRules chessRules;

  public Game() {
    this.chessBoard = new ChessBoard();
    this.chessRules = new ChessRules();
  }

  public Fen getFen() {
    // Dummy implementation to return the FEN representation of the game state
    return new Fen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
  }

  public String getHistory() {
    // Dummy implementation to return the game history in PGN format
    return "1. e4 e5 2. Nf3 Nc6";
  }
}
