package com.davidp.chessjourney.domain.games.tactic;

import com.davidp.chessjourney.domain.ChessBoard;
import com.davidp.chessjourney.domain.ChessBoardFactory;
import com.davidp.chessjourney.domain.Game;
import com.davidp.chessjourney.domain.common.DifficultyLevel;
import com.davidp.chessjourney.domain.common.Exercise;
import com.davidp.chessjourney.domain.common.Fen;
import com.davidp.chessjourney.domain.common.TimeControl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Controlador de un juego de táctica (v2).
 * Modelo de estados pensado para la UI:
 *  - WAITING_TO_START: antes de arrancar.
 *  - AWAITING_USER_MOVE: la UI debe pedir el movimiento del usuario (resolver el ply actual).
 *  - AWAITING_ENGINE_MOVE: el usuario acertó y hay movimiento automático pendiente (respuesta del ejercicio).
 *  - READY_FOR_NEXT_EXERCISE: ejercicio acabado (correcto o fallo), listo para cargar siguiente.
 *  - GAME_OVER: no hay más ejercicios.
 *
 * El PGN del ejercicio se parsea a una lista SAN: el usuario SIEMPRE juega los índices pares (0,2,4,...),
 * y si existe índice impar inmediatamente posterior, es el movimiento automático a ejecutar por la UI.
 * El último ply puede tener 1 SAN (sólo usuario).
 */
public class TacticGame2 extends Game {

    public enum GameState {
        WAITING_TO_START,
        AWAITING_USER_MOVE,
        AWAITING_ENGINE_MOVE,
        READY_FOR_NEXT_EXERCISE,
        GAME_OVER
    }

    public enum ExerciseState { PENDING, CORRECT, FAILED }

    // Dependencias dominio
    protected final com.davidp.chessjourney.domain.Player player;
    protected ChessBoard chessBoard;
    protected final TimeControl timeControl;
    protected final DifficultyLevel difficultyLevel;
    protected final List<Exercise> exercises;

    // Estado de juego
    protected GameState gameState = GameState.WAITING_TO_START;
    protected int currentExerciseIndex = 0;
    protected int currentPlyIndex = 0;   // ply actual (0-based)
    protected UUID currentExerciseId;

    // Tiempos
    protected Instant startTime;
    protected Instant exerciseStartTime;
    protected final List<Long> exerciseCompletionTimes = new ArrayList<>();

    // Tracking ejercicios
    protected final List<ExerciseState> exerciseStates = new ArrayList<>();
    protected int correctExercises = 0;
    protected int failedExercises = 0;

    // Datos ejercicio actual
    protected List<String> currentExerciseMoves = new ArrayList<>(); // SAN sin números/resultado
    protected Fen initialExerciseFen;

    // Movimiento automático pendiente (si tras acertar el usuario hay respuesta)
    protected String pendingAutoMove = null;

    public TacticGame2(com.davidp.chessjourney.domain.Player player,
                       TimeControl timeControl,
                       DifficultyLevel difficultyLevel,
                       List<Exercise> exercises) {
        super();
        this.player = player;
        this.timeControl = timeControl;
        this.difficultyLevel = difficultyLevel;
        this.exercises = exercises;

        for (int i = 0; i < exercises.size(); i++) {
            exerciseStates.add(ExerciseState.PENDING);
        }
    }

    /** Inicia el juego y carga el primer ejercicio. */
    public void startGame() {
        if (gameState != GameState.WAITING_TO_START && gameState != GameState.GAME_OVER) {
            throw new IllegalStateException("El juego ya ha comenzado.");
        }
        startTime = Instant.now();
        loadCurrentExercise();
    }

    public void setNewChessBoardState(ChessBoard chessBoard) {

        this.chessBoard = chessBoard;
    }

    /** Carga el ejercicio actual y deja listo el primer ply. */
    public void loadCurrentExercise() {
        if (currentExerciseIndex >= exercises.size()) {
            gameState = GameState.GAME_OVER;
            return;
        }

        Exercise ex = exercises.get(currentExerciseIndex);
        currentExerciseId = ex.getId();

        currentExerciseMoves = parsePGNMoves(ex.getPgn());         // SAN list
        initialExerciseFen = Fen.createCustom(ex.getFen());        // FEN inicial
        chessBoard = ChessBoardFactory.createFromFEN(initialExerciseFen);

        currentPlyIndex = 0;
        pendingAutoMove = null;
        exerciseStartTime = Instant.now();

        gameState = GameState.AWAITING_USER_MOVE;
    }

    /** La UI envía el SAN del usuario para el ply actual. */
    public boolean submitMove(String userSan) {
        if (gameState != GameState.AWAITING_USER_MOVE) return false;

        int userMoveIndex = currentPlyIndex * 2; // usuario juega índices pares
        if (userMoveIndex >= currentExerciseMoves.size()) return false;

        String expected = currentExerciseMoves.get(userMoveIndex);
        boolean correct = Objects.equals(expected, userSan);

        if (!correct) {
            completeCurrentExercise(false);
            return false;
        }

        // Movimiento correcto; ¿hay respuesta automática?
        int engineMoveIndex = userMoveIndex + 1;
        if (engineMoveIndex < currentExerciseMoves.size()) {
            pendingAutoMove = currentExerciseMoves.get(engineMoveIndex);
            gameState = GameState.AWAITING_ENGINE_MOVE;
        } else {
            completeCurrentExercise(true);
        }
        return true;
    }

    /** Indica si hay un movimiento automático pendiente (respuesta del ejercicio). */
    public boolean hasPendingAutoMove() {
        return gameState == GameState.AWAITING_ENGINE_MOVE && pendingAutoMove != null;
    }

    /** Devuelve, sin consumir, el movimiento automático pendiente (SAN) para que la UI lo aplique. */
    public String peekPendingAutoMove() {
        return pendingAutoMove;
    }

    /** La UI confirma que ha aplicado el movimiento automático; se avanza al siguiente ply. */
    public void consumePendingAutoMove() {
        if (!hasPendingAutoMove()) return;

        pendingAutoMove = null;
        currentPlyIndex++;

        int nextUserIndex = currentPlyIndex * 2;
        if (nextUserIndex >= currentExerciseMoves.size()) {
            completeCurrentExercise(true);
        } else {
            gameState = GameState.AWAITING_USER_MOVE;
        }
    }

    /** Completa ejercicio (éxito/fallo), registra métricas y decide siguiente estado. */
    protected void completeCurrentExercise(boolean success) {
        long secs = ChronoUnit.SECONDS.between(exerciseStartTime, Instant.now());
        exerciseCompletionTimes.add(secs);

        exerciseStates.set(currentExerciseIndex, success ? ExerciseState.CORRECT : ExerciseState.FAILED);
        if (success) correctExercises++; else failedExercises++;

        currentExerciseIndex++;
        pendingAutoMove = null;

        if (currentExerciseIndex < exercises.size()) {
            gameState = GameState.READY_FOR_NEXT_EXERCISE;
        } else {
            gameState = GameState.GAME_OVER;
        }
    }

    /** La UI llama cuando quiere ir al siguiente ejercicio. */
    public void startNextExercise() {
        if (gameState != GameState.READY_FOR_NEXT_EXERCISE) return;
        loadCurrentExercise();
    }

    /** Próximo SAN que debe jugar el usuario (si procede). */
    public String getCurrentExpectedMove() {
        if (gameState != GameState.AWAITING_USER_MOVE) return null;
        int userMoveIndex = currentPlyIndex * 2;
        return (userMoveIndex < currentExerciseMoves.size()) ? currentExerciseMoves.get(userMoveIndex) : null;
    }

    /** Número total de plies del ejercicio actual = ceil(moves.size()/2). */
    public int getTotalPliesInCurrentExercise() {
        return (currentExerciseMoves.size() + 1) / 2;
    }

    /** Ply actual 1-based. */
    public int getCurrentPlyNumber() {
        return currentPlyIndex + 1;
    }

    /** Parse básico PGN -> lista SAN (sin números ni resultados). */
    protected List<String> parsePGNMoves(String pgn) {
        if (pgn == null || pgn.isBlank()) return List.of();
        String clean = pgn
                .replaceAll("\\{[^}]*\\}", " ")        // comentarios {...}
                .replaceAll("\\d+\\.+", " ")           // "1.", "1...", etc
                .replaceAll("1-0|0-1|1/2-1/2|\\*", " ")// resultados
                .trim();
        if (clean.isEmpty()) return List.of();
        String[] tokens = clean.split("\\s+");
        List<String> moves = new ArrayList<>(tokens.length);
        for (String t : tokens) if (!t.isBlank()) moves.add(t.trim());
        return moves;
    }

    // ---- Helpers de tiempo y métricas ----

    public long getElapsedTimeInSeconds() {
        if (startTime == null) return 0;
        return ChronoUnit.SECONDS.between(startTime, Instant.now());
    }

    public String getFormattedElapsedTime() {
        long seconds = getElapsedTimeInSeconds();
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    public double getAverageExerciseTime() {
        if (exerciseCompletionTimes.isEmpty()) return 0.0;
        double sum = exerciseCompletionTimes.stream().mapToLong(Long::longValue).sum();
        return sum / exerciseCompletionTimes.size();
    }

    public int getSuccessPercentage() {
        int completed = correctExercises + failedExercises;
        if (completed == 0) return 0;
        return (correctExercises * 100) / completed;
    }

    // ---- Getters de estado ----

    public GameState getGameState() { return gameState; }
    public int getCurrentExerciseNumber() { return currentExerciseIndex + 1; }
    public int getTotalExercises() { return exercises.size(); }
    public int getCorrectExercises() { return correctExercises; }
    public int getFailedExercises() { return failedExercises; }
    public UUID getCurrentExerciseId() { return currentExerciseId; }
    public DifficultyLevel getDifficultyLevel() { return difficultyLevel; }
    public List<ExerciseState> getExerciseStates() { return new ArrayList<>(exerciseStates); }
    public ExerciseState getExerciseState(int index) {
        return index >= 0 && index < exerciseStates.size() ? exerciseStates.get(index) : ExerciseState.PENDING;
    }

    @Override
    public Fen getFen() {
        return chessBoard != null ? chessBoard.getFen() : initialExerciseFen;
    }

    /** ¿Quedan más ejercicios? (para UI que quiera mostrar progreso general) */
    public boolean hasMoreExercises() {
        return currentExerciseIndex < exercises.size();
    }
}