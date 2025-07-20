package com.davidp.chessjourney.application.ui.main;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;
import static javafx.application.Platform.runLater;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.davidp.chessjourney.application.config.AppProperties;
import com.davidp.chessjourney.application.config.GlobalEventBus;
import com.davidp.chessjourney.application.domain.*;
import com.davidp.chessjourney.application.factories.ScreenFactory;
import com.davidp.chessjourney.application.factories.ScreenFactory.Screens;
import com.davidp.chessjourney.application.factories.UseCaseFactory;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.ui.settings.SettingsViewInputScreenData;
import com.davidp.chessjourney.application.ui.user.UserStatsInputScreenData;
import com.davidp.chessjourney.application.usecases.GetAllTagsUseCase;
import com.davidp.chessjourney.application.usecases.GetUserByIdUseCase;
import com.davidp.chessjourney.domain.User;
import com.davidp.chessjourney.domain.common.Tag;
import com.google.common.eventbus.Subscribe;
import java.awt.*;
import java.util.List;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * This class is responsible for managing the main scene of the application, the main scene controls
 * all the features and windows dynamics of the application It is responsible for managing the main
 * menu, the settings menu, the board, and the game.
 */
public class MainSceneController implements ScreenController {

  @FXML private Button btClose;

  @FXML private Button btLeft;

  @FXML private Button btRight;

  @FXML private Button btSettings;

  @FXML private Label lbUserInitials;

  @FXML private Pane mainPane;

  @FXML private Pane pnlMessage;

  @FXML private StackPane pnlMenu;

  @FXML private ImageView imgClose;

  @FXML private ImageView imgSettings;

  @FXML private ImageView imgLogo;


  @FXML
  private Text lblPractice;

  @FXML
  private Text lblChessboard;


  // Variables para guardar la posición (offset) dentro de la ventana al pulsar el ratón
  private double xOffset = 0;
  private double yOffset = 0;

  // This map is used to cache the screens that are created.
  private final Map<Screens, ScreenController> screenManager = new HashMap<>();
  private static final Point MENU_POSITION = new Point(20, 460);
  private static final Point SETTINGS_POSITION = new Point(250, 250);
  private static final Point BOARD_POSITION = new Point(140, 60);
  private static final Point MEMORY_GAME_POSITION = new Point(140, 60);
  private static final Point DEFEND_GAME_POSITION = new Point(140, 60);
  private static final Point CHANGE_USER_POSITION = new Point(350, 250);
  private static final Point USER_STATS_POSITION = new Point(210, 120);


  @FXML
  void buttonAction(ActionEvent event) {

    if (isButtonCloseClicked(event)) {

      hide();
      return;
    }

    if (isButtonSettingsClicked(event)) {

      manageSettingsMenuVisibility();
      return;
    }

    if (event.getSource() == btLeft || event.getSource() == btRight) {

      showInfoPanel(pnlMessage);

      runLater(
          () -> {
            // TODO test code, remove it asap
            ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
            emitter.setNumParticles(10); // Número de partículas
            emitter.setSize(5, 10); // Tamaño de las partículas

            emitter.setStartColor(javafx.scene.paint.Color.ORANGE);
            emitter.setEndColor(Color.RED);
            emitter.setSize(5, 10);

            Entity fire =
                entityBuilder()
                    .at(200, 200) // Posición inicial
                    .with(new ParticleComponent(emitter)) // Agregar el componente de partículas
                    .buildAndAttach();

            if (!getGameWorld().getEntities().contains(fire)) {

              getGameWorld().addEntity(fire);
            }
          });
    }
  }

  @FXML
  public void handleButtonClick(MouseEvent event) {

    if (isContextMenuClicked(event)) {

      manageContextMenuVisibility();
      return;
    }
  }

  /** This method is called when the user clicks on the logger user icon. */
  private void manageContextMenuVisibility() {

    ScreenController contextMenuController = getScreen(Screens.MENU);

    if (contextMenuController.isVisible() && !contextMenuController.isInitialized()) {
      contextMenuController.hide();
      return;
    }

    contextMenuController.show(InputScreenData.fromPosition(MENU_POSITION));
  }

  private void manageSettingsMenuVisibility() {

    ScreenController settingMenuController = getScreen(Screens.SETTINGS);
    if (settingMenuController.isVisible() && !settingMenuController.isInitialized()) {

      settingMenuController.hide();
      return;
    }

    SettingsViewInputScreenData inputData =
        new SettingsViewInputScreenData(
            AppProperties.getInstance().getActiveUserId(), SETTINGS_POSITION);

    settingMenuController.show(inputData);
  }

  protected boolean isContextMenuClicked(MouseEvent event) {

    return event.getSource() == lbUserInitials || event.getSource() == pnlMenu;
  }

  private boolean isButtonCloseClicked(ActionEvent event) {

    return event.getSource() == btClose || event.getSource() == imgClose;
  }

  private boolean isButtonSettingsClicked(ActionEvent event) {

    return event.getSource() == btSettings || event.getSource() == imgSettings;
  }

  private void showInfoPanel(Pane panel) {

    panel.setVisible(!panel.isVisible());
  }

  @Subscribe
  public void onUserSaved(UserSavedAppEvent event) {

    Platform.runLater(
        () -> {
          System.out.println("Se guardó el usuario: " + event.getUserId());
          reloadUserInitials(event.getUserId());
        });
  }

  @Subscribe
  public void onMenuSettingsClicked(OpenSettingsFromMenuEvent event) {

    manageContextMenuVisibility();
    manageSettingsMenuVisibility();
  }

  @Subscribe
  public void onAnalysisBoardClicked(OpenAnalysisBoardEvent event) {

    manageContextMenuVisibility();
    manageAnalysisBoardVisibility();
  }

  @Subscribe
  public void onUserChanged(ChangeUserEvent event) {

    manageContextMenuVisibility();
    manageChangeUserVisibility();
  }

  @Subscribe
  public void onMemoryGameClicked(OpenMemoryGameEvent event) {

    manageContextMenuVisibility();
    manageMemoryGameVisibility();
  }

  @Subscribe
  public void onDefendGameClicked(OpenDefendGameEvent event) {

    manageContextMenuVisibility();
    manageDefendGameVisibility();
  }

  @Subscribe
  public void onUserStatsClicked(OpenUserStatsEvent event) {

    manageContextMenuVisibility();
    manageUserStatsVisibility();
  }
  private void manageDefendGameVisibility() {

    ScreenController defendGameController = getScreen(Screens.DEFEND_GAME);
    if (defendGameController.isVisible() && !defendGameController.isInitialized()) {

      defendGameController.hide();
      return;
    }

    SettingsViewInputScreenData inputData =
            new SettingsViewInputScreenData(
                    AppProperties.getInstance().getActiveUserId(), DEFEND_GAME_POSITION);
    defendGameController.show(inputData);
  }


  private void manageMemoryGameVisibility() {

    ScreenController memoryGameController = getScreen(Screens.MEMORY_GAME);
    if (memoryGameController.isVisible() && !memoryGameController.isInitialized()) {

      memoryGameController.hide();
      return;
    }

    SettingsViewInputScreenData inputData =
        new SettingsViewInputScreenData(
            AppProperties.getInstance().getActiveUserId(), MEMORY_GAME_POSITION);
    memoryGameController.show(inputData);
  }

  private void manageAnalysisBoardVisibility() {

    ScreenController boardController = getScreen(Screens.BOARD);
    if (boardController.isVisible() && !boardController.isInitialized()) {

      boardController.hide();
      return;
    }

    SettingsViewInputScreenData inputData =
        new SettingsViewInputScreenData(
            AppProperties.getInstance().getActiveUserId(), BOARD_POSITION);
    boardController.show(inputData);
  }

  private void manageChangeUserVisibility() {

    ScreenController changeUserController = getScreen(Screens.CHANGE_USER);
    if (changeUserController.isVisible() && !changeUserController.isInitialized()) {

      changeUserController.hide();
      return;
    }

    SettingsViewInputScreenData inputData =
            new SettingsViewInputScreenData(
                    AppProperties.getInstance().getActiveUserId(), CHANGE_USER_POSITION);
    changeUserController.show(inputData);
  }

  private void manageUserStatsVisibility() {

    ScreenController userStatsController = getScreen(Screens.USER_STATS);
    if (userStatsController.isVisible() && !userStatsController.isInitialized()) {

      userStatsController.hide();
      return;
    }

    UserStatsInputScreenData inputData =
            new UserStatsInputScreenData(
                    AppProperties.getInstance().getActiveUserId(), USER_STATS_POSITION);
    userStatsController.show(inputData);
  }

  public MainSceneController() {

    GlobalEventBus.get().register(this);
  }

  /** All the logic starts here! */
  @FXML
  public void initialize() {

    moveMainWindowsSetUp();
    reloadUserInitials(AppProperties.getInstance().getActiveUserId());
    showTextAnimation();
  }

  private void showTextAnimation() {

    playTypeWriterEffect(lblChessboard.getText(), lblChessboard,0.1);
    playTypeWriterEffect(lblPractice.getText(), lblPractice,0.1);

  }

  private void moveMainWindowsSetUp() {
    imgLogo.setOnMousePressed(
        event -> {
          xOffset = event.getSceneX();
          yOffset = event.getSceneY();
        });

    imgLogo.setOnMouseDragged(
        event -> {
          Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

          stage.setX(event.getScreenX() - xOffset);
          stage.setY(event.getScreenY() - yOffset);
        });
  }

  /**
   * Get or initialize the screen controller for the given screen
   *
   * @param screen Screen to get
   * @return ScreenController for the given screen
   */
  protected ScreenController getScreen(Screens screen) {

    return screenManager.computeIfAbsent(
        screen,
        s -> {
          try {

            var cachedScreen = ScreenFactory.getInstance().createScreen(s);
            mainPane.getChildren().add(cachedScreen.getRootPane());
            return cachedScreen;
          } catch (IOException e) {

            throw new RuntimeException(e);
          }
        });
  }

  private void reloadUserInitials(long userId) {

    // TODO move createGetUserByUseCase to the constructor... to minimize dependencies
    GetUserByIdUseCase getUserByIdUseCase = UseCaseFactory.createGetUserByIdUseCase();
    User loggedUser = getUserByIdUseCase.execute(userId);
    lbUserInitials.setText(loggedUser.getInitials());

    FXGL.animationBuilder()
        .duration(Duration.seconds(0.5))
        .repeat(2)
        .autoReverse(true)
        .fadeOut(lbUserInitials)
        .buildAndPlay();
  }

  private Stage stage;

  public void setStage(final Stage fxStage) {

    this.stage = fxStage;
  }

  public Stage getStage() {

    return this.stage;
  }

  @Override
  public void setData(InputScreenData inputData) {}

  @Override
  public void setLayout(double layoutX, double layoutY) {}

  @Override
  public void show() {}

  @Override
  public void show(InputScreenData inputData) {}

  public void hide() {

    stage.hide();
  }

  @Override
  public Pane getRootPane() {
    return mainPane;
  }

  @Override
  public ScreenStatus getStatus() {
    return null;
  }

  @Override
  public boolean isInitialized() {
    return false;
  }

  @Override
  public boolean isVisible() {
    return false;
  }

  @Override
  public boolean isHidden() {
    return false;
  }

  public void maximize() {

    if (stage.isMaximized()) {

      stage.setMaximized(false);

    } else {

      stage.setMaximized(true);
    }
  }

  public void minimize() {

    stage.setIconified(true);
  }


  private void playTypeWriterEffect(String text, Text textNode, double charInterval) {
    textNode.setText(""); // Asegurarse de que el Text esté vacío al iniciar
    StringBuilder currentText = new StringBuilder();

    for (int i = 0; i < text.length(); i++) {
      int index = i;
      runOnce(() -> {
        currentText.append(text.charAt(index)); // Añadir la siguiente letra
        textNode.setText(currentText.toString());
          return null;
      }, javafx.util.Duration.seconds(i * charInterval));
    }
  }
}
