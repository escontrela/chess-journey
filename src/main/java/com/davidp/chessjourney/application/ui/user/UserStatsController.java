package com.davidp.chessjourney.application.ui.user;

import com.almasb.fxgl.dsl.FXGL;
import com.davidp.chessjourney.application.config.AppProperties;
import com.davidp.chessjourney.application.config.GlobalEventBus;
import com.davidp.chessjourney.application.domain.*;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.board.PromoteViewInputScreenData;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.ui.util.FXAnimationUtil;
import com.davidp.chessjourney.application.usecases.GetUserByIdUseCase;
import com.davidp.chessjourney.application.usecases.GetUserStatsForLastNDaysUseCase;
import com.davidp.chessjourney.application.usecases.GetUsersUseCase;
import com.davidp.chessjourney.application.usecases.SaveActiveUserUseCase;
import com.davidp.chessjourney.domain.User;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.davidp.chessjourney.domain.common.AggregatedStats;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class UserStatsController implements ScreenController {


  @FXML
  private Button btClose;

  @FXML
  private Button btOptEasy;

  @FXML
  private Button btOptionMid;

  @FXML
  private Button btOptionDefend;

  @FXML
  private Button btOptionGuess;


  @FXML
  private ImageView imgClose;

  @FXML
  private Pane rootPane;

  private ScreenStatus status;

  private GetUserByIdUseCase getUserByIdUseCase;
  private GetUserStatsForLastNDaysUseCase getUserStatsForLastNDaysUseCase;

  @FXML
  private Label lblEloPlayer;

  @FXML
  private Label lblPlayer;

  @FXML
  private BarChart<String, Number> barCharUserStats;

  UserStatsInputScreenData userStatsInputScreenData;

  protected String difficulty = "easy";
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
   * 📊 Método para obtener y mostrar las estadísticas de usuario en el gráfico de barras.
   */
  private void displayUserStats(final Long userId,final String exerciseType) {

    if (userId == null || userId <= 0) {

      System.err.println("⚠️ Error: ID de usuario inválido.");
      return;
    }



    // 🔹 Configuración de parámetros
    UUID gameType = UUID.fromString("7ad9f7dd-1e9a-44b6-a8ad-1bb36fb53a38");

    if (Objects.equals(exerciseType, "memory_game")){

      gameType = UUID.fromString("7ad9f7dd-1e9a-44b6-a8ad-1bb36fb53a38");
    }

    if (Objects.equals(exerciseType, "defend_memory_game")){

      gameType = UUID.fromString("b8570288-21e1-4130-ad52-e88ac2444f94");
    }


    //TODO fix the difficulty levels, should be retrieved as a dynamic way from the database

    UUID difficultyLevel = UUID.fromString("cd343e6e-12d7-4c79-9519-c95dc0546b5e");

    if (Objects.equals(difficulty, "medium")){
      difficultyLevel = UUID.fromString("903ec9bd-aeb1-4b01-8fbd-a3ec2dce976f");
    }

    int lastNDays = 30; // 📆 Últimos 30 días

    // 🔍 Ejecutar el caso de uso para obtener los datos
    List<AggregatedStats> userStats = getUserStatsForLastNDaysUseCase.execute(userId, gameType, difficultyLevel, lastNDays);

    // 🛑 Limpiar datos previos en el gráfico
    barCharUserStats.getData().clear();

    // 🏷️ Crear una nueva serie de datos para el gráfico
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Éxito Promedio (%)");

    // 🔄 Formateador de fecha (día/mes)
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

    // 📊 Agregar cada dato a la serie
    for (AggregatedStats stat : userStats) {
      String formattedDate = stat.getDate().format(formatter);
      series.getData().add(new XYChart.Data<>(formattedDate, stat.getValue() * 100)); // Convertimos a %
    }

    // 🎯 Añadir la serie al gráfico
    barCharUserStats.getData().add(series);
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

    if (event.getSource() == btOptEasy){

      difficulty = "easy";
      displayUserStats(userStatsInputScreenData.getUserId(),exerciseType);

    }
    if (event.getSource() == btOptionMid){
      difficulty = "medium";
      displayUserStats(userStatsInputScreenData.getUserId(),exerciseType);

    }
  }

  public void setGetUserByIdUseCase(GetUserByIdUseCase getUserByIdUseCase) {

    this.getUserByIdUseCase = getUserByIdUseCase;
  }

  public void setGetUserStatsForLastNDaysUseCase(GetUserStatsForLastNDaysUseCase getUserStatsForLastNDaysUseCase) {
    this.getUserStatsForLastNDaysUseCase = getUserStatsForLastNDaysUseCase;
  }



}
