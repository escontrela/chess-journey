package com.davidp.chessjourney.domain.services;

import com.davidp.chessjourney.domain.ChessBoard;
import com.davidp.chessjourney.domain.common.*;
import java.util.List;
import java.util.Optional;

/**
 * This class is responsible for implements the service for managing the PGN format.
 *
 * <p><a href="https://en.wikipedia.org/wiki/Portable_Game_Notation">...</a>
 */
public class PGNServiceImpl implements PGNService {

  private static class Holder {

    private static final PGNServiceImpl INSTANCE = new PGNServiceImpl();
  }

  public static PGNServiceImpl getInstance() {

    return PGNServiceImpl.Holder.INSTANCE;
  }

  /* Example: c2 e5 : Ne5  */
  @Override
  public String toAlgebraic(Pos from, Pos to, ChessBoard board) {

    PiecePosition movingPiece = board.getPiece(from);
    Optional<PiecePosition> capturedPiece = board.isThereAnyPiece(to);

    if (isCastlingMove(from, to, movingPiece.getPiece())) {

      return to.getCol() == Col.G ? "O-O" : "O-O-O";
    }

    StringBuilder moveNotation = new StringBuilder();

    // The pawn haven't initial letter, only the destination column
    if (movingPiece.getPiece().is(PieceType.PAWN)) {

      if (capturedPiece.isPresent()) {

        moveNotation.append(
            from.getCol().name().toLowerCase()); // Column from which the pawn captured
        moveNotation.append('x');
      }
      moveNotation.append(posToAlgebraic(to));

    } else {

      // Añadir la letra correspondiente a la pieza (ej: N para caballos)
      moveNotation.append(toPGNLetter(movingPiece.getPiece().getType()));

      // Si es una captura
      if (capturedPiece.isPresent()) {

        moveNotation.append('x');
      }

      moveNotation.append(posToAlgebraic(to));
    }

    return moveNotation.toString();
  }

  //TODO Fix this method, it's not working, we should use the chess rules and board to check the origin piece (from),
  // because the board can have more than one piece from the to position
  @Override
  public List<Pos> fromAlgebraic(String move, ChessBoard board) {
    // Esta implementación analizará el movimiento en notación algebraica y devolverá las posiciones
    // desde/hasta

    /*
    if (move.equals("O-O")) {
      return createCastlingMove(PieceColor.WHITE, true, board);
    } else if (move.equals("O-O-O")) {
      return createCastlingMove(PieceColor.WHITE, false, board);
    }

    // Ejemplo: "Nxf3"
    if (move.contains("x")) {
      // Se trata de una captura
      // Obtener la pieza que captura y la posición destino
      char pieceChar = move.charAt(0);
      Col colTo = Col.valueOf(String.valueOf(move.charAt(move.length() - 2)).toUpperCase());
      Row rowTo = Row.valueOf(String.valueOf(move.charAt(move.length() - 1)));

      // Encontrar desde qué posición se movió esa pieza
      Pos from = findPiecePosition(pieceChar, colTo, rowTo, board);

      return new Move(from, new Pos(colTo, rowTo));
    }

    // Implementación adicional
    return null;
     */
    return List.of();
  }

  /**
   * The method checks two conditions: piece.is(PieceType.KING): It verifies if the piece being
   * moved is a king. Math.abs(from.getCol().ordinal() - to.getCol().ordinal()) == 2: It checks if
   * the absolute difference between the starting and ending column is 2.
   */
  private boolean isCastlingMove(Pos from, Pos to, Piece piece) {

    // Implement logic to check if the move is castling
    return piece.is(PieceType.KING)
        && (Math.abs(from.getCol().ordinal() - to.getCol().ordinal()) == 2);
  }

  protected static String toPGNLetter(final PieceType pieceType) {

    switch (pieceType) {
      case KING:
        return "K";
      case QUEEN:
        return "Q";
      case ROOK:
        return "R";
      case BISHOP:
        return "B";
      case KNIGHT:
        return "N";
      case PAWN:
        return "";
      default:
        throw new IllegalStateException("Unknown piece: " + pieceType);
    }
  }

  protected static String posToAlgebraic(final Pos pos) {

    return pos.getCol().name().toLowerCase() + pos.getRow().getValue();
  }
}
