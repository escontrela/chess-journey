package com.davidp.chessjourney.domain.services;

import com.davidp.chessjourney.domain.common.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A FEN record contains six fields, each separated by a space. The fields are as follows:[5]
 *
 * <p>Piece placement data: Each rank is described, starting with rank 8 and ending with rank 1,
 * with a "/" between each one; within each rank, the contents of the squares are described in order
 * from the a-file to the h-file. Each piece is identified by a single letter taken from the
 * standard English names in algebraic notation (pawn = "P", knight = "N", bishop = "B", rook = "R",
 * queen = "Q" and king = "K"). White pieces are designated using uppercase letters ("PNBRQK"),
 * while black pieces use lowercase letters ("pnbrqk"). A set of one or more consecutive empty
 * squares within a rank is denoted by a digit from "1" to "8", corresponding to the number of
 * squares. Active color: "w" means that White is to move; "b" means that Black is to move. Castling
 * availability: If neither side has the ability to castle, this field uses the character "-".
 * Otherwise, this field contains one or more letters: "K" if White can castle kingside, "Q" if
 * White can castle queenside, "k" if Black can castle kingside, and "q" if Black can castle
 * queenside. A situation that temporarily prevents castling does not prevent the use of this
 * notation. En passant target square: This is a square over which a pawn has just passed while
 * moving two squares; it is given in algebraic notation. If there is no en passant target square,
 * this field uses the character "-". This is recorded regardless of whether there is a pawn in
 * position to capture en passant.[6] An updated version of the spec has since made it so the target
 * square is recorded only if a legal en passant capture is possible, but the old version of the
 * standard is the one most commonly used.[7][8] Halfmove clock: The number of halfmoves since the
 * last capture or pawn advance, used for the fifty-move rule.[9] Fullmove number: The number of the
 * full moves. It starts at 1 and is incremented after Black's move.
 *
 * <p><a href="http://www.ee.unb.ca/cgi-bin/tervo/fen.pl">...</a> <a
 * href="https://en.wikipedia.org/wiki/Forsyth">...</a>–Edwards_Notation
 */
public class FenServiceImpl implements FenService {

  public static final String KING_CHAR_WHITE = "K";
  public static final String QUEEN_CHAR_WHITE = "Q";
  public static final String KING_CHAR_BLACK = "k";
  public static final String QUEEN_CHAR_BLACK = "q";
  public static final String WHITE_CHAR = "w";
  public static final String NOTHING_CHAR = "-";
  public static final char KING_CHAR = 'k';
  public static final char QUEEN_CHAR = 'q';
  public static final char ROOK_CHAR = 'r';
  public static final char BISHOP_CHAR = 'b';
  public static final char KNIGHT_CHAR = 'n';
  public static final char PAWN_CHAR = 'p';

  protected static final FenServiceImpl INSTANCE = new FenServiceImpl();

  private static class Holder {

    private static final FenServiceImpl INSTANCE = new FenServiceImpl();
  }

  public static FenServiceImpl getInstance() {

    return Holder.INSTANCE;
  }

  private FenServiceImpl() {}

  public GameState parseString(Fen fen) {

    final List<PiecePosition> pieces;
    final PieceColor activeColor;
    final CastlingAvailability castlingAvailability;
    final EnPassantTargetSquare enPassantTargetSquare;
    final int halfMoveClock;
    final int fullMoveNumber;

    final String[] parts = fen.getStringValue().split(" ");
    pieces = parsePieces(parts[0]);
    activeColor = parseActiveColor(parts[1]);
    castlingAvailability = parseCastlingAvailability(parts[2]);
    enPassantTargetSquare = parseEnPassantTargetSquare(parts[3]);
    halfMoveClock = Integer.parseInt(parts[4]);
    fullMoveNumber = Integer.parseInt(parts[5]);

    return new GameState(
        pieces,
        activeColor,
        castlingAvailability,
        enPassantTargetSquare,
        halfMoveClock,
        fullMoveNumber);
  }

  @Override
  public Fen parseActualStatus(GameState gameState) {

    StringBuilder fenBuilder = new StringBuilder();

    PiecePosition[][] board = new PiecePosition[8][8];
    for (PiecePosition piecePosition : gameState.getPieces()) {

      Pos pos = piecePosition.getPosition();
      board[pos.getRow().ordinal()][pos.getCol().ordinal()] = piecePosition;
    }

    for (int row = 7; row >= 0; row--) {
      int emptyCount = 0;
      for (int col = 0; col < 8; col++) {
        if (board[row][col] == null) {
          emptyCount++;
        } else {
          if (emptyCount > 0) {
            fenBuilder.append(emptyCount);
            emptyCount = 0;
          }
          Piece piece = board[row][col].getPiece();
          fenBuilder.append(
              piece.getColor() == PieceColor.WHITE
                  ? String.valueOf(pieceTypeToChar(piece.getType())).toUpperCase()
                  : String.valueOf(pieceTypeToChar(piece.getType())).toLowerCase());
        }
      }
      if (emptyCount > 0) {
        fenBuilder.append(emptyCount);
      }
      if (row > 0) {
        fenBuilder.append('/');
      }
    }

    // Add active color
    fenBuilder.append(' ').append(gameState.getActiveColor() == PieceColor.WHITE ? 'w' : 'b');

    // Add castling availability
    fenBuilder.append(' ').append(gameState.getCastlingAvailability().toString());

    // Add en passant target square
    fenBuilder
        .append(' ')
        .append(
            gameState.getEnPassantTargetSquare().isAvailable()
                ? gameState.getEnPassantTargetSquare().getPosition().toString().toLowerCase()
                : "-");

    // Add count of half moves since last capture or pawn advance
    fenBuilder.append(' ').append(gameState.getHalfMoveClock());

    // Add count of full moves
    fenBuilder.append(' ').append(gameState.getFullMoveNumber());

    return new Fen(fenBuilder.toString());
  }

  private List<PiecePosition> parsePieces(final String positionPart) {

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
          pieces.add(
              new PiecePosition(
                  new Piece(type, color), new Pos(Row.values()[7 - row], Col.values()[col])));
          col++;
        }
      }
    }
    return pieces;
  }

  private PieceColor parseActiveColor(String activeColorPart) {

    return WHITE_CHAR.equals(activeColorPart) ? PieceColor.WHITE : PieceColor.BLACK;
  }

  private CastlingAvailability parseCastlingAvailability(String castlingPart) {

    boolean whiteKingSide = castlingPart.contains(KING_CHAR_WHITE);
    boolean whiteQueenSide = castlingPart.contains(QUEEN_CHAR_WHITE);
    boolean blackKingSide = castlingPart.contains(KING_CHAR_BLACK);
    boolean blackQueenSide = castlingPart.contains(QUEEN_CHAR_BLACK);
    return new CastlingAvailability(whiteKingSide, whiteQueenSide, blackKingSide, blackQueenSide);
  }

  private EnPassantTargetSquare parseEnPassantTargetSquare(String enPassantPart) {

    if (NOTHING_CHAR.equals(enPassantPart)) {

      return new EnPassantTargetSquare(null, false);
    } else {

      String colChar = String.valueOf(enPassantPart.charAt(0));
      String rowChar = String.valueOf(enPassantPart.charAt(1));
      Col col = Col.valueOf(colChar.toUpperCase());
      Row row = Row.fromValue(Integer.parseInt(rowChar));
      Pos pos = Pos.of(row, col);
      return new EnPassantTargetSquare(pos, true);
    }
  }

  private PieceType charToPieceType(final char c) {

    switch (Character.toLowerCase(c)) {
      case KING_CHAR:
        return PieceType.KING;
      case QUEEN_CHAR:
        return PieceType.QUEEN;
      case ROOK_CHAR:
        return PieceType.ROOK;
      case BISHOP_CHAR:
        return PieceType.BISHOP;
      case KNIGHT_CHAR:
        return PieceType.KNIGHT;
      case PAWN_CHAR:
        return PieceType.PAWN;
      default:
        throw new IllegalArgumentException("Invalid FEN character: " + c);
    }
  }

  private char pieceTypeToChar(final PieceType pieceType) {

    switch (pieceType) {
      case KING:
        return KING_CHAR;
      case QUEEN:
        return QUEEN_CHAR;
      case ROOK:
        return ROOK_CHAR;
      case BISHOP:
        return BISHOP_CHAR;
      case KNIGHT:
        return KNIGHT_CHAR;
      case PAWN:
        return PAWN_CHAR;
      default:
        throw new IllegalArgumentException("Invalid PieceType: " + pieceType);
    }
  }
}