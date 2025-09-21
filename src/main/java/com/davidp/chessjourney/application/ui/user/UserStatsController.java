package com.davidp.chessjourney.application.ui.user;

import com.davidp.chessjourney.application.domain.*;
import com.davidp.chessjourney.application.service.DataStatsService;
import com.davidp.chessjourney.application.service.ExerciseService;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.ui.util.FXAnimationUtil;
import com.davidp.chessjourney.application.ui.controls.Chart2DController;
import com.davidp.chessjourney.application.usecases.GetUserByIdUseCase;
import com.davidp.chessjourney.application.usecases.GetUserStatsForLastNDaysUseCase;
import com.davidp.chessjourney.domain.User;

import java.time.format.DateTimeFormatter;
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


  @FXML
  private Button btClose;

  @FXML
  private Button btOptEasy;

  @FXML
  private Button btOptTatics;

  @FXML
  private Button btOptAll;

  @FXML
  private Button btOptionMid;

  @FXML
  private Button btOptionDefend;

  @FXML
  private Button btOptionGuess;

  @FXML
  private Button btOptYear;

  @FXML
  private Button btOptMonth;

  @FXML
  private ImageView imgClose;

  @FXML
  private Pane rootPane;

  private ScreenStatus status;

  private GetUserByIdUseCase getUserByIdUseCase;
  private GetUserStatsForLastNDaysUseCase getUserStatsForLastNDaysUseCase;
  private ExerciseService exerciseService;
  private DataStatsService datastatsService;

  @FXML
  private Label lblEloPlayer;

  @FXML
  private Label lblPlayer;

  @FXML
  private Chart2DController chartUserStats;

  UserStatsInputScreenData userStatsInputScreenData;

    protected String difficulty = "easy";
    protected String granularity = "month";
    protected String exerciseType = "memory_game";

    public void initialize() {

    status = ScreenStatus.INITIALIZED;
  }

  @Override
  public void setData(InputScreenData inputData) {

    if (inputData.isLayoutInfoValid()) {

      setLayout(inputData.getLayoutX(), inputData.getLayoutY());
    }

    UserStatsInputScreenData userStatsInputScreenData = (UserStatsInputScreenData) inputData;
    this.userStatsInputScreenData = userStatsInputScreenData;

    displayUserData(userStatsInputScreenData.getUserId());
    displayUserStats(userStatsInputScreenData.getUserId(),exerciseType);
  }

    /**
     * Show user stats in the chart
     * @param userId
     * @param exerciseType
     */
  private void displayUserStats(final Long userId,final String exerciseType) {

    if (userId == null || userId <= 0) {

      System.err.println(" Error. Invalid user ID: " + userId);
      return;
    }

    UUID gameType = exerciseService.getMemoryGameTypeId();

    if (Objects.equals(exerciseType, "memory_game")){

      gameType = exerciseService.getMemoryGameTypeId();
    }

    if (Objects.equals(exerciseType, "defend_memory_game")){

      gameType = exerciseService.getDefendGameTypeId();
    }

    if (Objects.equals(exerciseType, "tactic_game")){

          gameType = exerciseService.getTacticGameTypeId();
    }

    UUID difficultyLevel = exerciseService.getEasyLevelId();

    if (Objects.equals(difficulty, "medium")){

      difficultyLevel = exerciseService.getMediumLevelId();
    }

      if (Objects.equals(difficulty, "hard")){

          difficultyLevel = exerciseService.getHardLevelId();
      }

      if (Objects.equals(granularity, "month")){

          //TODO invoke another use case to get stats by month

      }
      if (Objects.equals(granularity, "year")){

            //TODO invoke another use case to get stats by year
      }

    int lastNDays = 31;

    chartUserStats.resetDataset();

    List<AggregatedStats> dataset1 = null;
    List<AggregatedStats> dataset2 = null;

    if (Objects.equals(difficulty, "all")){

        dataset1 = getUserStatsForLastNDaysUseCase.execute(userId, exerciseService.getMemoryGameTypeId(), difficultyLevel, lastNDays, Granularity.DAILY);
        dataset2 = getUserStatsForLastNDaysUseCase.execute(userId, exerciseService.getDefendGameTypeId(), difficultyLevel, lastNDays, Granularity.DAILY);

    }else{

        dataset1 = getUserStatsForLastNDaysUseCase.execute(userId, gameType, difficultyLevel, lastNDays, Granularity.DAILY);
    }

{
    // Homogenization and alignment of datasets
    DataStatsService.ChartSeriesResult aligned = datastatsService.prepareAlignedSeries(dataset1, dataset2);

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
    boolean hasSecond = dataset2 != null && !dataset2.isEmpty();
    if (hasSecond) {
        seriesNames = List.of("Guess Accuracy", "Defend Accuracy");
    } else {
        if (Objects.equals(exerciseType, "memory_game")) {
            seriesNames = List.of("Guess Accuracy");
        } else if (Objects.equals(exerciseType, "defend_memory_game")) {
            seriesNames = List.of("Defend Accuracy");
        } else if (Objects.equals(exerciseType, "tactic_game")) {
            seriesNames = List.of("Tactics Accuracy");
        } else {
            seriesNames = List.of("Accuracy");
        }
    }

    // Aplicar al gráfico
    chartUserStats.setChartTitle("Accuracy on focus exercises");
    chartUserStats.setSeriesNames(seriesNames);
    chartUserStats.setDatasets(seriesDataPoints, dateLabels);
}

  }

  private void displayUserData(final Long userId) {

    User user =  getUserByIdUseCase.execute(userId);

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
            .onFinished(() -> {
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
            .onFinished(() -> {
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



  @FXML
  void buttonAction(ActionEvent event) {

    if (event.getSource() == btClose) {

      rootPane.setVisible(false);
    }
    if (event.getSource() == btOptionGuess){

      exerciseType = "memory_game";
      displayUserStats(userStatsInputScreenData.getUserId(),exerciseType);

    }

    if (event.getSource() == btOptionDefend){

      exerciseType = "defend_memory_game";
      displayUserStats(userStatsInputScreenData.getUserId(),exerciseType);
    }

    if (event.getSource() == btOptTatics){

      exerciseType = "tactic_game";
      displayUserStats(userStatsInputScreenData.getUserId(),exerciseType);
    }

    if (event.getSource() == btOptEasy){

      difficulty = "easy";
      displayUserStats(userStatsInputScreenData.getUserId(),exerciseType);

    }
    if (event.getSource() == btOptAll){

      difficulty = "all";
      displayUserStats(userStatsInputScreenData.getUserId(),exerciseType);

    }

    if (event.getSource() == btOptionMid){
      difficulty = "hard";
      displayUserStats(userStatsInputScreenData.getUserId(),exerciseType);

    }

      if (event.getSource() == btOptYear){
          granularity = "year";
          displayUserStats(userStatsInputScreenData.getUserId(),exerciseType);
      }

      if (event.getSource() == btOptMonth){
          granularity = "month";
          displayUserStats(userStatsInputScreenData.getUserId(),exerciseType);
      }
  }

  public void setGetUserByIdUseCase(GetUserByIdUseCase getUserByIdUseCase) {

    this.getUserByIdUseCase = getUserByIdUseCase;
  }

  public void setGetUserStatsForLastNDaysUseCase(GetUserStatsForLastNDaysUseCase getUserStatsForLastNDaysUseCase) {

      this.getUserStatsForLastNDaysUseCase = getUserStatsForLastNDaysUseCase;
  }

    public void setExerciseService(ExerciseService exerciseService) {

        this.exerciseService = exerciseService;
    }
    public void setDataStatsService(DataStatsService dataStatsService) {
        this.datastatsService = dataStatsService;
    }



}
