package com.davidp.chessjourney;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.davidp.chessjourney.application.config.AppProperties;
import com.davidp.chessjourney.application.factories.UseCaseFactory;
import com.davidp.chessjourney.domain.common.Fen;
import com.davidp.chessjourney.domain.common.GameState;
import com.davidp.chessjourney.domain.common.Piece;
import com.davidp.chessjourney.domain.common.PiecePosition;
import com.davidp.chessjourney.domain.services.FenServiceFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;
import com.davidp.chessjourney.application.usecases.*;
import com.davidp.chessjourney.domain.User;

/** @see <a href="https://github.com/AlmasB/FXGL">FXGL framework</a> */
/** @see <a href="https://fonts.google.com/icons?selected=Material+Symbols+Outlined:close:FILL@0;wght@400;GRAD@0;opsz@20&icon.query=close&icon.size=18&icon.color=%23353535">Google Material design</a> */
/** @see <a href="https://coolors.co/palettes/trending">Coolors</a> */
public class ChessJourneyApp extends GameApplication {

  GridPane chessBoard = null;
  String fenPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

  private static Stage primaryStage;


  @Override
  protected void initSettings(GameSettings settings) {

    settings.setWidth(1280); // Updated dimensions
    settings.setHeight(900);
    settings.setTitle("Chess App");
    settings.setVersion("2.0");
    settings.setStageStyle(StageStyle.TRANSPARENT);

  }



  @Override
  protected void initGame() {

    try {

      primaryStage = FXGL.getPrimaryStage();

      FXMLLoader loader = new FXMLLoader(getClass().getResource("main-scene-3.fxml"));
      Pane root = loader.load();

      MainSceneController mainController = loader.getController();
      mainController.setStage(primaryStage);

      // Cargar el FXML del tablero
      FXMLLoader boardLoader = new FXMLLoader(getClass().getResource("board-view.fxml"));

      Pane boardRoot = boardLoader.load();
      BoardViewController boardController = boardLoader.getController();

      // Añadir el tablero a la zona izquierda
    //  mainController.getBoardPane().getChildren().add(boardRoot);

      Scene scene = new Scene(root);
      getGameScene().addUINode(scene.getRoot());

      loadUsers();

      System.out.println(String.format("UserId: %s" , AppProperties.getInstance().getActiveUserId()));

    } catch (Exception e) {

      e.printStackTrace();
    }
  }

  private void loadUsers() {

    // Solicitas el caso de uso a la factoría
    GetUsersUseCase getUsersUC = UseCaseFactory.createGetUsersUseCase();

    // Ejecutas el caso de uso
    List<User> userList = getUsersUC.execute();

    // Muestras o procesas los usuarios
    userList.forEach(System.out::println);

  }


  /** Old Code **/

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

    Fen fen = new Fen(fenPos);
    GameState gameState = FenServiceFactory.getFenService().parseString(fen);

    for (PiecePosition piecePosition : gameState.getPieces()) {
      ImageView imageView = createPieceImageView(piecePosition.getPiece());
      chessBoard.add(
          imageView,
          piecePosition.getPosition().getCol().ordinal(),
          7 - piecePosition.getPosition().getRow().ordinal());
    }
  }

  private ImageView createPieceImageView(Piece piece) {

    String pieceImageFile =
        "/images/"
            + piece.getColor().name().toLowerCase()
            + "-"
            + piece.getType().name().toLowerCase()
            + ".png";
    ChessPiece chessPiece = new ChessPiece(pieceImageFile);

    return chessPiece.getImageView();
  }

  protected void showChessPositionDeleteMe(GridPane chessBoard, String fenPos) {

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


  /**
  @Override
  protected void initGame() {

    this.chessBoard = createChessBoard();

    VBox controlPanel = createControlPanel();
    controlPanel.setPrefWidth(200); // Ajusta el ancho del panel de control

    BorderPane root = new BorderPane();
    root.setCenter(chessBoard);
    root.setRight(controlPanel);

    FXGL.getGameScene().addUINode(root);
  }
**/

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

    TextField fenInput = new TextField();
    fenInput.setPromptText("Enter FEN string");
    fenInput.setText("4k3/2p3r1/8/1R2P3/3P4/2P5/8/4K3 w - - 0 1");
    Button fenButton = new Button("Load FEN");
    fenButton.setOnAction(event -> showChessPosition(chessBoard, fenInput.getText()));

    vbox.getChildren()
        .addAll(messageLabel, startButton, stopButton, statsLabel, fenInput, fenButton);
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
