package com.davidp.chessjourney.domain.services;

import com.davidp.chessjourney.domain.ChessBoard;
import com.davidp.chessjourney.domain.ChessRules;
import com.davidp.chessjourney.domain.common.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class is responsible for implements the service for managing the PGN format
 * and conversions between algebraic coordinates and standard algebraic notation.
 *
 * <p><a href="https://en.wikipedia.org/wiki/Portable_Game_Notation">...</a>
 */
public class PGNServiceImpl implements PGNService {

    public static final String PGN_REGEXPR = "^(?:(O-O(?:-O)?)([+#])?|([NBRQK])?([a-h1-8])?(x)?([a-h][1-8])(?:=([NBRQK]))?([+#])?)$";

    private static class Holder {

        private static final PGNServiceImpl INSTANCE = new PGNServiceImpl();
    }

    public static PGNServiceImpl getInstance() {

        return Holder.INSTANCE;
    }


    /**
     * This method converts algebraic coordinates (e.g., e2, e4)
     * into standard algebraic notation (e.g., e4, Nf3)
     * Example: c2 e5 : Ne5
     *
     * @param from       The starting position of the piece.
     * @param to         The destination position of the piece.
     * @param board      The current state of the chessboard.
     * @param chessRules The current state of the chessboard.
     * @param promoteTo  The piece to be promoted
     * @return String with the algebraic notation
     */
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

    public enum PGNRegExprGroups {
        CASTLING_GROUP_1(1), CASTLING_CHECK_OR_MATE_GROUP_2(2), PIECE_GROUP_3(3), DISAMBIGUATION_GROUP_4(4), CAPTURE_GROUP_5(5), DESTINATION_GROUP_6(6), PROMOTION_GROUP_7(7), CHECK_OR_MATE_GROUP_8(8);

        private final int groupIndex;

        PGNRegExprGroups(int groupIndex) {
            this.groupIndex = groupIndex;
        }

        public int getGroupIndex() {
            return groupIndex;
        }
    }

    private String getDisambiguation(Piece piece, Pos from, Pos to, ChessBoard board, ChessRules rules) {

        List<Pos> similarPiecePositions = board.getAllPiecePositionsOfType(piece.getType(), piece.getColor());

        List<Pos> ambiguousMoves = similarPiecePositions.stream().filter(pos -> !pos.equals(from) && rules.isValidMove(pos, to, board.getFen())).collect(Collectors.toList());

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
                if (Math.abs(from.getCol().ordinal() - to.getCol().ordinal()) == 1 && Math.abs(from.getRow().getValue() - to.getRow().getValue()) == 1) {
                    // Validar si es un movimiento válido según las reglas del en passant
                    // Por ejemplo, verificar si el peón oponente avanzó dos filas y está en la posición
                    // correcta.
                    return board.isEnPassantTarget(to); // Método que debes tener para determinar si es un en passant
                }
            }
        }
        return false;
    }

    /**
     * Extracts valuable content from regex groups based on the provided PGN notation
     *
     * @param notation       The chess move notation (e.g., "Nf3", "O-O", "exf6+")
     * @param pattern        The compiled regex pattern
     * @param relevantGroups The enum groups to extract
     * @return Map with enum keys and their corresponding extracted values
     */
    public static Map<PGNRegExprGroups, String> extractValuableGroups(String notation, Pattern pattern, PGNRegExprGroups... relevantGroups) {

        Map<PGNRegExprGroups, String> result = new HashMap<>();

        Matcher matcher = pattern.matcher(notation);

        if (!matcher.matches()) {
            return result; // Return empty map if no match
        }

        for (PGNRegExprGroups group : relevantGroups) {

            String groupValue = matcher.group(group.getGroupIndex());

            // Only add groups with valuable content (not null and not empty)
            if (groupValue != null && !groupValue.trim().isEmpty()) {

                result.put(group, groupValue);
            }
        }

        return result;
    }

    /**
     * Alternative method that returns all non-empty groups from the regex
     *
     * @param notation The chess move notation
     * @param pattern  The compiled regex pattern
     * @return Map with all enum groups that have valuable content
     */
    public static Map<PGNRegExprGroups, String> extractAllValuableGroups(String notation, Pattern pattern) {

        return extractValuableGroups(notation, pattern, PGNRegExprGroups.values());
    }

    // TODO Fix this method, it's not working, we should use the chess rules and board to check the
    // origin piece (from),
    // https://regex101.com/
    // because the board can have more than one piece from the to position
    // INMO this method do not have to validate the move, it's the chess rules that must do it, only should transform the notation to a list of positions

    /**
     * This method converts  standard algebraic notation (e.g., e4, Nf3)
     * into algebraic coordinates (e.g., e2, e4) to allow moves to be made
     * on the board.
     *
     * @param move  The move in standard algebraic notation.
     * @param board The current state of the chessboard.
     * @return A response indicating the different parts of the move.
     */
    @Override
    public GameMove fromAlgebraic(String move, ChessBoard board) {

        GameMove toret = null;
        boolean isCheck = false;
        boolean isMate = false;
        boolean isPromotion = false;
        boolean isCastling = false;
        boolean isEnPassant = false;
        boolean isCapture = false;
        PieceType promotionPiece = null;

        var gameStatue = board.getGameState();
        var activeColor = gameStatue.getActiveColor(); // necesario para identificar si movemos blancas o negras.

        // TODO Utilizar el método board.getAllPiecePositionsOfType()... para identificar las posiciones de las piezas que participan en el movimiento
        // TODO sin entran variantes no standard no ajustar la expresión regular, mejor convertir esas variantes a standard

        Pattern capturePattern = Pattern.compile(PGN_REGEXPR);
        Map<PGNRegExprGroups, String> groups = extractAllValuableGroups(move, capturePattern);

        if (groups.isEmpty()) {

            throw new IllegalArgumentException("Invalid move notation: " + move);
        }


        // Test if the move is a castling
        if (groups.containsKey(PGNRegExprGroups.CASTLING_GROUP_1)) {

            isCastling = true;
            String castling = groups.get(PGNRegExprGroups.CASTLING_GROUP_1);
            Row baseCastlingRow = (activeColor == PieceColor.WHITE ? Row.ONE : Row.EIGHT);

            Pos kingFrom = null;
            Pos kingTo = null;
            Pos rookFrom = null;
            Pos rookTo = null;

            if ("O-O".equals(castling)) {

                // si son blancas sería e1 - g1 , a1 - d1
                // si son negras  sería e8 - g8 , a8 - d8
                kingFrom = Pos.of(Col.E, baseCastlingRow);
                kingTo = Pos.of(Col.G, baseCastlingRow);
                rookFrom = Pos.of(Col.H, baseCastlingRow);
                rookTo = Pos.of(Col.F, baseCastlingRow);

            } else if ("O-O-O".equalsIgnoreCase(castling)) {

                // si son blancas sería e1 - c1 , a1 - d1
                // si son negras  sería e8 - c8 , a8 - d8
                kingFrom = Pos.of(Col.E, baseCastlingRow);
                kingTo = Pos.of(Col.C, baseCastlingRow);
                rookFrom = Pos.of(Col.A, baseCastlingRow);
                rookTo = Pos.of(Col.D, baseCastlingRow);
            } else {

                throw new IllegalArgumentException("Not valid move found for castling notation: " + move);
            }

            BoardMove kingMove = new BoardMove(kingFrom, kingTo);
            BoardMove rookMove = new BoardMove(rookFrom, rookTo);


            // now we are gone test check or checkmate
            if (groups.containsKey(PGNRegExprGroups.CASTLING_CHECK_OR_MATE_GROUP_2)) {

                String checkOrMate = groups.get(PGNRegExprGroups.CASTLING_CHECK_OR_MATE_GROUP_2);
                if ("+".equalsIgnoreCase(checkOrMate)) {
                    isCheck = true;
                }
                if ("#".equalsIgnoreCase(checkOrMate)) {
                    isMate = true;
                }
            }

            toret = GameMoveFactory.createCastlingMove(kingMove, rookMove, isCheck, isMate);
        }

        return toret;
    }

    /**
     * The method checks two conditions: piece.is(PieceType.KING): It verifies if the piece being
     * moved is a king. Math.abs(from.getCol().ordinal() - to.getCol().ordinal()) == 2: It checks if
     * the absolute difference between the starting and ending column is 2.
     */
    private boolean isCastlingMove(Pos from, Pos to, Piece piece) {

        // Implement logic to check if the move is castling
        return piece.is(PieceType.KING) && (Math.abs(from.getCol().ordinal() - to.getCol().ordinal()) == 2);
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
        return piece.is(PieceType.PAWN) && (to.getRow() == Row.ONE || to.getRow() == Row.EIGHT);
    }
}