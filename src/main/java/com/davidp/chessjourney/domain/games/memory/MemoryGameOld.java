package com.davidp.chessjourney.domain.games.memory;

import com.davidp.chessjourney.domain.ChessBoard;
import com.davidp.chessjourney.domain.ChessBoardFactory;
import com.davidp.chessjourney.domain.Game;
import com.davidp.chessjourney.domain.Player;
import com.davidp.chessjourney.domain.common.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


/** Clase base para un juego de ajedrez de memoria. */
public class MemoryGameOld extends Game {

  private final Player player;
  private final TimeControl timeControl;
  private final List<Fen> positions;
  private int currentExerciseIndex;
  private int hiddenPiecesCount;
  private int guessPiecesCounts;
  private int successPiecesCount;
  private int totalGuessPiecesCounts;
  private Instant startTime;
  private Instant partialTime;
  private List<PiecePosition> hiddenPiecePositions;
  private final long timeToShowPiecesOnTheCurrentExerciseInSeconds = 5;
  private List <MemoryGamePartialStat> stats = new ArrayList<>();
  private DifficultyLevel difficultyLevel;
  private  List<Exercise> exercises;
  protected UUID currentExerciseId;

  private GameState gameState;

  public MemoryGameOld(Player player, ChessBoard board, TimeControl timeControl, DifficultyLevel difficultyLevel, List<Exercise> exercises) {

    super();
    this.player = player;
    this.chessBoard = board;
    this.timeControl = timeControl;
    this.currentExerciseIndex = 0;
    this.hiddenPiecesCount = 1;
    this.gameState = GameState.WAITING_TO_START;
    this.exercises = exercises;
    this.difficultyLevel = difficultyLevel;
    this.positions = new ArrayList<>();

    exercises.forEach(exercise -> {

      positions.add(Fen.createCustom(exercise.getFen()));
    });
  }


  /**
   * Registra la respuesta del usuario.
   */
  public void submitAnswer(boolean correct) {

    if (gameState != GameState.GUESSING_PIECES) {

      throw new IllegalStateException("No se puede enviar respuesta en este estado.");
    }

    stats.add(new MemoryGamePartialStat(hiddenPiecesCount, correct, getElapsedTimeOfCurrentPosInSeconds()));
    nextExercise();
    //TODO adjust the time control timeControl.adjustTime(solvedExercises);
  }


  /**
   * Devuelve el tiempo transcurrido en la posición actual.
   */
  private long getElapsedTimeOfCurrentPosInSeconds() {
    return java.time.Duration.between(partialTime, Instant.now()).getSeconds();
  }

  /**
   * Permite omitir un ejercicio y registrarlo como fallo.
   */
  public void skipExercise() {

    if (gameState != GameState.GUESSING_PIECES) {

      throw new IllegalStateException("No se puede omitir un ejercicio en este estado.");
    }

    submitAnswer(false);
  }

  /**
   * Inicia el juego desde la primera posición.
   */
  public void startGame() {

    if (gameState != GameState.WAITING_TO_START && gameState != GameState.GAME_OVER) {

      throw new IllegalStateException("The game is already started!.");
    }

    startTime = Instant.now();
    gameState = GameState.SHOWING_PIECES;  // Cambia de estado
    loadExercise();
  }

  /**
   * Devuelve el tiempo transcurrido desde que se inició el juego.
   */
  public long getElapsedTimeInSeconds() {
    if (startTime == null) return 0;
    return java.time.Duration.between(startTime, Instant.now()).getSeconds();
  }



  public String getFormatedElapsedTime(){

    long seconds = getElapsedTimeInSeconds();
    long minutes = seconds / 60;
    long remainingSeconds = seconds % 60;
    return String.format("%02d:%02d", minutes, remainingSeconds);
  }

  public boolean isTimeToHidePiecesOnTheCurrentExercise() {

    if (gameState != GameState.SHOWING_PIECES) {

      return false;
    }

    Instant currentTime = Instant.now();
    boolean timeToHide = java.time.Duration.between(partialTime, currentTime).getSeconds()
            > timeToShowPiecesOnTheCurrentExerciseInSeconds;

    if (timeToHide) {

      gameState = GameState.GUESSING_PIECES; // Cambiamos al estado de adivinanza
    }

    return timeToHide;
  }

  /**
   * Carga la posición actual y oculta las piezas según la dificultad.
   */
  private void loadExercise() {

    if (currentExerciseIndex >= positions.size()) {
      gameState = GameState.GAME_OVER;
      throw new IllegalStateException("No more exercises available.");
    }

    Fen currentFen = positions.get(currentExerciseIndex);
    currentExerciseId = exercises.get(currentExerciseIndex).getId();

    chessBoard = ChessBoardFactory.createFromFEN(currentFen);
    hiddenPiecePositions = hidePieces(hiddenPiecesCount);
    partialTime = Instant.now();

    gameState = GameState.SHOWING_PIECES;
  }

  public int totalHiddenPieces(){

    return hiddenPiecePositions.size();
  }

  public int getGuessPiecesCount(){

    return guessPiecesCounts;
  }

  public boolean guessPiece(final PiecePosition piecePosition){

    guessPiecesCounts++;
    totalGuessPiecesCounts++;
    System.out.println("total guess pieces: " + totalGuessPiecesCounts);

    if (hiddenPiecePositions.contains(piecePosition)){


      successPiecesCount++;
      System.out.println("successPiecesCount: " + successPiecesCount);
      return true;

    }
    return false;
  }

  public int getSuccessPercentage() {

    if (totalGuessPiecesCounts == 0) {

      return 0;
    }
    return Math.round((float) successPiecesCount / totalGuessPiecesCounts * 100);
  }

  private List<PiecePosition> hidePieces(int count) {

    List<PiecePosition> allPositions = new ArrayList<>(chessBoard.getAllPiecePositions());

    if (allPositions.isEmpty()) {

      return Collections.emptyList(); // No hay piezas que ocultar
    }

    Collections.shuffle(allPositions);

    int piecesToHide = Math.min(count, allPositions.size());

    List<PiecePosition> hiddenPositions = allPositions.subList(0, piecesToHide);

    guessPiecesCounts = 0;
    return new ArrayList<>(hiddenPositions); // Devolver copia para evitar modificaciones externas
  }

  public DifficultyLevel getDifficultyLevel() {

    return difficultyLevel;
  }

  /**
   * Mueve al siguiente ejercicio.
   */
  public void nextExercise() {

    if (!hasMoreExercises()) {

      gameState = GameState.GAME_OVER;
      return;
    }

    currentExerciseIndex++;
    guessPiecesCounts = 0;
    increaseDifficulty();
    loadExercise();
  }

  public int getCurrentExerciseNumber(){

    return  currentExerciseIndex + 1;
  }


  public List<PiecePosition> getHiddenPiecePositions() {

    return hiddenPiecePositions;
  }

  /**
   * Verifica si quedan más ejercicios.
   */
  public boolean hasMoreExercises() {

    return currentExerciseIndex < positions.size() - 1;
  }

  /**
   * Return the game status for the controller
   * @return GameState game status
   */
  public GameState getGameState(){

    return gameState;
  }

  /**
   * Ajusta la dificultad aumentando el número de piezas ocultas a medida que el jugador avanza.
   */
  private void increaseDifficulty() {

    int totalExercises = positions.size();

    if (totalExercises == 0) {
      return; // Evitar división por cero
    }

    return;
  }

  /**
   * Devuelve la posición FEN actual.
   */
  @Override
  public Fen getFen() {

    return chessBoard.getFen();
  }

  public UUID getCurrentExerciseId(){

    return currentExerciseId;
  }

  /**
   * These states are using to control the game status
   */
  public enum GameState {
    WAITING_TO_START,
    SHOWING_PIECES,
    GUESSING_PIECES,
    GAME_OVER
  }

  public static class MemoryGamePartialStat{

    private int level;
    private boolean succeded;
    private long time;

    public MemoryGamePartialStat(int level,boolean succeed,long time) {
        this.level = level;
        this.succeded = succeed;
        this.time = time;
    }
  }
}
