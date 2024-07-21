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

  final PieceColor activeColor = PieceColor.WHITE;
  final CastlingAvailability castlingAvailability = new CastlingAvailability(true,true,true,true);
  final EnPassantTargetSquare enPassantTargetSquare = new EnPassantTargetSquare(null,false);
  final int halfMoveClock = 0;
  final int fullMoveNumber=1;

  public ChessBoard() {

    this.board = new HashMap<>();
  }

  public List<PiecePosition> getAllPiecePositions() {
    // Obtiene todos los valores del mapa y los convierte en una lista
    return new ArrayList<>(board.values());
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

  public void movePiece(Piece piece, Pos fromPosition, Pos toPosition) {

    if (isThereAnyPiece(fromPosition).isPresent()) {

      if (isThereAnyPiece(toPosition).isPresent()) {

        throw new IllegalArgumentException(
            "There is already a piece in the position " + toPosition.toString());

      } else {

        board.remove(fromPosition);
        board.put(toPosition, new PiecePosition(piece, toPosition));
      }
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
    GameState gameState = new GameState(getAllPiecePositions(), activeColor, castlingAvailability, enPassantTargetSquare, halfMoveClock, fullMoveNumber);

    return fenService.parseActualStatus(gameState);
  }
}
