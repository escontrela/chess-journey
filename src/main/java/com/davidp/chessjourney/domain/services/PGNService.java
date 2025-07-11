// File: PGNService.java
package com.davidp.chessjourney.domain.services;

import com.davidp.chessjourney.domain.ChessBoard;
import com.davidp.chessjourney.domain.ChessRules;
import com.davidp.chessjourney.domain.common.GameMove;
import com.davidp.chessjourney.domain.common.PieceType;
import com.davidp.chessjourney.domain.common.Pos;

/**
 * Servicio para convertir movimientos de ajedrez entre las coordenadas
 * internas de la aplicación y la notación PGN (Portable Game Notation).
 *
 * <p>Proporciona dos operaciones fundamentales:
 * <ul>
 *   <li><strong>toAlgebraic</strong>: genera la notación algebraica estándar
 *       a partir de una jugada representada por coordenadas origen y destino,
 *       aplicando las reglas de ajedrez y manejando promociones.</li>
 *   <li><strong>fromAlgebraic</strong>: analiza un movimiento en notación PGN
 *       para producir las coordenadas de origen y destino que la aplicación
 *       utilizará internamente.</li>
 * </ul>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Portable_Game_Notation">
 *      Portable Game Notation (PGN)</a>
 */
public interface PGNService {

    /**
     * Genera la notación algebraica estándar (PGN) para un movimiento dado.
     *
     * @param from       Posición de partida de la pieza (coordenadas internas).
     * @param to         Posición de destino de la pieza (coordenadas internas).
     * @param board      Estado actual del tablero de ajedrez.
     * @param chessRules Conjunto de reglas que rigen la validez de los movimientos.
     * @param promoteTo  Tipo de pieza en caso de promoción (si no hay promoción, null o pieza vacía).
     * @return Cadena con el movimiento en notación algebraica PGN, incluyendo
     *         marca de captura, jaque, mate o promoción según corresponda.
     * @throws IllegalArgumentException Si el movimiento no es válido según las reglas.
     */
    String toAlgebraic(Pos from, Pos to, ChessBoard board, ChessRules chessRules, PieceType promoteTo);

    /**
     * Convierte una jugada en notación algebraica PGN a coordenadas internas.
     *
     * @param move  Cadena con el movimiento en notación algebraica estándar.
     * @param board Estado actual del tablero de ajedrez.
     * @return Objeto {@code GameMove} que contiene la posición de origen y
     *         destino correspondientes al movimiento.
     * @throws IllegalArgumentException Si la notación no se reconoce o es inválida.
     */
    GameMove fromAlgebraic(String move, ChessBoard board);
}