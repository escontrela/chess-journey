package com.davidp.chessjourney.application.ui.user;

import com.davidp.chessjourney.application.service.DataStatsService;
import com.davidp.chessjourney.application.service.ExerciseService;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.ui.util.FXAnimationUtil;
import com.davidp.chessjourney.application.ui.controls.Chart2DController;
import com.davidp.chessjourney.application.usecases.GetUserByIdUseCase;
import com.davidp.chessjourney.application.usecases.userstats.GetUserMetricTimeSeriesDatasetUseCase;
import com.davidp.chessjourney.application.usecases.userstats.GetUserMetricTimeSeriesDatasetRequest;
import com.davidp.chessjourney.domain.User;
import com.davidp.chessjourney.domain.common.stats.TimeSeriesDataset;
import com.davidp.chessjourney.domain.common.stats.TimeSeries;
import com.davidp.chessjourney.domain.common.stats.TimeSeriesPoint;
import com.davidp.chessjourney.domain.userstats.UserMetric;
import com.davidp.chessjourney.domain.userstats.StatsAggregationLevel;

import java.time.LocalDate;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class UserStatsController implements ScreenController {

  enum StatsExercises {
    MEMORY_GAME("Guess"),
    DEFEND_MEMORY_GAME("Defend"),
    TACTIC_GAME("Tactic"),
    ALL("All");

    final String exerciseName;

    StatsExercises(String exerciseName) {

      this.exerciseName = exerciseName;
    }

    public String getExerciseName() {

      return exerciseName;
    }
  }

  enum StatsDifficulty {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard");

    final String displayName;

    StatsDifficulty(String displayName) {
      this.displayName = displayName;
    }

    public String getDisplayName() {
      return displayName;
    }

    @Override
    public String toString() {
      return displayName;
    }
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

  @FXML private Button btFilter;

  @FXML private ImageView imgClose;

  @FXML private Pane rootPane;

  // New filter controls
  @FXML private DatePicker datePickerFrom;
  @FXML private DatePicker datePickerTo;
  @FXML private ComboBox<StatsAggregationLevel> cbAggregation;
  @FXML private ComboBox<StatsExercises> cbGameType;
  @FXML private ComboBox<StatsDifficulty> cbDifficulty;

  // Three chart controllers
  @FXML private Chart2DController chartAccuracy;
  @FXML private Chart2DController chartAvgTime;
  @FXML private Chart2DController chartVolume;

  // Legacy chart (kept for backward compatibility)
  @FXML private Chart2DController chartUserStats;

  private ScreenStatus status;

  private GetUserByIdUseCase getUserByIdUseCase;
  private GetUserMetricTimeSeriesDatasetUseCase getUserMetricTimeSeriesDatasetUseCase;
  private ExerciseService exerciseService;
  private DataStatsService datastatsService;

  @FXML private Label lblEloPlayer;

  @FXML private Label lblPlayer;

  UserStatsInputScreenData userStatsInputScreenData;

  StatsExercises currentExerciseType = StatsExercises.ALL;
  StatsDifficulty currentDifficultyLevel = StatsDifficulty.EASY;
  StatsAggregationLevel currentAggregationLevel = StatsAggregationLevel.MONTHLY;

  public void initialize() {

    status = ScreenStatus.INITIALIZED;

    btOptionGuess.setUserData(StatsExercises.MEMORY_GAME);
    btOptionDefend.setUserData(StatsExercises.DEFEND_MEMORY_GAME);
    btOptTactics.setUserData(StatsExercises.TACTIC_GAME);
    btOptAll.setUserData(StatsExercises.ALL);

    btOptEasy.setUserData(StatsDifficulty.EASY);
    // Legacy: btOptionMid was incorrectly mapped to HARD in original code, kept for backward compatibility
    btOptionMid.setUserData(StatsDifficulty.HARD);

    // Initialize date pickers with defaults (last 30 days)
    datePickerTo.setValue(LocalDate.now());
    datePickerFrom.setValue(LocalDate.now().minusDays(30));

    // Initialize aggregation level combo
    cbAggregation.setItems(FXCollections.observableArrayList(StatsAggregationLevel.values()));
    cbAggregation.setValue(StatsAggregationLevel.MONTHLY);

    // Initialize game type combo
    cbGameType.setItems(FXCollections.observableArrayList(StatsExercises.values()));
    cbGameType.setValue(StatsExercises.ALL);

    // Initialize difficulty combo
    cbDifficulty.setItems(FXCollections.observableArrayList(StatsDifficulty.values()));
    cbDifficulty.setValue(StatsDifficulty.EASY);

    // Initialize button styles
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
    displayAllCharts(userStatsInputScreenData.getUserId());
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
   * Display all three charts (Accuracy, Avg Time, Volume)
   */
  private void displayAllCharts(final Long userId) {
    LocalDate fromDate = datePickerFrom.getValue();
    LocalDate toDate = datePickerTo.getValue();
    StatsAggregationLevel aggregationLevel = cbAggregation.getValue();
    StatsExercises gameType = cbGameType.getValue();
    StatsDifficulty difficulty = cbDifficulty.getValue();

    // Build list of exercise type IDs based on selection
    List<UUID> selectedExerciseTypeIds = getSelectedExerciseTypeIds(gameType);
    UUID selectedDifficultyId = getDifficultyLevelId(difficulty);

    // Update current state
    currentExerciseType = gameType;
    currentDifficultyLevel = difficulty;
    currentAggregationLevel = aggregationLevel;

    // Display Accuracy chart
    displayChart(
        chartAccuracy,
        "Accuracy (%)",
        userId,
        UserMetric.ACCURACY,
        selectedExerciseTypeIds,
        selectedDifficultyId,
        fromDate,
        toDate,
        aggregationLevel
    );

    // Display Avg Solve Time chart
    displayChart(
        chartAvgTime,
        "Avg Solve Time (s)",
        userId,
        UserMetric.AVG_SOLVE_TIME_SUCCESS,
        selectedExerciseTypeIds,
        selectedDifficultyId,
        fromDate,
        toDate,
        aggregationLevel
    );

    // Display Volume chart
    displayChart(
        chartVolume,
        "Training Volume",
        userId,
        UserMetric.TRAINING_VOLUME,
        selectedExerciseTypeIds,
        selectedDifficultyId,
        fromDate,
        toDate,
        aggregationLevel
    );
  }

  private List<UUID> getSelectedExerciseTypeIds(StatsExercises exerciseType) {
    if (exerciseType == StatsExercises.ALL) {
      // Return all exercise types for multiple series
      return List.of(
          exerciseService.getMemoryGameTypeId(),
          exerciseService.getDefendGameTypeId(),
          exerciseService.getTacticGameTypeId()
      );
    } else {
      UUID typeId = getGameTypeId(exerciseType);
      return typeId != null ? List.of(typeId) : List.of();
    }
  }

  /**
   * Display a single chart with the given metric
   */
  private void displayChart(
      Chart2DController chart,
      String title,
      Long userId,
      UserMetric metric,
      List<UUID> exerciseTypeIds,
      UUID difficultyId,
      LocalDate fromDate,
      LocalDate toDate,
      StatsAggregationLevel aggregationLevel
  ) {
    chart.resetDataset();

    GetUserMetricTimeSeriesDatasetRequest request = new GetUserMetricTimeSeriesDatasetRequest(
        userId,
        metric,
        exerciseTypeIds,
        Optional.ofNullable(difficultyId),
        fromDate,
        toDate,
        aggregationLevel
    );

    TimeSeriesDataset dataset = getUserMetricTimeSeriesDatasetUseCase.execute(request);

    // Convert TimeSeriesDataset to chart data
    ChartData chartData = convertToChartData(dataset);

    chart.setChartTitle(title);
    chart.setSeriesNames(chartData.seriesNames);
    chart.setDatasets(chartData.dataPoints, chartData.labels);
  }

  /**
   * Convert TimeSeriesDataset to chart-compatible data
   */
  private ChartData convertToChartData(TimeSeriesDataset dataset) {
    if (dataset == null || dataset.isEmpty()) {
      return new ChartData(List.of(), List.of(), List.of());
    }

    List<TimeSeries> seriesList = dataset.getSeries();
    List<String> seriesNames = new ArrayList<>();
    List<List<Chart2DController.DataPoint2D>> allDataPoints = new ArrayList<>();

    // Collect all unique labels (periods) and align series
    Set<String> allLabelsSet = new LinkedHashSet<>();
    Map<String, Map<String, Double>> seriesDataByLabel = new LinkedHashMap<>();

    for (TimeSeries series : seriesList) {
      String seriesName = getSeriesDisplayName(series.getName());
      seriesNames.add(seriesName);

      for (TimeSeriesPoint point : series.getPoints()) {
        String label = point.getPeriod().getLabel();
        allLabelsSet.add(label);

        seriesDataByLabel
            .computeIfAbsent(seriesName, k -> new LinkedHashMap<>())
            .put(label, point.getValue());
      }
    }

    List<String> labels = new ArrayList<>(allLabelsSet);

    // Create data points for each series
    for (String seriesName : seriesNames) {
      List<Chart2DController.DataPoint2D> points = new ArrayList<>();
      Map<String, Double> dataByLabel = seriesDataByLabel.getOrDefault(seriesName, Map.of());

      for (int i = 0; i < labels.size(); i++) {
        String label = labels.get(i);
        double value = dataByLabel.getOrDefault(label, 0.0);
        points.add(new Chart2DController.DataPoint2D(i, value));
      }
      allDataPoints.add(points);
    }

    return new ChartData(seriesNames, allDataPoints, labels);
  }

  /**
   * Convert series name to display name
   */
  private String getSeriesDisplayName(String seriesName) {
    if (seriesName == null) return "Unknown";

    // Check if it's a Type-UUID format and convert to friendly name
    if (seriesName.startsWith("Type-")) {
      String uuidStr = seriesName.substring(5);
      try {
        UUID typeId = UUID.fromString(uuidStr);
        if (typeId.equals(exerciseService.getMemoryGameTypeId())) {
          return StatsExercises.MEMORY_GAME.getExerciseName();
        } else if (typeId.equals(exerciseService.getDefendGameTypeId())) {
          return StatsExercises.DEFEND_MEMORY_GAME.getExerciseName();
        } else if (typeId.equals(exerciseService.getTacticGameTypeId())) {
          return StatsExercises.TACTIC_GAME.getExerciseName();
        }
      } catch (IllegalArgumentException e) {
        // Not a valid UUID, return original
      }
    }
    return seriesName;
  }

  private record ChartData(
      List<String> seriesNames,
      List<List<Chart2DController.DataPoint2D>> dataPoints,
      List<String> labels
  ) {}

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

  // Apply styles to exercise type buttons based on selection
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

    if (event.getSource() == btFilter) {
      // Refresh all charts with current filter values
      if (userStatsInputScreenData != null) {
        displayAllCharts(userStatsInputScreenData.getUserId());
      }
    }

    if (event.getSource() == btOptionGuess) {
      cbGameType.setValue(StatsExercises.MEMORY_GAME);
      currentExerciseType = StatsExercises.MEMORY_GAME;
      updateExerciseButtonsPressed(currentExerciseType);
      if (userStatsInputScreenData != null) {
        displayAllCharts(userStatsInputScreenData.getUserId());
      }
    }

    if (event.getSource() == btOptionDefend) {
      cbGameType.setValue(StatsExercises.DEFEND_MEMORY_GAME);
      currentExerciseType = StatsExercises.DEFEND_MEMORY_GAME;
      updateExerciseButtonsPressed(currentExerciseType);
      if (userStatsInputScreenData != null) {
        displayAllCharts(userStatsInputScreenData.getUserId());
      }
    }

    if (event.getSource() == btOptTactics) {
      cbGameType.setValue(StatsExercises.TACTIC_GAME);
      currentExerciseType = StatsExercises.TACTIC_GAME;
      updateExerciseButtonsPressed(currentExerciseType);
      if (userStatsInputScreenData != null) {
        displayAllCharts(userStatsInputScreenData.getUserId());
      }
    }

    if (event.getSource() == btOptAll) {
      cbGameType.setValue(StatsExercises.ALL);
      currentExerciseType = StatsExercises.ALL;
      updateExerciseButtonsPressed(currentExerciseType);
      if (userStatsInputScreenData != null) {
        displayAllCharts(userStatsInputScreenData.getUserId());
      }
    }

    if (event.getSource() == btOptEasy) {
      cbDifficulty.setValue(StatsDifficulty.EASY);
      currentDifficultyLevel = StatsDifficulty.EASY;
      if (userStatsInputScreenData != null) {
        displayAllCharts(userStatsInputScreenData.getUserId());
      }
    }

    if (event.getSource() == btOptionMid) {
      cbDifficulty.setValue(StatsDifficulty.HARD);
      currentDifficultyLevel = StatsDifficulty.HARD;
      if (userStatsInputScreenData != null) {
        displayAllCharts(userStatsInputScreenData.getUserId());
      }
    }
  }

  /*
   * USE CASES AND SERVICES INJECTION
   */
  public void setGetUserByIdUseCase(GetUserByIdUseCase getUserByIdUseCase) {

    this.getUserByIdUseCase = getUserByIdUseCase;
  }

  public void setGetUserMetricTimeSeriesDatasetUseCase(
      GetUserMetricTimeSeriesDatasetUseCase getUserMetricTimeSeriesDatasetUseCase) {

    this.getUserMetricTimeSeriesDatasetUseCase = getUserMetricTimeSeriesDatasetUseCase;
  }

  public void setExerciseService(ExerciseService exerciseService) {

    this.exerciseService = exerciseService;
  }

  public void setDataStatsService(DataStatsService dataStatsService) {

    this.datastatsService = dataStatsService;
  }
}
