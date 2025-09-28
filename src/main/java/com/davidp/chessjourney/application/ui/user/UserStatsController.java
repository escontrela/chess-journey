package com.davidp.chessjourney.application.ui.user;

import com.davidp.chessjourney.application.service.DataStatsService;
import com.davidp.chessjourney.application.service.ExerciseService;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.ui.util.FXAnimationUtil;
import com.davidp.chessjourney.application.ui.controls.Chart2DController;
import com.davidp.chessjourney.application.usecases.GetUserByIdUseCase;
import com.davidp.chessjourney.application.usecases.GetUserStatsForLastNDaysUseCase;
import com.davidp.chessjourney.domain.User;

import java.util.*;

import com.davidp.chessjourney.domain.common.AggregatedStats;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import static com.davidp.chessjourney.application.usecases.GetUserStatsForLastNDaysUseCase.*;

public class UserStatsController implements ScreenController {

  enum StatsExercises {
    MEMORY_GAME("Guess Accuracy"),
    DEFEND_MEMORY_GAME("Defend Accuracy"),
    TACTIC_GAME("Tactics Accuracy"),
    ALL("All Exercises");

    final String exerciseName;

    StatsExercises(String exerciseName) {

      this.exerciseName = exerciseName;
    }

    public String getExerciseName() {

      return exerciseName;
    }
  }

  enum StatsDifficulty {
    EASY,
    MEDIUM,
    HARD
  }

  enum StatsGranularity {
    MONTH,
    YEAR
  }

  @FXML private Button btClose;

  @FXML private Button btOptEasy;

  @FXML private Button btOptTactics;

  @FXML private Button btOptAll;

  @FXML private Button btOptionMid;

  @FXML private Button btOptionDefend;

  @FXML private Button btOptionGuess;

  @FXML private Button btOptYear;

  @FXML private Button btOptMonth;

  @FXML private ImageView imgClose;

  @FXML private Pane rootPane;

  private ScreenStatus status;

  private GetUserByIdUseCase getUserByIdUseCase;
  private GetUserStatsForLastNDaysUseCase getUserStatsForLastNDaysUseCase;
  private ExerciseService exerciseService;
  private DataStatsService datastatsService;

  @FXML private Label lblEloPlayer;

  @FXML private Label lblPlayer;

  @FXML private Chart2DController chartUserStats;

  UserStatsInputScreenData userStatsInputScreenData;

  protected String difficulty = "easy";
  protected String granularity = "month";

  StatsExercises currentExerciseType = StatsExercises.MEMORY_GAME;
  StatsDifficulty currentDifficultyLevel = StatsDifficulty.EASY;
  StatsGranularity currentGranularity = StatsGranularity.MONTH;

  public void initialize() {

    status = ScreenStatus.INITIALIZED;

    btOptionGuess.setUserData(StatsExercises.MEMORY_GAME);
    btOptionDefend.setUserData(StatsExercises.DEFEND_MEMORY_GAME);
    btOptTactics.setUserData(StatsExercises.TACTIC_GAME);
    btOptAll.setUserData(StatsExercises.ALL);

    btOptEasy.setUserData(StatsDifficulty.EASY);
    btOptionMid.setUserData(StatsDifficulty.HARD);

    btOptYear.setUserData(StatsGranularity.YEAR);
    btOptMonth.setUserData(StatsGranularity.MONTH);

    // Inicializamos estilos de botones de ejercicios (All/Tactics/Guess)
    updateExerciseButtonsPressed(currentExerciseType);
  }

  @Override
  public void setData(InputScreenData inputData) {

    if (inputData.isLayoutInfoValid()) {

      setLayout(inputData.getLayoutX(), inputData.getLayoutY());
    }

    UserStatsInputScreenData userStatsInputScreenData = (UserStatsInputScreenData) inputData;
    this.userStatsInputScreenData = userStatsInputScreenData;

    displayUserData(userStatsInputScreenData.getUserId());
    displayUserStats(userStatsInputScreenData.getUserId());
  }

  private UUID getGameTypeId(StatsExercises exerciseType) {

    return switch (exerciseType) {
      case MEMORY_GAME -> exerciseService.getMemoryGameTypeId();
      case DEFEND_MEMORY_GAME -> exerciseService.getDefendGameTypeId();
      case TACTIC_GAME -> exerciseService.getTacticGameTypeId();
      default -> null;
    };
  }

  /**
   * Show user stats in the chart
   *
   * @param userId
   */
  private void displayUserStats(final Long userId) {

    UUID gameType = getGameTypeId(currentExerciseType);
    UUID difficultyLevel = getDifficultyLevelId(currentDifficultyLevel);

    // TODO Implement granularity handling
    StatsGranularity granularity = currentGranularity;

    int lastNDays = 31;

    chartUserStats.resetDataset();

    List<List<AggregatedStats>> datasets = new ArrayList<>();


    if (currentExerciseType == StatsExercises.ALL) {

        datasets.add(
          getUserStatsForLastNDaysUseCase.execute(
              userId,
              exerciseService.getMemoryGameTypeId(),
              difficultyLevel,
              lastNDays,
              Granularity.DAILY));

        datasets.add(
          getUserStatsForLastNDaysUseCase.execute(
              userId,
              exerciseService.getDefendGameTypeId(),
              difficultyLevel,
              lastNDays,
              Granularity.DAILY));

        datasets.add(
                getUserStatsForLastNDaysUseCase.execute(
                        userId,
                        exerciseService.getTacticGameTypeId(),
                        difficultyLevel,
                        lastNDays,
                        Granularity.DAILY));

    } else {

        datasets.add(
          getUserStatsForLastNDaysUseCase.execute(
              userId, gameType, difficultyLevel, lastNDays, Granularity.DAILY));
    }

    // Homogenization and alignment of datasets
    DataStatsService.ChartSeriesResult aligned =
        datastatsService.prepareAlignedSeries(datasets);

    List<String> dateLabels = aligned.labels();
    List<List<Double>> seriesValues = aligned.series();

    // Convertir cada serie a List<DataPoint2D> (X=index, Y=value)
    List<List<Chart2DController.DataPoint2D>> seriesDataPoints = new ArrayList<>();
    for (List<Double> serie : seriesValues) {
      List<Chart2DController.DataPoint2D> points = new ArrayList<>();
      for (int i = 0; i < serie.size(); i++) {
        points.add(new Chart2DController.DataPoint2D(i, serie.get(i)));
      }
      seriesDataPoints.add(points);
    }

    // Determinar nombres de series según contexto (uno o dos series)
    List<String> seriesNames;
    boolean hasSecond = datasets.size() > 1;
    if (hasSecond) {

      seriesNames =
          List.of(
              StatsExercises.MEMORY_GAME.getExerciseName(),
              StatsExercises.DEFEND_MEMORY_GAME.getExerciseName()
          , StatsExercises.TACTIC_GAME.getExerciseName());

    } else {

      seriesNames = List.of(currentExerciseType.getExerciseName());
    }

    // Aplicar al gráfico
    chartUserStats.setChartTitle(
        "Accuracy on focus exercises (" + currentExerciseType.getExerciseName() + ")");
    chartUserStats.setSeriesNames(seriesNames);
    chartUserStats.setDatasets(seriesDataPoints, dateLabels);
  }

  private UUID getDifficultyLevelId(StatsDifficulty currentDifficultyLevel) {
    return switch (currentDifficultyLevel) {
      case EASY -> exerciseService.getEasyLevelId();
      case HARD -> exerciseService.getHardLevelId();
      default -> exerciseService.getMediumLevelId();
    };
  }

  private void displayUserData(final Long userId) {

    User user = getUserByIdUseCase.execute(userId);

    lblPlayer.setText(user.getFirstname() + " " + user.getLastname());
    lblEloPlayer.setText(user.getInitials());
  }

  @Override
  public void setLayout(double layoutX, double layoutY) {

    rootPane.setLayoutX(layoutX);
    rootPane.setLayoutY(layoutY);
  }

  @Override
  public void show() {

    rootPane.setVisible(false);
    FXAnimationUtil.fadeIn(rootPane, 0.2)
        .repeat(1)
        .autoReverse(false)
        .onFinished(
            () -> {
              rootPane.setVisible(true);
              rootPane.toFront();
              status = ScreenStatus.HIDDEN;
            })
        .buildAndPlay();
  }

  @Override
  public void show(InputScreenData inputData) {

    setData(inputData);
    status = ScreenStatus.VISIBLE;
    show();
  }

  @Override
  public void hide() {

    FXAnimationUtil.fadeIn(rootPane, 0.2)
        .repeat(1)
        .autoReverse(false)
        .onFinished(
            () -> {
              rootPane.setVisible(false);
              status = ScreenStatus.HIDDEN;
            })
        .buildAndPlay();
  }

  @Override
  public boolean isVisible() {
    return rootPane.isVisible();
  }

  @Override
  public boolean isHidden() {
    return !rootPane.isVisible();
  }

  @Override
  public Pane getRootPane() {
    return rootPane;
  }

  @Override
  public ScreenStatus getStatus() {
    return null;
  }

  @Override
  public boolean isInitialized() {

    return status == ScreenStatus.INITIALIZED;
  }

  // Aplica estilos a los tres botones de tipo de ejercicio según el seleccionado
  private void updateExerciseButtonsPressed(StatsExercises selected) {

      boolean allPressed = selected == StatsExercises.ALL;
    boolean tacticsPressed = selected == StatsExercises.TACTIC_GAME;
    boolean guessPressed = selected == StatsExercises.MEMORY_GAME;
    boolean defendPressed = selected == StatsExercises.DEFEND_MEMORY_GAME;

    applyButtonStyle(btOptAll, allPressed);
    applyButtonStyle(btOptTactics, tacticsPressed);
    applyButtonStyle(btOptionGuess, guessPressed);
    applyButtonStyle(btOptionDefend, defendPressed);
  }

  private void applyButtonStyle(Button button, boolean pressed) {

    if (button == null) return;
    List<String> classes = button.getStyleClass();
    classes.remove("button-regular");
    classes.remove("button-regular-pressed");
    classes.add(pressed ? "button-regular-pressed" : "button-regular");
  }

  @FXML
  void buttonAction(ActionEvent event) {

    if (event.getSource() == btClose) {

      rootPane.setVisible(false);
    }

    if (event.getSource() == btOptionGuess) {

      currentExerciseType = (StatsExercises) btOptionGuess.getUserData();
      updateExerciseButtonsPressed(currentExerciseType);
      displayUserStats(userStatsInputScreenData.getUserId());
    }

    if (event.getSource() == btOptionDefend) {

      currentExerciseType = (StatsExercises) btOptionDefend.getUserData();
      updateExerciseButtonsPressed(currentExerciseType); // dejar All/Tactics/Guess en estado coherente
      displayUserStats(userStatsInputScreenData.getUserId());
    }

    if (event.getSource() == btOptTactics) {

      currentExerciseType = (StatsExercises) btOptTactics.getUserData();
      updateExerciseButtonsPressed(currentExerciseType);
      displayUserStats(userStatsInputScreenData.getUserId());
    }

    if (event.getSource() == btOptAll) {

      currentExerciseType = (StatsExercises) btOptAll.getUserData();
      updateExerciseButtonsPressed(currentExerciseType);
      displayUserStats(userStatsInputScreenData.getUserId());
    }

    if (event.getSource() == btOptEasy) {

      currentDifficultyLevel = (StatsDifficulty) btOptEasy.getUserData();
      displayUserStats(userStatsInputScreenData.getUserId());
    }

    if (event.getSource() == btOptionMid) {
      currentDifficultyLevel = (StatsDifficulty) btOptionMid.getUserData();

      displayUserStats(userStatsInputScreenData.getUserId());
    }

    if (event.getSource() == btOptYear) {

      currentGranularity = (StatsGranularity) btOptYear.getUserData();
      displayUserStats(userStatsInputScreenData.getUserId());
    }

    if (event.getSource() == btOptMonth) {

      currentGranularity = (StatsGranularity) btOptMonth.getUserData();
      displayUserStats(userStatsInputScreenData.getUserId());
    }
  }

  /*
   * USE CASES AND SERVICES INJECTION
   */
  public void setGetUserByIdUseCase(GetUserByIdUseCase getUserByIdUseCase) {

    this.getUserByIdUseCase = getUserByIdUseCase;
  }

  public void setGetUserStatsForLastNDaysUseCase(
      GetUserStatsForLastNDaysUseCase getUserStatsForLastNDaysUseCase) {

    this.getUserStatsForLastNDaysUseCase = getUserStatsForLastNDaysUseCase;
  }

  public void setExerciseService(ExerciseService exerciseService) {

    this.exerciseService = exerciseService;
  }

  public void setDataStatsService(DataStatsService dataStatsService) {

    this.datastatsService = dataStatsService;
  }
}
