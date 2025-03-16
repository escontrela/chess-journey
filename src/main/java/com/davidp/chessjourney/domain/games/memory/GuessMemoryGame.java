package com.davidp.chessjourney.domain.games.memory;

import com.davidp.chessjourney.domain.ChessBoard;
import com.davidp.chessjourney.domain.ChessBoardFactory;
import com.davidp.chessjourney.domain.Player;
import com.davidp.chessjourney.domain.common.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuessMemoryGame extends MemoryGame<PiecePosition> {

    private final int hiddenPiecesCount = 1;
    private int guessPiecesCounts;
    private int successPiecesCount;
    private int totalGuessPiecesCounts;
    private List<PiecePosition> hiddenPiecePositions;
    private final long timeToShowPiecesOnTheCurrentExerciseInSeconds = 5;

    public GuessMemoryGame(Player player, ChessBoard board, TimeControl timeControl,
                           DifficultyLevel difficultyLevel, List<Exercise> exercises) {
        super(player, board, timeControl, difficultyLevel, exercises);
    }

    @Override
    public void loadExercise() {

        if (currentExerciseIndex >= positions.size()) {

            gameState = GameState.GAME_OVER;
            throw new IllegalStateException("No more exercises available.");
        }

        Fen currentFen = positions.get(currentExerciseIndex);
        currentExerciseId = exercises.get(currentExerciseIndex).getId();
        chessBoard = ChessBoardFactory.createFromFEN(currentFen);

        // Calculate the pieces that will be hidden
        hiddenPiecePositions = hidePieces(hiddenPiecesCount);
        partialTime = Instant.now();
        gameState = GameState.SHOWING_PIECES;
    }

    @Override
    public GameKind getGameKind() {

        return  GameKind.GUESS_MEMORY_GAME;
    }


    /**
     * Get the hidden piece positions that should be guessed.
     * @param count The number of pieces to hide.
     * @return A list of piece positions that should be guessed.
     */
    private List<PiecePosition> hidePieces(int count) {

        List<PiecePosition> allPositions = new ArrayList<>(chessBoard.getAllPiecePositions());
        if (allPositions.isEmpty()) {

            return Collections.emptyList(); // No hay piezas para ocultar
        }
        Collections.shuffle(allPositions);
        int piecesToHide = Math.min(count, allPositions.size());
        List<PiecePosition> hiddenPositions = allPositions.subList(0, piecesToHide);
        guessPiecesCounts = 0;
        return new ArrayList<>(hiddenPositions);
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

    @Override
    public boolean submitAnswer(PiecePosition answer) {

        guessPiecesCounts++;
        totalGuessPiecesCounts++;
        System.out.println("total guess pieces: " + totalGuessPiecesCounts);

        if (hiddenPiecePositions.contains(answer)){


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
        return
                this.getGuessPiecesCount() == this.getHiddenPiecePositions().size();
    }

    @Override
    public int getGuessPiecesCount(){

        return guessPiecesCounts;
    }

    @Override
    public int totalHiddenPieces(){

        return hiddenPiecePositions.size();
    }
}