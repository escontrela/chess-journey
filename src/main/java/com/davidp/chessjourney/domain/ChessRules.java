package com.davidp.chessjourney.domain;

import chesspresso.Chess;
import chesspresso.move.Move;
import chesspresso.position.Position;
import com.davidp.chessjourney.domain.common.Col;
import com.davidp.chessjourney.domain.common.Fen;
import com.davidp.chessjourney.domain.common.IllegalMoveException;
import com.davidp.chessjourney.domain.common.Pos;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Chesspresso Move helper methods overview:
 *
 * <ul>
 *   <li><strong>Move.getLongCastle(int toPlay)</strong>
 *       – devuelve el código de movimiento para un enroque largo (O-O-O)
 *         para el bando indicado (Chess.WHITE o Chess.BLACK).</li>
 *
 *   <li><strong>Move.getShortCastle(int toPlay)</strong>
 *       – devuelve el código de movimiento para un enroque corto (O-O)
 *         para el bando indicado.</li>
 *
 *   <li><strong>Move.getRegularMove(int fromSqi, int toSqi, boolean isCapture)</strong>
 *       – construye un movimiento “normal” (no peón, no enroque), codificando
 *         origen, destino y si es captura.</li>
 *
 *   <li><strong>Move.getPawnMove(int fromSqi, int toSqi, boolean capturing, int promotionPiece)</strong>
 *       – construye un movimiento de peón, incluyendo captura y posible
 *         promoción (Chess.QUEEN, Chess.ROOK, etc.).</li>
 *
 *   <li><strong>Move.getEPMove(int fromSqi, int toSqi)</strong>
 *       – construye el movimiento de captura al paso (‘en passant’)
 *         para el peón que va de fromSqi a toSqi.</li>
 *
 *   <li><strong>Move.getString(short move)</strong>
 *       – obtiene la notación humana del movimiento (PGN/SAN), p.ej. “e2xf4”.</li>
 *
 *   <li><strong>Move.getBinaryString(short move)</strong>
 *       – devuelve la representación binaria del código interno (para depuración).</li>
 *
 *   <li><strong>Move.getFromSqi(short move)</strong>
 *       – extrae el índice de casilla origen (0–63) del código de movimiento.</li>
 *
 *   <li><strong>Move.getToSqi(short move)</strong>
 *       – extrae el índice de casilla destino (0–63) del código de movimiento.</li>
 *
 *   <li><strong>Move.getPromotionPiece(short move)</strong>
 *       – si el movimiento incluye promoción, devuelve el código de la pieza
 *         resultante; si no, Chess.NO_STONE.</li>
 * </ul>
 */

/**
 * This class is responsible for implementing the rules of chess.
 */
public class ChessRules {
  // Implement using chess-tempo library (assumed)

  private final Logger logger = LoggerFactory.getLogger(ChessRules.class);

  public List<Pos> getAttackedPositions(Pos position) {
    // Dummy implementation
    return List.of();
  }

  public boolean isValidMoveWithCapture(Pos from, Pos to, Fen actualFen,boolean isCapture) {

    Position position = new Position(actualFen.getStringValue());
    int toIndex = Chess.strToSqi(to.toString().toLowerCase());
    boolean isRealCapture = position.getStone(toIndex) != Chess.NO_STONE;
    if (isRealCapture != isCapture){
      return false;
    }
    return isValidMove(from, to, actualFen);
  }

  public boolean isValidMove(Pos from, Pos to, Fen actualFen) {

    Position position = new Position(actualFen.getStringValue());

    try {

      // Convert Pos to ChessTempo format
      int fromIndex = Chess.strToSqi(from.toString().toLowerCase());
      int toIndex = Chess.strToSqi(to.toString().toLowerCase());
      boolean isCapture = position.getStone(toIndex) != Chess.NO_STONE;

      short move = Move.getRegularMove(fromIndex, toIndex, isCapture);
      return isLegalMove(position, move);

    } catch (Exception e) {

      logger.error("Error trying to validate move from {} to {}", from, to, e);
      throw new IllegalMoveException(e);
    }
    }

  /**
   * Please use the king from and to position to identify the castling move
   * @param from king start position
   * @param to king end position
   * @param actualFen fen of the game
   * @return true if the move is valid
   */
    public boolean isValidCastlingMove(Pos from, Pos to, Fen actualFen) {

      Position position = new Position(actualFen.getStringValue());

      try {

        short move;

        if (to.getCol() == Col.G) {
          move = Move.getShortCastle(position.getToPlay());
        } else if (to.getCol() == Col.C) {
          move = Move.getLongCastle(position.getToPlay());
        }else {
          return false;
        }

        return isLegalMove(position, move);

      } catch (Exception e) {

        logger.error("Error trying to validate castling move from {} to {}", from, to, e);
        throw new IllegalMoveException(e);
      }
    }


  /**
   * Validate if the position is checked or mate
   * @param actualFen
   * @return true if the position is checked or mate
   */
  public boolean isCheckOrMate(Fen actualFen) {

    Position position = new Position(actualFen.getStringValue());

    try {

      return position.isCheck() || position.isMate();

    } catch (Exception e) {

      logger.error("Error trying to isVCheckOrMate on FEN:{}", actualFen);
      throw new IllegalMoveException(e);
    }
  }

  private static boolean isLegalMove(Position position, short move) {
    short[] legalMoves = position.getAllMoves();
    // TODO improve this code with a hashmap!
    for (short legalMove : legalMoves) {

      if (Move.isCastle(legalMove)){
        System.out.println("Este es el move de castling!");
      }

      if (move == legalMove) {
        return true;
      }
    }
    return false;
  }
}
