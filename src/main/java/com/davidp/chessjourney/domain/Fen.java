package com.davidp.chessjourney.domain;

import java.util.ArrayList;
import java.util.List;

public class Fen {

  private String fen;

  public Fen(String fen) {
    this.fen = fen;
  }

  public List<Piece> getPieces() {
    List<Piece> pieces = new ArrayList<>();
    String[] rows = fen.split(" ")[0].split("/");

    for (int row = 0; row < rows.length; row++) {
      int col = 0;
      for (char c : rows[row].toCharArray()) {
        if (Character.isDigit(c)) {
          col += Character.getNumericValue(c);
        } else {
          PieceType type = charToPieceType(c);
          PieceColor color = Character.isUpperCase(c) ? PieceColor.WHITE : PieceColor.BLACK;
          pieces.add(new Piece(type, color, Row.values()[7 - row], Col.values()[col]));
          col++;
        }
      }
    }

    return pieces;
  }

  private PieceType charToPieceType(char c) {
    switch (Character.toLowerCase(c)) {
      case 'k':
        return PieceType.KING;
      case 'q':
        return PieceType.QUEEN;
      case 'r':
        return PieceType.ROOK;
      case 'b':
        return PieceType.BISHOP;
      case 'n':
        return PieceType.KNIGHT;
      case 'p':
        return PieceType.PAWN;
      default:
        throw new IllegalArgumentException("Invalid FEN character: " + c);
    }
  }
}
