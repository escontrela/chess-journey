package com.davidp.chessjourney.domain.games.tactic;

import com.davidp.chessjourney.domain.ChessBoard;
import com.davidp.chessjourney.domain.ChessBoardFactory;
import com.davidp.chessjourney.domain.Game;
import com.davidp.chessjourney.domain.Player;
import com.davidp.chessjourney.domain.common.*;
import com.davidp.chessjourney.domain.services.PGNService;
import com.davidp.chessjourney.domain.services.PGNServiceFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Tactic game where the player solves tactical exercises by playing the correct moves.
 * The player is always white and must complete all plies of the PGN solution.
 */
public class TacticGame extends Game {

    public enum GameState {
        WAITING_TO_START,
        PLAYING,
        PLAYING_NEW_EXERCISE,
        EXERCISE_COMPLETED,
        GAME_OVER
    }

    public enum ExerciseState {
        PENDING,
        CORRECT,
        FAILED
    }

    protected final Player player;
    protected ChessBoard chessBoard;
    protected final TimeControl timeControl;
    protected final DifficultyLevel difficultyLevel;
    protected final List<Exercise> exercises;
    protected final PGNService pgnService = PGNServiceFactory.getPGNService();

    // Game state
    protected GameState gameState = GameState.WAITING_TO_START;
    protected GameState lastGame = GameState.WAITING_TO_START;
    protected int currentExerciseIndex = 0;
    protected int currentPlyIndex = 0;
    protected UUID currentExerciseId;

    // Timing
    protected Instant startTime;
    protected Instant exerciseStartTime;
    protected final List<Long> exerciseCompletionTimes = new ArrayList<>();

    // Exercise tracking
    protected final List<ExerciseState> exerciseStates = new ArrayList<>();
    protected int correctExercises = 0;
    protected int failedExercises = 0;

    // Current exercise data
    protected List<String> currentExerciseMoves = new ArrayList<>();
    protected Fen initialExerciseFen;

    public TacticGame(Player player, TimeControl timeControl,
                     DifficultyLevel difficultyLevel, List<Exercise> exercises) {
        super();
        this.player = player;
        this.timeControl = timeControl;
        this.difficultyLevel = difficultyLevel;
        this.exercises = exercises;

        // Initialize exercise states as pending
        for (Exercise exercise : exercises) {
            exerciseStates.add(ExerciseState.PENDING);
        }
    }

    /**
     * Starts the tactic game from the first exercise.
     */
    public void startGame() {
        if (gameState != GameState.WAITING_TO_START && gameState != GameState.GAME_OVER) {
            throw new IllegalStateException("El juego ya ha comenzado.");
        }

        startTime = Instant.now();
        gameState = GameState.PLAYING;
        loadCurrentExercise();
    }

    /**
     * Loads the current exercise and sets up the board.
     */
    public void loadCurrentExercise() {

        if (currentExerciseIndex >= exercises.size()) {
            gameState = GameState.GAME_OVER;
            return;
        }

        Exercise currentExercise = exercises.get(currentExerciseIndex);
        currentExerciseId = currentExercise.getId();

        // Parse PGN to get the list of moves
        currentExerciseMoves = parsePGNMoves(currentExercise.getPgn());

        // Set up board with the exercise FEN
        initialExerciseFen = Fen.createCustom(currentExercise.getFen());
        chessBoard = ChessBoardFactory.createFromFEN(initialExerciseFen);

        // Reset ply index for new exercise
        currentPlyIndex = 0;
        exerciseStartTime = Instant.now();

        gameState = GameState.PLAYING;
    }

    public void setCurrentFen(Fen fen) {
        if (fen == null) {
            throw new IllegalArgumentException("FEN cannot be null");
        }
        chessBoard = ChessBoardFactory.createFromFEN(fen);
    }
    /**
     * Validates if the given move is the correct next move in the current exercise.
     * @param move The move in algebraic notation (e.g., "Nf3", "Qxh7+")
     * @return true if the move is correct, false otherwise
     */
    public boolean submitMove(String move) {

        if (gameState != GameState.PLAYING && gameState != GameState.PLAYING_NEW_EXERCISE ) {
            return false;
        }

        if (currentPlyIndex >= currentExerciseMoves.size()) {
            return false;
        }

        gameState =  GameState.PLAYING;

        String expectedMove = currentExerciseMoves.get(currentPlyIndex*2);
        System.out.println("Expected move: " + expectedMove + " for ply index: " + currentPlyIndex + " current exercise: " + currentExerciseIndex);
        boolean isCorrect = move.equals(expectedMove);

        if (isCorrect) {

            currentPlyIndex++;

            // Check if exercise is completed
            if (currentPlyIndex+ 1 >= currentExerciseMoves.size()) {

                completeCurrentExercise(true);

            }
        } else {

            completeCurrentExercise(false);
        }



        return isCorrect;
    }

    /**
     * Completes the current exercise and moves to the next one.
     * @param success whether the exercise was completed successfully
     */
    protected void completeCurrentExercise(boolean success) {
        // Record exercise completion time
        long completionTime = ChronoUnit.SECONDS.between(exerciseStartTime, Instant.now());
        exerciseCompletionTimes.add(completionTime);

        // Update exercise state
        ExerciseState state = success ? ExerciseState.CORRECT : ExerciseState.FAILED;
        exerciseStates.set(currentExerciseIndex, state);

        if (success) {
            correctExercises++;
        } else {
            failedExercises++;
        }

        gameState = GameState.EXERCISE_COMPLETED;

        // Move to next exercise
        currentExerciseIndex++;
        if (currentExerciseIndex < exercises.size()) {
            loadCurrentExercise();
            gameState =  GameState.PLAYING_NEW_EXERCISE;
        } else {
            gameState = GameState.GAME_OVER;
        }
    }

    /**
     * Parses PGN string to extract individual moves.
     * @param pgn The PGN string
     * @return List of moves in algebraic notation
     */
    protected List<String> parsePGNMoves(String pgn) {
        List<String> moves = new ArrayList<>();
        // Simple PGN parsing - remove move numbers and split by spaces
        String cleanPgn = pgn.replaceAll("\\d+\\.", "").trim();
        String[] moveArray = cleanPgn.split("\\s+");

        for (String move : moveArray) {
            if (!move.isEmpty() && !move.equals("*") && !move.startsWith("1-0")
                && !move.startsWith("0-1") && !move.startsWith("1/2-1/2")) {
                moves.add(move);
            }
        }

        return moves;
    }

    /**
     * Gets the current expected move.
     * @return The next move the player should make, or null if exercise is complete
     */
    public String getCurrentExpectedMove() {
        if (currentPlyIndex >= currentExerciseMoves.size()) {
            return null;
        }
        return currentExerciseMoves.get(currentPlyIndex);
    }

    /**
     * Gets the average time per exercise.
     * @return Average completion time in seconds
     */
    public double getAverageExerciseTime() {
        if (exerciseCompletionTimes.isEmpty()) {
            return 0.0;
        }

        double sum = exerciseCompletionTimes.stream().mapToLong(Long::longValue).sum();
        return sum / exerciseCompletionTimes.size();
    }

    /**
     * Gets the success percentage.
     * @return Success percentage (0-100)
     */
    public int getSuccessPercentage() {
        int completedExercises = correctExercises + failedExercises;
        if (completedExercises == 0) {
            return 0;
        }
        return (correctExercises * 100) / completedExercises;
    }

    /**
     * Gets the total elapsed time since game start.
     * @return Elapsed time in seconds
     */
    public long getElapsedTimeInSeconds() {
        if (startTime == null) {
            return 0;
        }
        return ChronoUnit.SECONDS.between(startTime, Instant.now());
    }

    /**
     * Gets formatted elapsed time.
     * @return Time in MM:SS format
     */
    public String getFormattedElapsedTime() {
        long seconds = getElapsedTimeInSeconds();
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    // Getters
    public GameState getGameState() { return gameState; }
    public int getCurrentExerciseNumber() { return currentExerciseIndex + 1; }
    public int getCurrentPlyNumber() { return currentPlyIndex + 1; }
    public int getTotalExercises() { return exercises.size(); }
    public int getTotalPliesInCurrentExercise() { return currentExerciseMoves.size(); }
    public int getCorrectExercises() { return correctExercises; }
    public int getFailedExercises() { return failedExercises; }
    public UUID getCurrentExerciseId() { return currentExerciseId; }
    public DifficultyLevel getDifficultyLevel() { return difficultyLevel; }
    public List<ExerciseState> getExerciseStates() { return new ArrayList<>(exerciseStates); }
    public ExerciseState getExerciseState(int index) {
        return index < exerciseStates.size() ? exerciseStates.get(index) : ExerciseState.PENDING;
    }

    @Override
    public Fen getFen() {
        return chessBoard != null ? chessBoard.getFen() : initialExerciseFen;
    }

    /**
     * Checks if there are more exercises to complete.
     * @return true if there are more exercises
     */
    public boolean hasMoreExercises() {
        return currentExerciseIndex < exercises.size();
    }

    /**
     * Resets the game to start from the beginning.
     */
    public void resetGame() {
        currentExerciseIndex = 0;
        currentPlyIndex = 0;
        correctExercises = 0;
        failedExercises = 0;
        exerciseCompletionTimes.clear();

        for (int i = 0; i < exerciseStates.size(); i++) {
            exerciseStates.set(i, ExerciseState.PENDING);
        }

        gameState = GameState.WAITING_TO_START;
        startTime = null;
        exerciseStartTime = null;
    }

    /**
     * Gets the moves for a specific ply in a specific exercise.
     * @param exerciseIndex The exercise index (0-based)
     * @param plyIndex The ply index (0-based, where each ply represents 2 moves or 1 if at the end)
     * @return Array containing the ply moves, may contain 1 or 2 elements
     */
    public String[] getPlyMoves(int exerciseIndex, int plyIndex) {
        if (exerciseIndex < 0 || exerciseIndex >= exercises.size()) {
            return new String[0];
        }

        Exercise exercise = exercises.get(exerciseIndex);
        List<String> exerciseMoves = parsePGNMoves(exercise.getPgn());

        if (exerciseMoves.isEmpty()) {
            return new String[0];
        }

        // Calculate starting move index for this ply (each ply has 2 moves)
        int startMoveIndex = plyIndex * 2;
        List<String> plyMoves = new ArrayList<>();

        // Add first move of the ply if exists
        if (startMoveIndex < exerciseMoves.size()) {
            plyMoves.add(exerciseMoves.get(startMoveIndex));

            // Add second move of the ply if exists
            if (startMoveIndex + 1 < exerciseMoves.size()) {
                plyMoves.add(exerciseMoves.get(startMoveIndex + 1));
            }
        }

        return plyMoves.toArray(new String[0]);
    }
}
