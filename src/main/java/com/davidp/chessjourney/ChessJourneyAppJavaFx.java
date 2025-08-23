package com.davidp.chessjourney;

import com.davidp.chessjourney.api.ActiveUserController;
import com.davidp.chessjourney.api.ApiConfig;
import com.davidp.chessjourney.application.config.AppProperties;
import com.davidp.chessjourney.application.factories.ScreenFactory;
import com.davidp.chessjourney.application.factories.UseCaseFactory;
import com.davidp.chessjourney.application.ui.main.MainSceneController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/** @see <a href="https://github.com/AlmasB/FXGL">FXGL framework</a> */
/**
 * @see <a
 *     href="https://fonts.google.com/icons?selected=Material+Symbols+Outlined:close:FILL@0;wght@400;GRAD@0;opsz@20&icon.query=close&icon.size=18&icon.color=%23353535">Google
 *     Material design</a>
 */

/** @see <a href="https://coolors.co/palettes/trending">Coolors</a> */
public class ChessJourneyAppJavaFx extends Application {

  GridPane chessBoard = null;
  String fenPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

  private static Stage primaryStage;
  private ActiveUserController activeUserController;
  private ApiConfig apiConfig;
  private static final int API_PORT = 8080;

  /** Old Code * */
  public static void main(String[] args) {

    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {

    try {

      initializeRestServer();

      primaryStage = stage;
      primaryStage.setTitle("Chess Journey");
      primaryStage.initStyle(StageStyle.TRANSPARENT);

      var mainScreenController =
          ScreenFactory.getInstance().createScreen(ScreenFactory.Screens.MAIN_STAGE);
      Pane root = mainScreenController.getRootPane();
      ((MainSceneController) mainScreenController).setStage(primaryStage);

      Scene scene = new Scene(root);
      primaryStage.setScene(scene);
      scene.setFill(Color.TRANSPARENT);

      primaryStage.show();

      System.out.println(
          String.format("UserId: %s", AppProperties.getInstance().getActiveUserId()));

    } catch (Exception e) {

      e.printStackTrace();
    }
  }

  private void initializeRestServer() {


    apiConfig = ApiConfig.getInstance();
    apiConfig.start(API_PORT);

  }

  @Override
  public void stop() throws Exception {

    if (apiConfig != null) {
      apiConfig.stop();
    }

    super.stop();
  }
}
