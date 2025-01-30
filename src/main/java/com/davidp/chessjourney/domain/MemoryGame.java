package com.davidp.chessjourney.domain;

import com.davidp.chessjourney.domain.common.Fen;
import com.davidp.chessjourney.domain.common.PiecePosition;
import com.davidp.chessjourney.domain.common.TimeControl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Clase base para un juego de ajedrez de memoria. */
public class MemoryGame extends Game {

  private final Player player;
  private final TimeControl timeControl;
  private final List<Fen> positions;
  private int currentExerciseIndex;
  private int hiddenPiecesCount;
  private Instant startTime;
  private Instant partialTime;
  private List<PiecePosition> hiddenPiecePositions;
  private final long timeToShowPiecesOnTheCurrentExerciseInSeconds = 5;
  private List <MemoryGamePartialStat> stats = new ArrayList<>();

  public MemoryGame(Player player, ChessBoard board, TimeControl timeControl, List<Fen> positions) {

    super();
    this.player = player;
    this.chessBoard = board;
    this.timeControl = timeControl;
    this.positions = positions;
    this.currentExerciseIndex = 0;
    this.hiddenPiecesCount = 1;
  }


  /**
   * Registra la respuesta del usuario.
   */
  public void submitAnswer(boolean correct) {


    stats.add(new MemoryGamePartialStat(hiddenPiecesCount,correct,getElapsedTimeOfCurrentPosInSeconds() ));// Ajustar tiempo de control con incremento
    //TODO adjust the time control timeControl.adjustTime(solvedExercises);
    nextExercise();
  }

  private long getElapsedTimeOfCurrentPosInSeconds() {
     return java.time.Duration.between(partialTime, Instant.now()).getSeconds();
  }

  /**
   * Permite omitir un ejercicio y registrarlo como fallo.
   */
  public void skipExercise() {
    submitAnswer(false);
  }

  /**
   * Inicia el juego desde la primera posición.
   */
  public void startGame() {

    startTime = Instant.now();
    loadExercise();
  }

  public long getElapsedTimeInSeconds(){

    if (startTime != null) {
      Instant currentTime = Instant.now();
      return java.time.Duration.between(startTime, currentTime).getSeconds();
    }
    return 0;
  }

  public String getFormatedElapsedTime(){

    long seconds = getElapsedTimeInSeconds();
    long minutes = seconds / 60;
    long remainingSeconds = seconds % 60;
    return String.format("%02d:%02d", minutes, remainingSeconds);
  }

  public boolean isTimeToHidePiecesOnTheCurrentExercise() {

    Instant currentTime = Instant.now();
    return java.time.Duration.between(partialTime, currentTime).getSeconds() > timeToShowPiecesOnTheCurrentExerciseInSeconds;
  }

  /**
   * Carga la posición actual y oculta las piezas según la dificultad.
   */
  private void loadExercise() {

    if (currentExerciseIndex < positions.size()) {

      Fen currentFen = positions.get(currentExerciseIndex);
      chessBoard = ChessBoardFactory.createFromFEN(currentFen);
      hiddenPiecePositions = hidePieces(hiddenPiecesCount);
      partialTime = Instant.now();

    }else{

      throw new IllegalStateException("No more exercises available.");
    }
  }


  private List<PiecePosition> hidePieces(int count) {

    List<PiecePosition> allPositions = new ArrayList<>(chessBoard.getAllPiecePositions());

    if (allPositions.isEmpty()) {

      return Collections.emptyList(); // No hay piezas que ocultar
    }

    Collections.shuffle(allPositions);

    int piecesToHide = Math.min(count, allPositions.size());

    List<PiecePosition> hiddenPositions = allPositions.subList(0, piecesToHide);

    return new ArrayList<>(hiddenPositions); // Devolver copia para evitar modificaciones externas
  }


  /**
   * Mueve al siguiente ejercicio.
   */
  public void nextExercise() {

    if (hasMoreExercises()) {

      currentExerciseIndex++;
      increaseDifficulty();
      loadExercise();
    }
  }

  public List<PiecePosition> hideAleatoryPieces() {

    hiddenPiecePositions = hidePieces(hiddenPiecesCount);
    return hiddenPiecePositions;
  }

  /**
   * Verifica si quedan más ejercicios.
   */
  public boolean hasMoreExercises() {

    return currentExerciseIndex < positions.size() - 1;
  }



  /**
   * Ajusta la dificultad aumentando el número de piezas ocultas a medida que el jugador avanza.
   */
  private void increaseDifficulty() {

    int totalExercises = positions.size();

    if (totalExercises == 0) {
      return; // Evitar división por cero
    }

    // Si se han resuelto más del 33% de los ejercicios, ocultar 2 piezas
    if (currentExerciseIndex >= totalExercises * 0.33) {
      hiddenPiecesCount = 2;
    }

    // Si se han resuelto más del 66% de los ejercicios, ocultar 3 piezas
    if (currentExerciseIndex >= totalExercises * 0.66) {
      hiddenPiecesCount = 3;
    }
  }

  /**
   * Devuelve la posición FEN actual.
   */
  @Override
  public Fen getFen() {

    return chessBoard.getFen();
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