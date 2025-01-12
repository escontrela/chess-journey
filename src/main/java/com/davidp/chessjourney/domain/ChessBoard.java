package com.davidp.chessjourney.domain;

import com.davidp.chessjourney.domain.common.*;
import com.davidp.chessjourney.domain.services.FenService;
import com.davidp.chessjourney.domain.services.FenServiceFactory;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for managing the positions of the pieces on the board and providing
 * methods to verify if there is a piece in a position, move pieces and get pieces from a position.
 */
public class ChessBoard {

  private final FenService fenService = FenServiceFactory.getFenService();
  private final Map<Pos, PiecePosition> board;

  protected PieceColor activeColor = PieceColor.WHITE;
  CastlingAvailability castlingAvailability = new CastlingAvailability(true, true, true, true);
  EnPassantTargetSquare enPassantTargetSquare = new EnPassantTargetSquare(null, false);
  int halfMoveClock = 0;
  int fullMoveNumber = 1;

  public ChessBoard() {

    this.board = new HashMap<>();
  }

  public List<PiecePosition> getAllPiecePositions() {
    // Obtiene todos los valores del mapa y los convierte en una lista
    return new ArrayList<>(board.values());
  }

  public boolean isEnPassantTarget(Pos pos) {
    // Comprobar si la posición actual es el objetivo de una captura al paso
    // Aquí deberías tener la lógica que valide si un peón enemigo se movió
    // dos filas adelante en el último turno y está disponible para ser capturado al paso.

    return (enPassantTargetSquare.isAvailable()
        && enPassantTargetSquare.getPosition().getRow() == pos.getRow()
        && enPassantTargetSquare.getPosition().getCol() == pos.getCol());
  }

  /**
   * Returns all the positions of the pieces of a given type and color.
   *
   * @param type of the piece
   * @param color of the piece
   * @return a list of positions of the pieces of the given type and color.
   */
  public List<Pos> getAllPiecePositionsOfType(PieceType type, PieceColor color) {

    return board.entrySet().stream()
        .filter(
            entry ->
                entry.getValue().getPiece().getType() == type
                    && entry.getValue().getPiece().getColor() == color)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  public Optional<PiecePosition> isThereAnyPiece(Pos position) {

    return Optional.ofNullable(board.get(position));
  }

  public Optional<Collection<PiecePosition>> isThereAnyPiece(Piece piece) {

    return Optional.of(
        board.values().stream()
            .filter(pp -> pp.getPiece().equals(piece))
            .collect(Collectors.toSet()));
  }

  public void dropPieceFromPosition(Pos position) {

    if (isThereAnyPiece(position).isPresent()) {

      board.remove(position);
    } else {
      throw new IllegalArgumentException(
          "There is no piece in the position " + position.toString());
    }
  }
  /**
   * public List<Pos> getAllPossibleMovesForPieceType(PieceType type, Pos to) { List<Pos>
   * possibleMoves = new ArrayList<>();
   *
   * <p>// Iterar por todas las posiciones en el tablero y encontrar las piezas del mismo tipo for
   * (Map.Entry<Pos, PiecePosition> entry : board.entrySet()) { Pos pos = entry.getKey();
   * PiecePosition piece = entry.getValue();
   *
   * <p>// Si la pieza es del mismo tipo y puede moverse a la posición 'to' if
   * (piece.getPiece().getType() == type && canMove(pos, to)) { possibleMoves.add(pos); } }
   *
   * <p>return possibleMoves; }
   */
  public void movePiece(Piece piece, Pos fromPosition, Pos toPosition) {

    if (isThereAnyPiece(fromPosition).isEmpty()) {

      throw new IllegalArgumentException(
          "There is already a piece in the position " + toPosition.toString());
    }
    board.remove(fromPosition);

    if (isThereAnyPiece(toPosition).isPresent()) {

      board.remove(toPosition);
    }

    board.put(toPosition, new PiecePosition(piece, toPosition));
  }

  public void setTurn(final PieceColor activeColor) {

    if (activeColor == null) {

      throw new IllegalArgumentException("Active color cannot be null");
    }

    if (activeColor == PieceColor.WHITE) {

      this.activeColor = PieceColor.WHITE;
    } else {

      this.activeColor = PieceColor.BLACK;
    }
  }

  public PiecePosition getPiece(Pos position) {

    if (isThereAnyPiece(position).isPresent()) {

      return board.get(position);
    } else {
      throw new IllegalArgumentException(
          "There is no piece in the position " + position.toString());
    }
  }

  public void addPiece(Piece piece, Pos position) {

    if (!isThereAnyPiece(position).isPresent()) {
      board.put(position, new PiecePosition(piece, position));
    } else {
      throw new IllegalArgumentException(
          "There is already a piece in the position " + position.toString());
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int row = 7; row >= 0; row--) {
      for (int col = 0; col < 8; col++) {
        Pos pos = new Pos(Col.values()[col], Row.values()[row]);
        Piece piece = null;
        if (isThereAnyPiece(pos).isPresent()) {

          piece = board.get(pos).getPiece();
        }
        if (piece != null) {
          char symbol = piece.getType().getSymbol();
          if (piece.getColor() == PieceColor.WHITE) {
            sb.append(Character.toUpperCase(symbol));
          } else {
            sb.append(Character.toLowerCase(symbol));
          }
        } else {
          sb.append('.');
        }
        sb.append(' ');
      }
      sb.append('\n');
    }
    return sb.toString();
  }

  public Fen getFen() {

    // TODO: implement all the GameState, because we need to know the actual status of the board!
    GameState gameState =
        new GameState(
            getAllPiecePositions(),
            activeColor,
            castlingAvailability,
            enPassantTargetSquare,
            halfMoveClock,
            fullMoveNumber);

    return fenService.parseActualStatus(gameState);
  }

  public void setEnPassantTargetSquare(final EnPassantTargetSquare enPassantTargetSquare) {

    this.enPassantTargetSquare = enPassantTargetSquare;
  }
}
