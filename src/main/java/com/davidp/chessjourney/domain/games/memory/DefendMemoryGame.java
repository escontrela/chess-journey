package com.davidp.chessjourney.domain.games.memory;

import com.davidp.chessjourney.domain.ChessBoard;
import com.davidp.chessjourney.domain.ChessBoardFactory;
import com.davidp.chessjourney.domain.Player;
import com.davidp.chessjourney.domain.common.*;
import com.davidp.chessjourney.domain.services.FenService;
import com.davidp.chessjourney.domain.services.FenServiceFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DefendMemoryGame extends MemoryGame<String> {

    // Movimiento defensivo esperado en notación algebraica.
    private String expectedDefensiveMove;
    private final long timeToShowPiecesOnTheCurrentExerciseInSeconds = 5;
    private int totalGuessPiecesCounts;
    private int successPiecesCount;
    private int guessPiecesCounts;
    private final List<String> defensiveMoves = new ArrayList<>();
    private List<PiecePosition> hiddenPiecePositions;
    private FenService fenService = FenServiceFactory.getFenService();

    public DefendMemoryGame(Player player, ChessBoard board, TimeControl timeControl,
                            DifficultyLevel difficultyLevel, List<Exercise> exercises) {
        super(player, board, timeControl, difficultyLevel, exercises);

        exercises.forEach(exercise -> defensiveMoves.add(exercise.getPgn()));
    }

    @Override
    public int getTotalStepsPerExercise() {
        return 1;
    }

    @Override
    public void loadExercise() {

        if (currentExerciseIndex >= positions.size()) {
            gameState = GameState.GAME_OVER;
            throw new IllegalStateException("No hay más ejercicios disponibles.");
        }

        Fen currentFen = positions.get(currentExerciseIndex);
        currentExerciseId = exercises.get(currentExerciseIndex).getId();
        chessBoard = ChessBoardFactory.createFromFEN(currentFen);

        // Calculate the pieces that will be hidden
        hiddenPiecePositions = hidePieces(chessBoard);

        expectedDefensiveMove =  defensiveMoves.get(currentExerciseIndex);

        guessPiecesCounts = 0;

        partialTime = Instant.now();
        gameState = GameState.SHOWING_PIECES;

    }
    private List<PiecePosition> hidePieces(ChessBoard board) {

        List<PiecePosition> piecesToHide = new ArrayList<>();

        final com.davidp.chessjourney.domain.common.GameState fenParserResponse =
                fenService.parseString(board.getFen());

        List<PiecePosition> pieces = fenParserResponse.getPieces();

        pieces.forEach(piecePosition -> {

            if (!piecePosition.getPiece().isColor(fenParserResponse.getActiveColor())) {
                piecesToHide.add(piecePosition);
            }
        });

        return piecesToHide;
    }

    /**
     * Check if it's time to hide the pieces on the current exercise.
     * @return  True if it's time to hide the pieces, false otherwise.
     */
    public boolean isTimeToHidePiecesOnTheCurrentExercise() {

        if (gameState != GameState.SHOWING_PIECES) {

            return false;
        }

        Instant currentTime = Instant.now();

        boolean timeToHide = java.time.Duration.between(partialTime, currentTime)
                .getSeconds() > timeToShowPiecesOnTheCurrentExerciseInSeconds;

        if (timeToHide) {
            gameState = GameState.GUESSING_PIECES;
        }
        return timeToHide;
    }


    @Override
    public int getSuccessPercentage() {

        if (totalGuessPiecesCounts == 0) return 0;
        return Math.round((float) successPiecesCount / totalGuessPiecesCounts * 100);
    }


    /**
     * En este juego el usuario envía un movimiento (String) y se compara con el esperado.
     */

    @Override
    public boolean submitAnswer(String answer) {

        guessPiecesCounts++;
        totalGuessPiecesCounts++;
        System.out.println("total guess pieces: " + totalGuessPiecesCounts);

        if (expectedDefensiveMove.equalsIgnoreCase(answer)){

            successPiecesCount++;
            System.out.println("successPiecesCount: " + successPiecesCount);
            return true;

        }
        return false;
    }


    @Override
    public List<PiecePosition> getHiddenPiecePositions() {

        return hiddenPiecePositions;
    }

    @Override
    public boolean isTimeToMoveToNextExercise() {

        return this.getPartialStepCounter() == 1;
    }

    @Override
    public GameKind getGameKind() {

        return  GameKind.DEFEND_MEMORY_GAME;
    }


    @Override
    public int getPartialStepCounter(){

        return guessPiecesCounts;
    }

    /**
     * Devuelve el movimiento defensivo esperado.
     */
    public String getExpectedDefensiveMove() {

        return expectedDefensiveMove;
    }
}