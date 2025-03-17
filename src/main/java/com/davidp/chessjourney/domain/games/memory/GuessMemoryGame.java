package com.davidp.chessjourney.domain.games.memory;

import com.davidp.chessjourney.domain.ChessBoard;
import com.davidp.chessjourney.domain.ChessBoardFactory;
import com.davidp.chessjourney.domain.Player;
import com.davidp.chessjourney.domain.common.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A memory game implementation where players must remember and guess the positions of hidden chess pieces.
 * This game extends the base MemoryGame class and implements specific logic for piece guessing mechanics.
 *
 * <p>The game flow works as follows:
 * <ol>
 *   <li>Pieces are shown to the player for a fixed time period</li>
 *   <li>Selected pieces are hidden from the board</li>
 *   <li>Player must guess the correct positions and pieces that were hidden</li>
 *   <li>Score is calculated based on correct guesses</li>
 * </ol>
 *
 * <p>Game states transition through:
 * <ul>
 *   <li>WAITING_TO_START - Initial state</li>
 *   <li>SHOWING_PIECES - Pieces are visible to memorize</li>
 *   <li>GUESSING_PIECES - Player must guess hidden pieces</li>
 *   <li>GAME_OVER - Final state when exercises are completed</li>
 * </ul>
 *
 * @see MemoryGame
 * @see GameKind#GUESS_MEMORY_GAME
 */
public class GuessMemoryGame extends MemoryGame<PiecePosition> {

    private static final int MIN_PIECES_TO_HIDE = 1;
    private static final int MAX_PIECES_TO_HIDE = 2;

    private int partialStepCounter;
    private int successPiecesCount;
    /**
     * The total number of guess pieces that the user has to guess in total.
     */
    private int totalStepsAllOverTheExercises;

    private List<PiecePosition> hiddenPiecePositions;

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

        int numPiecesToHide = getRandomNumberUsingThreadLocalRandom();

        // Calculate the pieces that will be hidden
        hiddenPiecePositions = calculateHiddenPieces(numPiecesToHide);

        partialTime = Instant.now();
        gameState = GameState.SHOWING_PIECES;
    }

    @Override
    public GameKind getGameKind() {

        return  GameKind.GUESS_MEMORY_GAME;
    }


    protected int getRandomNumberUsingThreadLocalRandom() {

        return ThreadLocalRandom.current().nextInt(MIN_PIECES_TO_HIDE, MAX_PIECES_TO_HIDE);
    }

    /**
     * Get the hidden piece positions that should be guessed.
     * @param count The number of pieces to hide.
     * @return A list of piece positions that should be guessed.
     */
    private List<PiecePosition> calculateHiddenPieces(int count) {

        List<PiecePosition> allPositions = new ArrayList<>(chessBoard.getAllPiecePositions());
        if (allPositions.isEmpty()) {

            return Collections.emptyList(); // No hay piezas para ocultar
        }

        Collections.shuffle(allPositions);
        int piecesToHide = Math.min(count, allPositions.size());
        List<PiecePosition> hiddenPositions = allPositions.subList(0, piecesToHide);

        partialStepCounter = 0;
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

        if (totalStepsAllOverTheExercises == 0) return 0;
        return Math.round((float) successPiecesCount / totalStepsAllOverTheExercises * 100);
    }

    @Override
    public boolean submitAnswer(PiecePosition answer) {

        partialStepCounter++;
        totalStepsAllOverTheExercises++;

        if (hiddenPiecePositions.contains(answer)){

            successPiecesCount++;
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

        return this.getPartialStepCounter()
                        == this.getTotalStepsPerExercise();
    }

    @Override
    public int getPartialStepCounter(){

        return partialStepCounter;
    }

    @Override
    public int getTotalStepsPerExercise(){

        return hiddenPiecePositions.size();
    }
}