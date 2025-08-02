package com.davidp.chessjourney;

import com.almasb.fxgl.dsl.FXGL;
import com.davidp.chessjourney.application.config.AppProperties;
import com.davidp.chessjourney.application.ui.main.MainSceneController;
import com.davidp.chessjourney.application.util.JavaFXAnimationUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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

  /** Old Code * */
  public static void main(String[] args) {

    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {

    try {
      primaryStage = stage;
      primaryStage.setTitle("Chess Journey");
      primaryStage.initStyle(StageStyle.TRANSPARENT);

      FXMLLoader loader = new FXMLLoader(getClass().getResource("main-scene-3.fxml"));
      Pane root = loader.load();

      MainSceneController mainController = loader.getController();
      mainController.setStage(primaryStage);

      Scene scene = new Scene(root);
      primaryStage.setScene(scene);
      scene.setFill(Color.TRANSPARENT);

      // Efecto niebla al iniciar
      root.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
        if (newVal.getWidth() > 0 && newVal.getHeight() > 0) {
           JavaFXAnimationUtil.playFogEffect(root, 10.0);
        }
      });
      primaryStage.show();

      System.out.println(
          String.format("UserId: %s", AppProperties.getInstance().getActiveUserId()));

    } catch (Exception e) {

      e.printStackTrace();
    }
  }
}
