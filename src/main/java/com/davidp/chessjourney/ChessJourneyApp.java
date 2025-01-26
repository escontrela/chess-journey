package com.davidp.chessjourney;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.davidp.chessjourney.application.config.AppProperties;
import com.davidp.chessjourney.application.factories.ScreenFactory;
import com.davidp.chessjourney.application.ui.main.MainSceneController;
import java.util.logging.Logger;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @see <a href="https://github.com/AlmasB/FXGL">FXGL framework</a> Chess Journey is a JavaFX
 *     application that uses the FXGL framework and helps to train chess players.
 */
public class ChessJourneyApp extends GameApplication {

  private static final Logger logger = Logger.getLogger(ChessJourneyApp.class.getName());

  protected void initSettings(GameSettings settings) {

    settings.setWidth(1290); // Updated dimensions
    settings.setHeight(900);
    settings.setTitle("Chess Journey");
    settings.setVersion("2.0");
    settings.setStageStyle(StageStyle.TRANSPARENT);
  }

  /** Initializes the game. */
  @Override
  protected void initGame() {

    try {

      Stage primaryStage = FXGL.getPrimaryStage();

      var mainScreenController =
          ScreenFactory.getInstance().createScreen(ScreenFactory.Screens.MAIN_STAGE);

      Pane root = mainScreenController.getRootPane();
      ((MainSceneController) mainScreenController).setStage(primaryStage);

      Scene scene = new Scene(root);
      getGameScene().addUINode(scene.getRoot());

      String userId = String.valueOf(AppProperties.getInstance().getActiveUserId());
      validateUserId(userId);

      logger.info(() -> String.format("UserId: %s", userId));

    } catch (Exception ex) {

      logger.severe("An error was found on initialize game: " + ex.getMessage());
    }
  }

  private void validateUserId(String userId) {

    if (userId == null || userId.isBlank()) {

      logger.warning("It is not possible to start the game without a valid user id.");
    }
  }

  public static void main(String[] args) {

    launch(args);
  }
}
