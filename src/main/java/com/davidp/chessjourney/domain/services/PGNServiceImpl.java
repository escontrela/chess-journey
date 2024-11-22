package com.davidp.chessjourney.domain.services;

import com.davidp.chessjourney.domain.ChessBoard;
import com.davidp.chessjourney.domain.ChessRules;
import com.davidp.chessjourney.domain.common.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
  public String toAlgebraic(Pos from, Pos to, ChessBoard board, ChessRules chessRules, PieceType promoteTo) {

    PiecePosition movingPiece = board.getPiece(from);
    Optional<PiecePosition> capturedPiece = board.isThereAnyPiece(to);

    if (isCastlingMove(from, to, movingPiece.getPiece())) {

      return to.getCol() == Col.G ? "O-O" : "O-O-O";
    }

    StringBuilder moveNotation = new StringBuilder();

    // The pawn haven't initial letter, only the destination column
    if (movingPiece.getPiece().is(PieceType.PAWN)) {

      if (isPromotionMove(to, movingPiece.getPiece())) {
        // Captura con promoción
        if (capturedPiece.isPresent()) {
          moveNotation.append(from.getCol().name().toLowerCase()); // Columna de origen
          moveNotation.append('x'); // Indicador de captura
        }

        moveNotation.append(posToAlgebraic(to)); // Destino
        moveNotation.append('=');
        moveNotation.append(toPGNLetter(promoteTo)); // Letra de la pieza de promoción
        return moveNotation.toString(); // Aquí evitamos agregar información extra
      }

      // Captura al paso
      if (isEnPassantMove(from, to, movingPiece.getPiece(), board)) {

        moveNotation.append(from.getCol().name().toLowerCase()); // Columna de origen
        moveNotation.append('x'); // Indicador de captura
        moveNotation.append(posToAlgebraic(to));
        moveNotation.append(" e.p."); // Agregar la notación de captura al paso
      }
      // Captura normal de peón
      else if (capturedPiece.isPresent()) {

        moveNotation.append(from.getCol().name().toLowerCase()); // Columna de origen
        moveNotation.append('x'); // Indicador de captura
        moveNotation.append(posToAlgebraic(to));
      }
      // Movimiento sin captura
      else {

        moveNotation.append(posToAlgebraic(to));
      }

    } else {

      // Añadir la letra correspondiente a la pieza (ej: N para caballos)
      moveNotation.append(toPGNLetter(movingPiece.getPiece().getType()));

      // Determiner if we should to disambiguate
      String disambiguation = getDisambiguation(movingPiece.getPiece(), from, to, board, chessRules);
      moveNotation.append(disambiguation);

      // Si es una captura
      if (capturedPiece.isPresent()) {

        moveNotation.append('x');
      }

      moveNotation.append(posToAlgebraic(to));
    }

    return moveNotation.toString();
  }

  private String getDisambiguation(Piece piece, Pos from, Pos to,ChessBoard board, ChessRules rules) {

    List<Pos> similarPiecePositions = board.getAllPiecePositionsOfType(piece.getType(), piece.getColor());

    List<Pos> ambiguousMoves = similarPiecePositions.stream()
            .filter(pos -> !pos.equals(from) && rules.isValidMove(pos, to, board.getFen()))
            .collect(Collectors.toList());

    if (ambiguousMoves.isEmpty()) {
      return "";
    }

    // Si hay otras piezas que pueden moverse a "to", debemos desambiguar
    boolean sameColumn = ambiguousMoves.stream().anyMatch(pos -> pos.getCol() == from.getCol());
    boolean sameRow = ambiguousMoves.stream().anyMatch(pos -> pos.getRow() == from.getRow());

    if (sameColumn && sameRow) {
      // Si hay piezas en la misma fila y columna, desambiguamos usando fila y columna
      return posToAlgebraic(from); // ej: Nd2
    } else if (sameColumn) {
      // Si están en la misma columna, desambiguamos con la fila
      return String.valueOf(from.getRow().toString());
    } else if (sameRow) {
      // Si están en la misma fila, desambiguamos con la columna
      return from.getCol().name().toLowerCase();
    }

    return "";
  }

  private boolean isEnPassantMove(Pos from, Pos to, Piece piece, ChessBoard board) {


    if (piece.is(PieceType.PAWN)) {
      // Verificar si el peón está intentando capturar al paso
      Optional<PiecePosition> capturedPiece = board.isThereAnyPiece(to);
      if (capturedPiece.isEmpty()) {
        // El destino está vacío, comprobar si es una captura al paso
        // Posición de destino debería estar en la fila correcta para captura al paso
        if (Math.abs(from.getCol().ordinal() - to.getCol().ordinal()) == 1 &&
                Math.abs(from.getRow().getValue() - to.getRow().getValue()) == 1) {
          // Validar si es un movimiento válido según las reglas del en passant
          // Por ejemplo, verificar si el peón oponente avanzó dos filas y está en la posición correcta.
          return board.isEnPassantTarget(to); // Método que debes tener para determinar si es un en passant
        }
      }
    }
    return false;
  }

  // TODO Fix this method, it's not working, we should use the chess rules and board to check the
  // origin piece (from),
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

  private boolean isPromotionMove(Pos to, Piece piece) {
    // Verificar si el movimiento es una promoción de peón
    return piece.is(PieceType.PAWN) &&
            (to.getRow() == Row.ONE || to.getRow() == Row.EIGHT);
  }


}
