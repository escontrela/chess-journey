package com.davidp.chessjourney.domain.common;

import java.util.ArrayList;
import java.util.List;

/**
 * A FEN record contains six fields, each separated by a space. The fields are as follows:[5]
 *
 * Piece placement data: Each rank is described, starting with rank 8 and ending with rank 1, with a "/" between each one; within each rank, the contents of the squares are described in order from the a-file to the h-file. Each piece is identified by a single letter taken from the standard English names in algebraic notation (pawn = "P", knight = "N", bishop = "B", rook = "R", queen = "Q" and king = "K"). White pieces are designated using uppercase letters ("PNBRQK"), while black pieces use lowercase letters ("pnbrqk"). A set of one or more consecutive empty squares within a rank is denoted by a digit from "1" to "8", corresponding to the number of squares.
 * Active color: "w" means that White is to move; "b" means that Black is to move.
 * Castling availability: If neither side has the ability to castle, this field uses the character "-". Otherwise, this field contains one or more letters: "K" if White can castle kingside, "Q" if White can castle queenside, "k" if Black can castle kingside, and "q" if Black can castle queenside. A situation that temporarily prevents castling does not prevent the use of this notation.
 * En passant target square: This is a square over which a pawn has just passed while moving two squares; it is given in algebraic notation. If there is no en passant target square, this field uses the character "-". This is recorded regardless of whether there is a pawn in position to capture en passant.[6] An updated version of the spec has since made it so the target square is recorded only if a legal en passant capture is possible, but the old version of the standard is the one most commonly used.[7][8]
 * Halfmove clock: The number of halfmoves since the last capture or pawn advance, used for the fifty-move rule.[9]
 * Fullmove number: The number of the full moves. It starts at 1 and is incremented after Black's move.
 *
 * http://www.ee.unb.ca/cgi-bin/tervo/fen.pl
 * https://en.wikipedia.org/wiki/Forsyth–Edwards_Notation
 *
 */
public class Fen {

  private final String fen;
  private final List<PiecePosition> pieces;
  private final PieceColor activeColor;
  private final CastlingAvailability castlingAvailability;
  private final EnPassantTargetSquare enPassantTargetSquare;
  private final int halfmoveClock;
  private final int fullmoveNumber;

  public Fen(String fen) {
    this.fen = fen;
    String[] parts = fen.split(" ");
    this.pieces = parsePieces(parts[0]);
    this.activeColor = parseActiveColor(parts[1]);
    this.castlingAvailability = parseCastlingAvailability(parts[2]);
    this.enPassantTargetSquare = parseEnPassantTargetSquare(parts[3]);
    this.halfmoveClock = Integer.parseInt(parts[4]);
    this.fullmoveNumber = Integer.parseInt(parts[5]);
  }

  private List<PiecePosition> parsePieces(String positionPart) {
    List<PiecePosition> pieces = new ArrayList<>();
    String[] rows = positionPart.split("/");

    for (int row = 0; row < rows.length; row++) {
      int col = 0;
      for (char c : rows[row].toCharArray()) {
        if (Character.isDigit(c)) {
          col += Character.getNumericValue(c);
        } else {
          PieceType type = charToPieceType(c);
          PieceColor color = Character.isUpperCase(c) ? PieceColor.WHITE : PieceColor.BLACK;
          pieces.add(new PiecePosition(new Piece(type, color), new Pos(Row.values()[7 - row], Col.values()[col])));
          col++;
        }
      }
    }
    return pieces;
  }

  private PieceColor parseActiveColor(String activeColorPart) {
    return "w".equals(activeColorPart) ? PieceColor.WHITE : PieceColor.BLACK;
  }

  private CastlingAvailability parseCastlingAvailability(String castlingPart) {
    boolean whiteKingside = castlingPart.contains("K");
    boolean whiteQueenside = castlingPart.contains("Q");
    boolean blackKingside = castlingPart.contains("k");
    boolean blackQueenside = castlingPart.contains("q");
    return new CastlingAvailability(whiteKingside, whiteQueenside, blackKingside, blackQueenside);
  }

  private EnPassantTargetSquare parseEnPassantTargetSquare(String enPassantPart) {
    if ("-".equals(enPassantPart)) {
      return new EnPassantTargetSquare(null, false);
    } else {
      String colChar = String.valueOf(enPassantPart.charAt(0));
      String rowChar = String.valueOf(enPassantPart.charAt(1));
      Col col = Col.valueOf(colChar.toUpperCase());
      Row row = Row.fromValue(Integer.parseInt(rowChar));
      Pos pos = new Pos(row, col);
      return new EnPassantTargetSquare(pos, true);
    }
  }

  private PieceType charToPieceType(char c) {
    switch (Character.toLowerCase(c)) {
      case 'k': return PieceType.KING;
      case 'q': return PieceType.QUEEN;
      case 'r': return PieceType.ROOK;
      case 'b': return PieceType.BISHOP;
      case 'n': return PieceType.KNIGHT;
      case 'p': return PieceType.PAWN;
      default: throw new IllegalArgumentException("Invalid FEN character: " + c);
    }
  }

  public List<PiecePosition> getPieces() {
    return pieces;
  }

  public PieceColor getActiveColor() {
    return activeColor;
  }

  public CastlingAvailability getCastlingAvailability() {
    return castlingAvailability;
  }

  public EnPassantTargetSquare getEnPassantTargetSquare() {
    return enPassantTargetSquare;
  }

  public int getHalfmoveClock() {
    return halfmoveClock;
  }

  public int getFullmoveNumber() {
    return fullmoveNumber;
  }

  public static class PiecePosition {
    private final Piece piece;
    private final Pos position;

    public PiecePosition(Piece piece, Pos position) {
      this.piece = piece;
      this.position = position;
    }

    public Piece getPiece() {
      return piece;
    }

    public Pos getPosition() {
      return position;
    }
  }
}