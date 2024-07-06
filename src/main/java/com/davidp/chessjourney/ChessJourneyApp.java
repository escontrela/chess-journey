package com.davidp.chessjourney;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/** @see <a href="https://github.com/AlmasB/FXGL">FXGL framework</a> */
public class ChessJourneyApp extends GameApplication {

  GridPane chessBoard = null;
  String fenPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

  @Override
  protected void initSettings(GameSettings settings) {
    settings.setWidth(1000); // Ajustado para acomodar el panel
    settings.setHeight(800);
    settings.setTitle("Chess App");
    settings.setVersion("1.0");
  }

  public static void main(String[] args) {
    launch(args);
  }

  protected GridPane createChessBoard() {
    GridPane board = new GridPane();
    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        Rectangle square = new Rectangle(100, 100);
        if ((row + col) % 2 == 0) {
          square.setFill(Color.WHITE);
        } else {
          square.setFill(Color.SKYBLUE);
        }
        board.add(square, col, row);
      }
    }
    return board;
  }

  protected void showChessPosition(GridPane chessBoard, String fenPos) {

    List<ImageView> nodesToDelete = new ArrayList<>();
    chessBoard
        .getChildren()
        .forEach(
            node -> {
              if (node instanceof ImageView) {
                nodesToDelete.add((ImageView) node);
              }
            });

    if (!nodesToDelete.isEmpty()) {

      chessBoard.getChildren().removeAll(nodesToDelete);
    }

    ChessPiece king = new ChessPiece("/images/white-pawn.png");
    chessBoard.add(king.getImageView(), 4, 2);

    ChessPiece rook = new ChessPiece("/images/white-rook.png");
    chessBoard.add(rook.getImageView(), 5, 0);

    ChessPiece bKing = new ChessPiece("/images/black-pawn.png");
    chessBoard.add(bKing.getImageView(), 4, 6);

    ChessPiece bRook = new ChessPiece("/images/black-rook.png");
    chessBoard.add(bRook.getImageView(), 5, 7);
  }

  @Override
  protected void initGame() {

    this.chessBoard = createChessBoard();

    VBox controlPanel = createControlPanel();
    controlPanel.setPrefWidth(200); // Ajusta el ancho del panel de control

    BorderPane root = new BorderPane();
    root.setCenter(chessBoard);
    root.setRight(controlPanel);

    FXGL.getGameScene().addUINode(root);

    // Mostrar piezas por 5 segundos y luego ocultarlas
    // FXGL.runOnce(() -> king.getImageView().setVisible(false), Duration.seconds(5));
  }

  private VBox createControlPanel() {
    VBox vbox = new VBox();
    vbox.setSpacing(10);
    vbox.setStyle("-fx-padding: 5; -fx-border-color: black; -fx-border-width: 1;");

    Label messageLabel = new Label("Mensajes del juego");
    Button startButton = new Button("Iniciar Ejercicio");
    startButton.setOnAction(event -> startExercise());
    Button stopButton = new Button("Detener Ejercicio");
    stopButton.setOnAction(event -> stopExercise());
    Label statsLabel = new Label("Estadísticas");

    vbox.getChildren().addAll(messageLabel, startButton, stopButton, statsLabel);
    return vbox;
  }

  protected void startExercise() {

    showChessPosition(this.chessBoard, fenPosition);
    FXGL.runOnce(() -> hideRandomPieces(this.chessBoard, 2), Duration.seconds(5));
  }

  protected void hideRandomPieces(GridPane gridPane, int numToHide) {

    List<ImageView> imageViews = new ArrayList<>();
    for (Node node : gridPane.getChildren()) {
      if (node instanceof ImageView) {
        imageViews.add((ImageView) node);
      }
    }

    Collections.shuffle(imageViews);

    for (int i = 0; i < numToHide && i < imageViews.size(); i++) {
      imageViews.get(i).setVisible(false);
    }
  }

  protected void stopExercise() {

    showChessPosition(this.chessBoard, fenPosition);
  }
}
