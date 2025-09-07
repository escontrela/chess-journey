package com.davidp.chessjourney.application.ui.menu;

import com.davidp.chessjourney.application.config.GlobalEventBus;
import com.davidp.chessjourney.application.domain.*;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.ui.util.FXAnimationUtil;
import com.davidp.chessjourney.application.util.JavaFXSchedulerUtil;
import com.davidp.chessjourney.application.usecases.GetNextTournamentUseCase;
import com.davidp.chessjourney.domain.Tournament;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

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

  @FXML private Pane pnlOptionUser;

  @FXML private ImageView imgUser;

  @FXML private Text txtUser;

  @FXML private Text txtUserStats;

  @FXML private Pane pnlOptionUserStats;

  @FXML private ImageView imgUserStats;

  @FXML private Pane pnlOptionUserSuites;

  @FXML private Text txtUserSuites;

  @FXML private ImageView imgUserSuites;

  @FXML private Pane pnlOptionUserData;

  @FXML private Text txtUserData;

  @FXML private ImageView imgUserData;

  @FXML private Pane pnlOptionTournaments;

  @FXML private Text txtTournaments;

  private Timeline timer;

  @FXML private Label lblTitle;

  @FXML private ImageView imgRightPiece;

  @FXML private ImageView imgTournaments;

  @FXML private Label lblNextTournament;

  private GetNextTournamentUseCase getNextTournamentUseCase;


    @FXML
    private Button btRight;
    @FXML
    private ImageView imgRight;


    private static final DateTimeFormatter SPANISH_FMT =
      DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'del' yyyy", Locale.forLanguageTag("es-ES"));

  private static final DateTimeFormatter TIME_FMT =
      DateTimeFormatter.ofPattern("HH:mm:ss", Locale.forLanguageTag("es-ES"));

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

  public void startTimer() {

    if (timer != null) {

      timer.play();
      return;
    }
    timer =
        new Timeline(
            new KeyFrame(
                javafx.util.Duration.seconds(1),
                e -> {
                  lblDate.setText(formatInstant(Instant.now()));
                  lblWhiteTime.setText(formatTime(Instant.now()));
                }));
    timer.setCycleCount(Timeline.INDEFINITE);
    timer.play();
  }

  public void stopTimer() {

    if (timer != null) {
      timer.stop();
    }
  }

  private void loadNextTournament() {
    if (getNextTournamentUseCase != null && lblNextTournament != null) {
      // Load tournament asynchronously to avoid blocking UI
      JavaFXSchedulerUtil.runOnce(
          () -> {
            try {
              Tournament nextTournament = getNextTournamentUseCase.execute();
              if (nextTournament != null) {
                String tournamentText = formatTournamentText(nextTournament);
                // Use typewriter effect for the tournament text
                JavaFXSchedulerUtil.runOnce(
                    () -> playTypeWriterEffect(tournamentText, lblNextTournament, 0.05),
                    javafx.util.Duration.seconds(0.5) // Small delay for the typewriter effect
                );
              } else {
                lblNextTournament.setText(""); // Hide if no tournament
              }
            } catch (Exception e) {
              System.err.println("Error loading next tournament: " + e.getMessage());
              lblNextTournament.setText(""); // Hide on error
            }
          },
          javafx.util.Duration.seconds(1.5) // Delay to start after main title
      );
    }
  }

  private String formatTournamentText(Tournament tournament) {
    if (tournament == null) return "";
    
    String formattedDate = tournament.getInicio().format(
        java.time.format.DateTimeFormatter.ofPattern("d 'de' MMMM", 
        java.util.Locale.forLanguageTag("es-ES"))
    );
    
    return String.format("Próximo torneo: %s - %s (%s)", 
        tournament.getTorneo(), 
        formattedDate,
        tournament.getConcejo());
  }

  @Override
  public void show() {

    // Set a random right piece image each time the view is shown
    setRandomRightPieceImage();

    // Load and display next tournament
    loadNextTournament();

    FXAnimationUtil.fadeIn(rootPane, 0.2)
        .repeat(1)
        .autoReverse(false)
        .onFinished(
            () -> {
              rootPane.setVisible(true);
              rootPane.toFront();
              playTypeWriterEffect("Chess Journey", lblTitle, 0.02);
            })
        .buildAndPlay();
  }

  public void show(InputScreenData inputData) {

    setData(inputData);
    status = ScreenController.ScreenStatus.VISIBLE;
    show();
    startTimer();
  }

  @Override
  public void hide() {
    FXAnimationUtil.fadeIn(rootPane, 0.2)
        .repeat(1)
        .autoReverse(false)
        .onFinished(
            () -> {
              rootPane.setVisible(false);
              status = ScreenController.ScreenStatus.HIDDEN;
              stopTimer();
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

  public void setGetNextTournamentUseCase(GetNextTournamentUseCase getNextTournamentUseCase) {
    this.getNextTournamentUseCase = getNextTournamentUseCase;
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

    if (isUserChangedClicked(event)) {
      GlobalEventBus.get().post(new ChangeUserEvent());
    }
    if (isUserStatsClicked(event)) {
      GlobalEventBus.get().post(new OpenUserStatsEvent());
    }
    if (isDefendGameClicked(event)) {
      GlobalEventBus.get().post(new OpenDefendGameEvent());
    }
    if (isUserSuitesClicked(event)) {
      GlobalEventBus.get().post(new OpenUserSuitesEvent());
    }
    if (isUserDataClicked(event)) {
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

  protected boolean isUserStatsClicked(MouseEvent event) {

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

    if (s == null || s.isEmpty()) {

      return s;
    }
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }

  private String formatTime(Instant instant) {

    return TIME_FMT.format(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()));
  }

  // new helper: set a random image from /com/davidp/chessjourney/images/chess-creativity-1..7.png
  private void setRandomRightPieceImage() {
    if (imgRightPiece == null) {
      return;
    }
    int n = ThreadLocalRandom.current().nextInt(1, 8); // 1..7 inclusive
    String path = "/com/davidp/chessjourney/images/chess-creativity-" + n + ".png";
    try {
      java.io.InputStream is = getClass().getResourceAsStream(path);
      if (is != null) {
        Image img = new Image(is);
        imgRightPiece.setImage(img);
      } else {
        // fallback: clear image if resource missing
        imgRightPiece.setImage(null);
        System.out.println("Resource not found: " + path);
      }
    } catch (Exception ex) {
      imgRightPiece.setImage(null);
      System.err.println("Failed to load image " + path + ": " + ex.getMessage());
    }
  }

  private void playTypeWriterEffect(String text, Label textNode, double charInterval) {

    textNode.setText("");
    if (text == null || text.isEmpty()) return;

    StringBuilder currentText = new StringBuilder();

    currentText.append(text.charAt(0));
    textNode.setText(currentText.toString());

    for (int i = 1; i < text.length(); i++) {
      int index = i;
      JavaFXSchedulerUtil.runOnce(
          () -> {
            currentText.append(text.charAt(index));
            textNode.setText(currentText.toString());
          },
          javafx.util.Duration.seconds(index * charInterval));
    }
  }

  @FXML
  void buttonAction(ActionEvent event) {

    if (isButtonCloseClicked(event)) {

      hide();
      return;
    }
}

    private boolean isButtonCloseClicked(ActionEvent event) {

        return event.getSource() == btRight || event.getSource() == imgRight;
    }
    }
