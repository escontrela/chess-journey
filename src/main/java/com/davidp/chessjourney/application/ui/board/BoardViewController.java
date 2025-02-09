package com.davidp.chessjourney.application.ui.board;

import static javafx.application.Platform.runLater;

import com.almasb.fxgl.dsl.FXGL;
import com.davidp.chessjourney.application.config.AppProperties;
import com.davidp.chessjourney.application.config.GlobalEventBus;
import com.davidp.chessjourney.application.domain.OpenMemoryGameEvent;
import com.davidp.chessjourney.application.domain.PromoteSelectedPieceEvent;
import com.davidp.chessjourney.application.factories.ScreenFactory;
import com.davidp.chessjourney.application.factories.SoundServiceFactory;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.chess.PieceView;
import com.davidp.chessjourney.application.ui.chess.PieceViewFactory;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.ui.settings.SettingsViewInputScreenData;
import com.davidp.chessjourney.application.usecases.MemoryGameUseCase;
import com.davidp.chessjourney.domain.games.memory.MemoryGame;
import com.davidp.chessjourney.domain.common.*;
import com.davidp.chessjourney.domain.services.FenService;
import com.davidp.chessjourney.domain.services.FenServiceFactory;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.eventbus.Subscribe;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class BoardViewController implements ScreenController {

  private enum BoardType {
    CHESS,
    MEMORY
  }

  protected BoardType boardType = BoardType.CHESS;
  protected MemoryGame activeMemoryGame;
  private final FenService fenService = FenServiceFactory.getFenService();
  private final SoundServiceFactory soundService = SoundServiceFactory.getInstance();
  private final Map<ScreenFactory.Screens, ScreenController> screenManager = new HashMap<>();

  @FXML private Button btClose;

  @FXML private Pane pnlBoard;

  @FXML private TextField inFen;

  @FXML private Pane rootPane;

  @FXML private Pane pnlTime;

  @FXML private Label lblBoardType;

  @FXML private Button btStart;


  @FXML
  private Label lblStatus;

  @FXML
  private Label lblExerciseNum;

  @FXML
  private Label lblBlackTime;

  private ImageView imgOk =   new ImageView("com/davidp/chessjourney/img-white/ic_data_usage_white_24dp.png");
  private ImageView imgFail = new ImageView("com/davidp/chessjourney/img-white/baseline_clear_white_24dp.png");

  private boolean piecesHided = false;
  private int matchedPieces = 0;

  private final HashMap<Pos, Pane> boardPanes = new HashMap<>();

  private ScreenController.ScreenStatus status;

  protected MemoryGameUseCase memoryGameUseCase;

  public void initialize() {


    GlobalEventBus.get().register(this);

    status = ScreenController.ScreenStatus.INITIALIZED;
    initializeBoardPanes();

    pnlBoard.setOnMousePressed(this::onMousePressed);

  }

  private void onMousePressed(MouseEvent event) {

    Optional<Pane> selectedSquare = getSquareViewFromMouseEvent(event);

    selectedSquare.ifPresent(
        pane ->{
            pane.setStyle(
                "-fx-border-color: #FFFFFF; -fx-border-width: 2px; -fx-border-inset: -2px;");
            Point screenPos = new Point((int) event.getX(), (int) event.getY());

          if (activeMemoryGame.getGameState() == MemoryGame.GameState.GUESSING_PIECES){

            String squareId = selectedSquare.get().getId();

              var pos = Pos.parseString(squareId);

              PieceColor pieceColor = getPieceColorFromFENPosition(activeMemoryGame.getFen());
              managePromotePanelVisibility(screenPos,pos,pieceColor);
          }
        });
  }

  private PieceColor getPieceColorFromFENPosition(Fen fen) {

      GameState gameState = fenService.parseString(fen);
      return gameState.getActiveColor();
  }
  // TODO improve
  private void initializeBoardPanes() {

    for (Node node : pnlBoard.getChildren()) {

      if (node instanceof Pane) {

        Pane square = (Pane) node;

        node.setOnDragOver(
            new EventHandler<DragEvent>() {

              public void handle(DragEvent event) {

                System.out.println("Entered");
                event.acceptTransferModes(TransferMode.ANY);
                event.consume();
              }
            });

        node.setOnDragEntered(
            new EventHandler<DragEvent>() {
              public void handle(DragEvent event) {

                System.out.println("Entered");
                square.setStyle(
                    "-fx-border-color: #FF0000; -fx-border-width: 2px; -fx-border-inset: -2px;");
                event.consume();
              }
            });

        node.setOnDragExited(
            new EventHandler<DragEvent>() {
              public void handle(DragEvent event) {

                System.out.println("Exited");
                square.setStyle(
                    "-fx-border-color: #000000; -fx-border-width: 0px; -fx-border-inset: -2px;");
                event.consume();
              }
            });

        node.setOnDragDropped(
            new EventHandler<DragEvent>() {
              public void handle(DragEvent event) {

                System.out.println("Dropped");
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                  String droppedPos = db.getString(); // esto sí existirá

                  runLater(
                      () -> soundService.playSound(SoundServiceFactory.SoundType.PIECE_PLACEMENT));

                  System.out.println("Recibido: " + droppedPos);

                  // Lógica para mover la pieza
                  Pane toPane = (Pane) event.getGestureTarget();
                  PieceView pieceView = (PieceView) event.getGestureSource();
                  // Quita la pieza del panel original y muévela al nuevo panel
                  Pane fromPane = (Pane) pieceView.getParent();
                  fromPane.getChildren().remove(pieceView);
                  toPane.getChildren().add(pieceView);

                  success = true;
                }
                event.setDropCompleted(success);
                event.consume();
              }
            });

        String squareId = square.getId();

        if (squareId != null) {

          var pos = Pos.parseString(squareId);
          square.setUserData(Pos.parseString(squareId));
          boardPanes.put(pos, square);
        }
      }
    }
  }

  private Optional<Pane> getSquareViewFromMouseEvent(MouseEvent event) {

    Object target = event.getTarget();

    if (target instanceof Pane) {

      return Optional.of((Pane) target);

    } else if (target instanceof PieceView) {

      Node parent = ((PieceView) target).getParent();

      if (parent instanceof Pane) {

        return Optional.of((Pane) parent);
      }
    }

    // Si no se cumple ninguna de las condiciones, devolvemos un Optional vacío
    return Optional.empty();
  }

  private void freeSquare(Pane pane) {

    pane.getChildren().clear();
  }

  @Override
  public void setData(InputScreenData inputData) {

    if (inputData.isLayoutInfoValid()) {

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

    // Fade in animation when showing
    FXGL.animationBuilder()
        .duration(Duration.seconds(0.2))
        .onFinished(() -> rootPane.setVisible(true))
        .fadeIn(rootPane)
        .buildAndPlay();
  }

  public void show(InputScreenData inputData) {

    setData(inputData);
    status = ScreenController.ScreenStatus.VISIBLE;
    show();
  }

  @Override
  public void hide() {

    FXGL.animationBuilder()
        .duration(Duration.seconds(0.2))
        .onFinished(
            () -> {
              rootPane.setVisible(false);
              status = ScreenStatus.HIDDEN;
            })
        .fadeOut(rootPane)
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
  void buttonAction(ActionEvent event) {

    if (isCloseButtonClicked(event)) {

      rootPane.setVisible(false);
      // TODO if user close the board then the exercise or the game should be over, please show a
      // advert box message.
      // GlobalEventBus.get().post(new UserSavedAppEvent(settingsViewData.getUserId()));
    }
    if (isButtonStartClicked(event)) {

      if (boardType == BoardType.MEMORY) {

        btStart.setDisable(true);
        activeMemoryGame = memoryGameUseCase.execute( AppProperties.getInstance().getActiveUserId() );
        startMemoryGame();
      }
    }
  }

  private void startMemoryGame() {

    cleanBoard();

    GameState gameState = fenService.parseString(activeMemoryGame.getFen());

    showPiecesOnBoard(gameState);

    piecesHided = false;
    matchedPieces = 0;
    activeMemoryGame.startGame();
    lblBoardType.setText("Estado: Jugando...");
    lblExerciseNum.setText(
                    String.valueOf(activeMemoryGame.getCurrentExerciseNumber())
                    + " - " +
                    String.valueOf(activeMemoryGame.totalHiddenPieces())
                    );
    // Iniciar el bucle de juego en FXGL
    FXGL.run(this::gameLoop, Duration.millis(0.1));
  }


  private void showPiecesOnBoard(GameState gameState) {

    List<PiecePosition> pieces = gameState.getPieces();

    pieces.forEach(piece -> {
      var pane = boardPanes.get(piece.getPosition());
      addPieceFromPosition(pane, piece.getPiece(), piece.getPosition());
    });
  }

  private void cleanBoard() {

    boardPanes.forEach((pos, pane) -> freeSquare(pane));
  }

  private void hidePiecesOnBoard(List<PiecePosition> pieces) {
    pieces.forEach(piece -> {
      var pane = boardPanes.get(piece.getPosition());
      freeSquare(pane);
    });
  }

  /**
 * Bucle de juego que controla la lógica del MemoryGame.
 */
private void gameLoop() {


   lblStatus.setText(activeMemoryGame.getGameState().toString());

  if (activeMemoryGame.getGameState() == MemoryGame.GameState.GAME_OVER) {
    lblBoardType.setText("¡Juego Terminado!");
    btStart.setDisable(false);
    return;
  }


    if (activeMemoryGame.getGameState() == MemoryGame.GameState.GUESSING_PIECES) {

      if (matchedPieces == activeMemoryGame.getHiddenPiecePositions().size()) {

        matchedPieces = 0;
        piecesHided = false;
        activeMemoryGame.nextExercise();
        GameState gameState = fenService.parseString(activeMemoryGame.getFen());
        cleanBoard();
        showPiecesOnBoard(gameState);
        lblExerciseNum.setText(
                String.valueOf(activeMemoryGame.getCurrentExerciseNumber())
                        + " - " +
                        String.valueOf(activeMemoryGame.totalHiddenPieces())
        );
      }
  }

  if (activeMemoryGame.getGameState() == MemoryGame.GameState.SHOWING_PIECES
          && activeMemoryGame.isTimeToHidePiecesOnTheCurrentExercise()) {

      hidePiecesOnBoard(activeMemoryGame.getHiddenPiecePositions());
      piecesHided = true;
      lblBoardType.setText("Piezas ocultas. Adivina la posición.");
  }

  lblBlackTime.setText(activeMemoryGame.getFormatedElapsedTime());
}


private boolean isButtonStartClicked(ActionEvent event) {

    return event.getSource() == btStart;
  }

  @FXML
  private void handleKeyPress(KeyEvent event) {

    if (isFenInputField(event)) {

      if (event.getCode() == KeyCode.ENTER) {

        cleanPieces();

        // TODO validate fen
        final GameState fenParserResponse =
            fenService.parseString(Fen.createCustom(inFen.getText()));

        showPiecesOnBoard(fenParserResponse);
      }
    }
  }

  /**
   * Get or initialize the screen controller for the given screen
   *
   * @param screen Screen to get
   * @return ScreenController for the given screen
   */
  protected ScreenController getScreen(ScreenFactory.Screens screen) {

    return screenManager.computeIfAbsent(
            screen,
            s -> {
              try {

                var cachedScreen = ScreenFactory.getInstance().createScreen(s);
                pnlBoard.getChildren().add(cachedScreen.getRootPane());
                return cachedScreen;
              } catch (IOException e) {

                throw new RuntimeException(e);
              }
            });
  }


  /** This method is called when the user clicks on the logger user icon. */
  private void managePromotePanelVisibility(final Point screenPos,final Pos pos, final PieceColor pieceColor) {

    ScreenController contextMenuController = getScreen(ScreenFactory.Screens.PROMOTE_PANEL);

    if (contextMenuController.isVisible() && !contextMenuController.isInitialized()) {

      contextMenuController.hide();
    }

    contextMenuController.show(PromoteViewInputScreenData.from(screenPos,pos,pieceColor));
  }


  /** Add a piece to the chessboard view position. */
  private void addPieceFromPosition(final Pane e, final Piece piece, final Pos position) {

    PieceView pieceView = PieceViewFactory.getPiece(piece.getType(), piece.getColor());
    addPiece(e, pieceView, position);
  }

  private void addPiece(Pane pane, PieceView pieceView, Pos position) {

    pane.getChildren().add(pieceView);

    pieceView.setOnDragDetected(
        event -> {
          Dragboard db = pieceView.startDragAndDrop(TransferMode.ANY);

          pieceView.setStyle("-fx-background-color: transparent;");
          // Configura la imagen que aparecerá bajo el puntero del ratón durante el arrastre
          db.setDragView(pieceView.snapshot(null, null));

          // Convierte a string
          String posStr = position.toString();

          Map<DataFormat, Object> var1 = new HashMap<>();
          var1.put(DataFormat.PLAIN_TEXT, posStr);
          db.setContent(var1);
          event.consume();
        });
  }

  /** Clean the chessboard view. */
  private void cleanPieces() {

    boardPanes.forEach(
        (pos, pane) -> {
          pane.getChildren().clear();
        });
  }

  private boolean isCloseButtonClicked(ActionEvent event) {

    return event.getSource() == btClose;
  }

  private boolean isFenInputField(KeyEvent event) {

    return event.getSource() == inFen;
  }

  public void setMemoryGameUseCase(MemoryGameUseCase memoryGameUseCase) {

    this.memoryGameUseCase = memoryGameUseCase;
    this.boardType = BoardType.MEMORY;
    this.btStart.setVisible(true);
    this.lblBoardType.setText("The memory game!");
  }


  @Subscribe
  public void onMemoryGameClicked(PromoteSelectedPieceEvent event) {

    System.out.println("MemoryGameClicked:" + event.getSelectedPiece());
    System.out.println("MemoryGameClicked pos:" + event.getPos());
    // TODO fix it!

    boolean result = activeMemoryGame.guessPiece(new PiecePosition(event.getSelectedPiece(), event.getPos()));
    if (result) {
      lblBoardType.setText("¡Correcto!");
      boardPanes.get(event.getPos()).getChildren().add(imgOk);

    } else {
      //TODO add correct bitmap
      lblBoardType.setText("Incorrecto");
      boardPanes.get(event.getPos()).getChildren().add(imgFail);
    }
    //matchedPieces = activeMemoryGame.getGuessPiecesCount();
    matchedPieces++;
  }
}
