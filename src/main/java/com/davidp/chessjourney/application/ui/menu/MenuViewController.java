package com.davidp.chessjourney.application.ui.menu;

import com.davidp.chessjourney.application.config.GlobalEventBus;
import com.davidp.chessjourney.application.domain.*;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.ui.util.FXAnimationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MenuViewController implements ScreenController {

  @FXML private Pane rootPane;

  @FXML private ImageView imgSettings;

  @FXML private Pane pnlOptionAnalysisBoard;

  @FXML private Pane pnlOptionSettings;

  private ScreenController.ScreenStatus status;

  @FXML private Text txtSettings;

  @FXML private Text txtAnalysisBoard;

  @FXML private Text txtMemoryGame;

  @FXML private Pane pnlOptionMemoryGame;

  @FXML private Pane pnlOptionTacticGame;

  @FXML private Pane pnlOptionDefendGame;

  @FXML private Text txtDefendGame;

  @FXML private Text txtTacticGame;

  @FXML private Label lblDate;
  @FXML private Label lblWhiteTime;

  @FXML
  private Pane pnlOptionUser;

  @FXML
  private ImageView imgUser;


  @FXML
  private Text txtUser;


  @FXML
  private Text txtUserStats;


  @FXML
  private Pane pnlOptionUserStats;


  @FXML
  private ImageView imgUserStats;

  @FXML
  private Pane pnlOptionUserSuites;

  @FXML
  private Text txtUserSuites;

  @FXML
  private ImageView imgUserSuites;

  @FXML
  private Pane pnlOptionUserData;

  @FXML
  private Text txtUserData;

  @FXML
  private ImageView imgUserData;

  @FXML
  private Pane pnlOptionTournaments;

  @FXML
  private Text txtTournaments;

  @FXML
  private ImageView imgTournaments;

    private static final DateTimeFormatter SPANISH_FMT =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'del' yyyy", new Locale("es", "ES"));

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm", new Locale("es", "ES"));


    public void initialize() {

    status = ScreenController.ScreenStatus.INITIALIZED;
  }

  @Override
  public void setData(InputScreenData inputData) {

    if (inputData.isLayoutInfoValid()) {
        lblDate.setText(formatInstant(Instant.now()));
        lblWhiteTime.setText(formatTime(Instant.now()));
        setLayout(inputData.getLayoutX(), inputData.getLayoutY());
    }
  }

  @Override
  public void setLayout(double layoutX, double layoutY) {

    rootPane.setLayoutX(layoutX);
    rootPane.setLayoutY(layoutY);
  }

  @Override
  public void show() {

    FXAnimationUtil.fadeIn(rootPane, 0.2)
            .repeat(1)
            .autoReverse(false)
            .onFinished(() -> {

              rootPane.setVisible(true);
              rootPane.toFront();
            })
            .buildAndPlay();

  }

  public void show(InputScreenData inputData) {

    setData(inputData);
    status = ScreenController.ScreenStatus.VISIBLE;
    show();
  }

  @Override
  public void hide() {
    FXAnimationUtil.fadeIn(rootPane, 0.2)
            .repeat(1)
            .autoReverse(false)
            .onFinished(() -> {

              rootPane.setVisible(false);
              status = ScreenController.ScreenStatus.HIDDEN;
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

    return status == ScreenController.ScreenStatus.INITIALIZED;
  }

  @FXML
  void optionClicked(MouseEvent event) {

    if (isSettingMenuClicked(event)) {

      GlobalEventBus.get().post(new OpenSettingsFromMenuEvent());
    }
    if (isAnalysisBoardClicked(event)) {

      GlobalEventBus.get().post(new OpenAnalysisBoardEvent());
    }

    if (isMemoryGameClicked(event)) {
      GlobalEventBus.get().post(new OpenMemoryGameEvent());
    }

    if (isTacticGameClicked(event)) {
      GlobalEventBus.get().post(new OpenTacticGameEvent());
    }

    if (isTournamentsClicked(event)) {
      GlobalEventBus.get().post(new OpenTournamentsEvent());
    }

    if (isUserChangedClicked(event)){
      GlobalEventBus.get().post(new ChangeUserEvent());
    }
    if (isUserStatsClicked(event)){
      GlobalEventBus.get().post(new OpenUserStatsEvent());
    }
    if (isDefendGameClicked(event)){
      GlobalEventBus.get().post(new OpenDefendGameEvent());
    }
    if (isUserSuitesClicked(event)){
      GlobalEventBus.get().post(new OpenUserSuitesEvent());
    }
    if (isUserDataClicked(event)){
      GlobalEventBus.get().post(new OpenUserDataEvent());
    }
  }

  private boolean isDefendGameClicked(MouseEvent event) {

    return event.getSource() == pnlOptionDefendGame || event.getSource() == txtDefendGame;
  }

  private boolean isMemoryGameClicked(MouseEvent event) {

    return event.getSource() == pnlOptionMemoryGame || event.getSource() == txtMemoryGame;
  }

  private boolean isTacticGameClicked(MouseEvent event) {

    return event.getSource() == pnlOptionTacticGame || event.getSource() == txtTacticGame;
  }

  private boolean isAnalysisBoardClicked(MouseEvent event) {

    return event.getSource() == pnlOptionAnalysisBoard || event.getSource() == txtAnalysisBoard;
  }

  protected boolean isSettingMenuClicked(MouseEvent event) {

    return event.getSource() == pnlOptionSettings || event.getSource() == txtSettings;
  }

  protected boolean isUserChangedClicked(MouseEvent event) {

    return event.getSource() == pnlOptionUser || event.getSource() == txtUser;
  }

  protected  boolean isUserStatsClicked(MouseEvent event) {

    return event.getSource() == pnlOptionUserStats || event.getSource() == txtUserStats;
  }

  protected boolean isUserSuitesClicked(MouseEvent event) {
    return event.getSource() == pnlOptionUserSuites || event.getSource() == txtUserSuites;
  }

  protected boolean isUserDataClicked(MouseEvent event) {
    return event.getSource() == pnlOptionUserData || event.getSource() == txtUserData;
  }

  protected boolean isTournamentsClicked(MouseEvent event) {
    return event.getSource() == pnlOptionTournaments || event.getSource() == txtTournaments;
  }

    private String formatInstant(Instant instant) {
        String formatted = SPANISH_FMT.format(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()));
        return capitalize(formatted);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    // Añade este método:
    private String formatTime(Instant instant) {
        return TIME_FMT.format(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()));
    }
}
