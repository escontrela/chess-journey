package com.davidp.chessjourney.application.ui.controls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/*
/ Configurar textos
setExerciseAvgTime("20 s.");
setExerciseLevel("5");
setExerciseRating("85%");
setUserName("MARTIN PEREIRA");
setUserRating("1470");
setExerciseTime("24:32");

// Configurar número de rectángulos
setNumExercises(5);  // Crea 5 rectángulos en hbExercises
setNumPly(3);        // Crea 3 rectángulos en hbPly

// Configurar ejercicio/ply actual (con stroke blanco)
setCurrentExercise(2);  // El 3er rectángulo tendrá stroke blanco
setCurrentPly(1);       // El 2do rectángulo tendrá stroke blanco

// Configurar estados de los rectángulos
setExerciseState(0, TacticStatusController.STATE_OK);    // Verde
setExerciseState(1, TacticStatusController.STATE_FAIL);  // Rojo
setPlyState(0, TacticStatusController.STATE_OK);

// Si necesitas hacer binding bidireccional
tacticStatus.exerciseAvgTimeProperty().bind(otherProperty);
tacticStatus.numExercisesProperty().bind(countProperty);

 */

/**
 *  This controller is responsible for the Tactic Status control that displays the status of
 *  exercises and ply in a chess tactics training session.
 */
public class TacticStatusController extends Pane {

  @FXML private HBox hbExercises;

  @FXML private HBox hbPly;

  @FXML private Label lblAvgTime;

  @FXML private Label lblBlackLevel;

  @FXML private Label lblBlackRating;

  @FXML private Label lblWhiteHR;

  @FXML private Label lblWhiteLevel;

  @FXML private Label lblWhiteName;

  @FXML private Label lblWhiteRating;

  @FXML private Label lblWhiteTime;

  @FXML private Pane pnlBlack;

  @FXML private Pane pnlWhite;

  // Properties for labels synchronization
  private final StringProperty exerciseAvgTimeProperty = new SimpleStringProperty();
  private final StringProperty exerciseLevelProperty = new SimpleStringProperty();
  private final StringProperty exerciseRatingProperty = new SimpleStringProperty();
  private final StringProperty userNameProperty = new SimpleStringProperty();
  private final StringProperty userRatingProperty = new SimpleStringProperty();
  private final StringProperty exerciseTimeProperty = new SimpleStringProperty();

  // Properties for exercises and ply
  private final IntegerProperty numExercisesProperty = new SimpleIntegerProperty(0);
  private final IntegerProperty numPlyProperty = new SimpleIntegerProperty(0);
  private final IntegerProperty currentExerciseProperty = new SimpleIntegerProperty(-1);
  private final IntegerProperty currentPlyProperty = new SimpleIntegerProperty(-1);

  // Lists to store rectangles
  private final List<Rectangle> exerciseRectangles = new ArrayList<>();
  private final List<Rectangle> plyRectangles = new ArrayList<>();

  // Exercise states: 0 = normal, 1 = OK, 2 = fail
  private final List<Integer> exerciseStates = new ArrayList<>();
  // Ply states: 0 = normal, 1 = OK, 2 = fail
  private final List<Integer> plyStates = new ArrayList<>();

  // Colors
  private static final Color NORMAL_COLOR = Color.BLACK;
  private static final Color OK_COLOR = Color.GREEN;
  private static final Color FAIL_COLOR = Color.RED;
  private static final Color ACTIVE_STROKE = Color.WHITE;
  private static final Color INACTIVE_STROKE = Color.GRAY;

  public TacticStatusController() {

    FXMLLoader fxmlLoader =
        new FXMLLoader(
            getClass().getResource("/com/davidp/chessjourney/tactics-status-control-view.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);

    try {

      fxmlLoader.load();

    } catch (IOException e) {

      throw new RuntimeException(
          "No se pudo cargar el FXML: /com/davidp/chessjourney/tactics-status-control-view.fxml",
          e);
    }
  }

  @FXML
  private void initialize() {
    // Synchronize properties with labels
    exerciseAvgTimeProperty.addListener((obs, oldVal, newVal) -> lblAvgTime.setText(newVal));
    exerciseLevelProperty.addListener((obs, oldVal, newVal) -> {
      lblBlackLevel.setText(newVal);
      lblWhiteLevel.setText(newVal);
    });
    exerciseRatingProperty.addListener((obs, oldVal, newVal) -> lblBlackRating.setText(newVal));
    userNameProperty.addListener((obs, oldVal, newVal) -> lblWhiteName.setText(newVal));
    userRatingProperty.addListener((obs, oldVal, newVal) -> lblWhiteRating.setText(newVal));
    exerciseTimeProperty.addListener((obs, oldVal, newVal) -> lblWhiteTime.setText(newVal));

    // Listen for changes in number of exercises and ply
    numExercisesProperty.addListener((obs, oldVal, newVal) -> updateExerciseRectangles(newVal.intValue()));
    numPlyProperty.addListener((obs, oldVal, newVal) -> updatePlyRectangles(newVal.intValue()));

    // Listen for current exercise/ply changes
    currentExerciseProperty.addListener((obs, oldVal, newVal) -> updateExerciseStrokes());
    currentPlyProperty.addListener((obs, oldVal, newVal) -> updatePlyStrokes());
  }

  // Property getters
  public StringProperty exerciseAvgTimeProperty() { return exerciseAvgTimeProperty; }
  public StringProperty exerciseLevelProperty() { return exerciseLevelProperty; }
  public StringProperty exerciseRatingProperty() { return exerciseRatingProperty; }
  public StringProperty userNameProperty() { return userNameProperty; }
  public StringProperty userRatingProperty() { return userRatingProperty; }
  public StringProperty exerciseTimeProperty() { return exerciseTimeProperty; }
  public IntegerProperty numExercisesProperty() { return numExercisesProperty; }
  public IntegerProperty numPlyProperty() { return numPlyProperty; }
  public IntegerProperty currentExerciseProperty() { return currentExerciseProperty; }
  public IntegerProperty currentPlyProperty() { return currentPlyProperty; }

  // Setters for convenience
  public void setExerciseAvgTime(String value) { exerciseAvgTimeProperty.set(value); }
  public void setExerciseLevel(String value) { exerciseLevelProperty.set(value); }
  public void setExerciseRating(String value) { exerciseRatingProperty.set(value); }
  public void setUserName(String value) { userNameProperty.set(value); }
  public void setUserRating(String value) { userRatingProperty.set(value); }
  public void setExerciseTime(String value) { exerciseTimeProperty.set(value); }
  public void setNumExercises(int value) { numExercisesProperty.set(value); }
  public void setNumPly(int value) { numPlyProperty.set(value); }
  public void setCurrentExercise(int value) { currentExerciseProperty.set(value); }
  public void setCurrentPly(int value) { currentPlyProperty.set(value); }

  // Methods to update exercise states
  public void setExerciseState(int exerciseIndex, int state) {
    if (exerciseIndex >= 0 && exerciseIndex < exerciseStates.size()) {
      exerciseStates.set(exerciseIndex, state);
      updateExerciseRectangleColor(exerciseIndex);
    }
  }

  public void setPlyState(int plyIndex, int state) {
    if (plyIndex >= 0 && plyIndex < plyStates.size()) {
      plyStates.set(plyIndex, state);
      updatePlyRectangleColor(plyIndex);
    }
  }

  private void updateExerciseRectangles(int count) {
    // Clear existing rectangles
    hbExercises.getChildren().clear();
    exerciseRectangles.clear();
    exerciseStates.clear();

    // Create new rectangles
    for (int i = 0; i < count; i++) {
      Rectangle rect = createRectangle();
      exerciseRectangles.add(rect);
      exerciseStates.add(0); // Normal state
      hbExercises.getChildren().add(rect);
    }

    updateExerciseStrokes();
  }

  private void updatePlyRectangles(int count) {
    // Clear existing rectangles
    hbPly.getChildren().clear();
    plyRectangles.clear();
    plyStates.clear();

    // Create new rectangles
    for (int i = 0; i < count; i++) {
      Rectangle rect = createRectangle();
      plyRectangles.add(rect);
      plyStates.add(0); // Normal state
      hbPly.getChildren().add(rect);
    }

    updatePlyStrokes();
  }

  private Rectangle createRectangle() {
    Rectangle rect = new Rectangle(12, 12);
    rect.setArcHeight(5.0);
    rect.setArcWidth(5.0);
    rect.setFill(NORMAL_COLOR);
    rect.setStroke(INACTIVE_STROKE);
    rect.setStrokeWidth(1.0);
    return rect;
  }

  private void updateExerciseStrokes() {
    int current = currentExerciseProperty.get();
    for (int i = 0; i < exerciseRectangles.size(); i++) {
      Rectangle rect = exerciseRectangles.get(i);
      rect.setStroke(i == current ? ACTIVE_STROKE : INACTIVE_STROKE);
    }
  }

  private void updatePlyStrokes() {
    int current = currentPlyProperty.get();
    for (int i = 0; i < plyRectangles.size(); i++) {
      Rectangle rect = plyRectangles.get(i);
      rect.setStroke(i == current ? ACTIVE_STROKE : INACTIVE_STROKE);
    }
  }

  private void updateExerciseRectangleColor(int index) {
    if (index >= 0 && index < exerciseRectangles.size()) {
      Rectangle rect = exerciseRectangles.get(index);
      int state = exerciseStates.get(index);
      rect.setFill(getColorForState(state));
    }
  }

  private void updatePlyRectangleColor(int index) {
    if (index >= 0 && index < plyRectangles.size()) {
      Rectangle rect = plyRectangles.get(index);
      int state = plyStates.get(index);
      rect.setFill(getColorForState(state));
    }
  }

  private Color getColorForState(int state) {
    switch (state) {
      case 1: return OK_COLOR;
      case 2: return FAIL_COLOR;
      default: return NORMAL_COLOR;
    }
  }

  // Convenience methods for state constants
  public static final int STATE_NORMAL = 0;
  public static final int STATE_OK = 1;
  public static final int STATE_FAIL = 2;
}
