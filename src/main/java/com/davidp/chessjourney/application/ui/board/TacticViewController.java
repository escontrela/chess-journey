package com.davidp.chessjourney.application.ui.board;

import static javafx.application.Platform.runLater;

import com.davidp.chessjourney.application.config.AppProperties;
import com.davidp.chessjourney.application.config.GlobalEventBus;
import com.davidp.chessjourney.application.factories.RepositoryFactory;
import com.davidp.chessjourney.application.factories.ScreenFactory;
import com.davidp.chessjourney.application.factories.SoundServiceFactory;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.chess.PieceView;
import com.davidp.chessjourney.application.ui.chess.PieceViewFactory;
import com.davidp.chessjourney.application.ui.controls.PGNEditorController;
import com.davidp.chessjourney.application.ui.controls.TacticStatusController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.ui.util.FXAnimationUtil;
import com.davidp.chessjourney.application.usecases.SaveUserExerciseStatsUseCase;
import com.davidp.chessjourney.application.usecases.TacticGameUseCase;
import com.davidp.chessjourney.application.util.JavaFXGameTimerUtil;
import com.davidp.chessjourney.application.util.JavaFXSchedulerUtil;
import com.davidp.chessjourney.domain.*;
import com.davidp.chessjourney.domain.common.*;
import com.davidp.chessjourney.domain.games.tactic.TacticGame;
import com.davidp.chessjourney.domain.services.FenService;
import com.davidp.chessjourney.domain.services.FenServiceFactory;
import com.davidp.chessjourney.domain.services.PGNService;
import com.davidp.chessjourney.domain.services.PGNServiceFactory;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class TacticViewController implements ScreenController {

  protected enum BoardType {
      DO_TACTIC,
      DO_STUDY
  }

  protected BoardType boardType = BoardType.DO_TACTIC;

  private final FenService fenService = FenServiceFactory.getFenService();
  private final PGNService pgnService = PGNServiceFactory.getPGNService();

  private final SoundServiceFactory soundService = SoundServiceFactory.getInstance();
  private final Map<ScreenFactory.Screens, ScreenController> screenManager = new HashMap<>();

  @FXML private Button btClose;
  @FXML private Button btnPGNEditor;

  @FXML private Pane pnlBoard;

  @FXML private Pane rootPane;

  @FXML private Pane pnlTime;

  @FXML private Label lblBoardType;

  @FXML private Button btStart;

  protected String difficulty = "easy";

  @FXML private Label lblStatus;

  @FXML private Label lblExerciseNum;

  @FXML private Label lblBlackTime;

  @FXML private Label lblGhostMsg;

  @FXML private PGNEditorController pnlPGNControl;
  @FXML private TacticStatusController pnlStatusControl;

  private ImageView imgOk =
      new ImageView("com/davidp/chessjourney/img-white/ic_data_usage_white_24dp.png");
  private ImageView imgFail =
      new ImageView("com/davidp/chessjourney/img-white/baseline_clear_white_24dp.png");

  private boolean piecesHided = false;
  private int matchedPieces = 0;

  private final HashMap<Pos, Pane> boardPanes = new HashMap<>();

  private ScreenController.ScreenStatus status;

  protected TacticGameUseCase tacticGameUseCase;
  protected TacticGame activeTacticGame;
  protected SaveUserExerciseStatsUseCase saveUserExerciseStatsUseCase;

  protected boolean pauseLoopGame = false;

  protected boolean idValidForELO = true;

  public void initialize() {

    GlobalEventBus.get().register(this);
    status = ScreenController.ScreenStatus.INITIALIZED;

    initializeBoardPanes();

    initializePGNEditorPanes();

    setStatusPanelState();
  }

    private void initializePGNEditorPanes() {

        pnlPGNControl.setPGNEditorKeyListener(
            new PGNEditorController.PGNEditorKeyListener() {
              @Override
              public void onFenChanged(String newFen) {

                cleanPieces();

                final GameState fenParserResponse =
                    fenService.parseString(Fen.createCustom(pnlPGNControl.fenProperty().get()));

                showPiecesOnBoard(fenParserResponse);
              }

              @Override
              public void onSANChanged(String newSAN) {
                PGNMove();
              }

              @Override
              public void onPGNChanged(String newPGN) {
                // TODO place the new PGN on the board
                // This is not implemented yet, but we can use the PGNService to parse
              }
            });

        pnlPGNControl.setPGNEditorActionListener(
            new PGNEditorController.PGNEditorActionListener() {
              @Override
              public void onCloseButtonClicked() {
                pnlPGNControl.setVisible(false);
              }

              @Override
              public void onFenCopyClicked() {
                // No hacer nada
              }

              @Override
              public void onPGNCopyClicked() {
                // No hacer nada
              }

              @Override
              public void onMaxMinButtonClicked() {
                boolean newMaximizedState = !pnlPGNControl.isMaximized();
                pnlPGNControl.setMaximized(newMaximizedState);
                if (newMaximizedState) {
                  pnlPGNControl.setLayoutY(pnlPGNControl.getLayoutY() - 80);
                } else {
                  pnlPGNControl.setLayoutY(pnlPGNControl.getLayoutY() + 80);
                }
              }
            });

        updatePGNEditorButtonStyle();

        pnlPGNControl
            .visibleProperty()
            .addListener(
                (obs, oldVal, newVal) -> {
                  updatePGNEditorButtonStyle();
                });
    }

    private void setStatusPanelState() {


    // This should be give by constructor on a service interface.
    long userId =  AppProperties.getInstance().getActiveUserId();
    UserRepository userRepository = RepositoryFactory.createUserRepository();
    User user = userRepository.getUserById(userId);


    pnlStatusControl.setExerciseAvgTime("1.5 s.");
    pnlStatusControl.setExerciseLevel("5");
    pnlStatusControl.setExerciseRating("85%");
    pnlStatusControl.setUserName(user.getFirstname() + " " + user.getLastname());
    pnlStatusControl.setUserRating("1470");
    pnlStatusControl.setExerciseTime("00:32");

    // Configurar número de rectángulos
    pnlStatusControl.setNumExercises(20); // Crea 5 rectángulos en hbExercises
    pnlStatusControl.setNumPly(4); // Crea 3 rectángulos en hbPly

    // Configurar ejercicio/ply actual (con stroke blanco)
    pnlStatusControl.setCurrentExercise(3); // El 3er rectángulo tendrá stroke blanco
    pnlStatusControl.setCurrentPly(4); // El 2do rectángulo tendrá stroke blanco

    pnlStatusControl.setExerciseState(0, TacticStatusController.STATE_OK); // Verde
    pnlStatusControl.setExerciseState(1, TacticStatusController.STATE_FAIL);
    pnlStatusControl.setExerciseState(2, TacticStatusController.STATE_OK); // Rojo
    pnlStatusControl.setPlyState(0, TacticStatusController.STATE_OK);
    pnlStatusControl.setPlyState(1, TacticStatusController.STATE_OK);
    pnlStatusControl.setPlyState(2, TacticStatusController.STATE_FAIL);

    /**
     * // Si necesitas hacer binding bidireccional
     * tacticStatus.exerciseAvgTimeProperty().bind(otherProperty);
     * tacticStatus.numExercisesProperty().bind(countProperty);
     */
  }



  // TODO improve
  private void initializeBoardPanes() {

    for (Node node : pnlBoard.getChildren()) {

      if (node instanceof Pane) {

        Pane square = (Pane) node;

        node.setOnDragOver(
            new EventHandler<DragEvent>() {

              public void handle(DragEvent event) {

                event.acceptTransferModes(TransferMode.ANY);
                event.consume();
              }
            });

        node.setOnDragEntered(
            new EventHandler<DragEvent>() {
              public void handle(DragEvent event) {

                square.setStyle(
                    "-fx-border-color: #FF0000; -fx-border-width: 2px; -fx-border-inset: -2px;");
                event.consume();
              }
            });

        node.setOnDragExited(
            new EventHandler<DragEvent>() {
              public void handle(DragEvent event) {

                square.setStyle(
                    "-fx-border-color: #000000; -fx-border-width: 0px; -fx-border-inset: -2px;");
                event.consume();
              }
            });

        node.setOnDragDropped(
            new EventHandler<DragEvent>() {
              public void handle(DragEvent event) {



                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                  String droppedPos = db.getString(); // esto sí existirá

                  runLater(
                      () -> soundService.playSound(SoundServiceFactory.SoundType.PIECE_PLACEMENT));

                  // Lógica para mover la pieza
                  Pane toPane = (Pane) event.getGestureTarget();
                  PieceView pieceView = (PieceView) event.getGestureSource();
                  // Quita la pieza del panel original y muévela al nuevo panel
                  Pane fromPane = (Pane) pieceView.getParent();
                  fromPane.getChildren().remove(pieceView);
                  toPane.getChildren().add(pieceView);

                  success = true;
                  onPieceMoveClicked(fromPane.getId(), toPane.getId());
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


  private void freeSquare(Pane pane) {

    pane.getChildren().clear();
    pane.setStyle("");
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
    FXAnimationUtil.fadeIn(rootPane, 0.2)
        .onFinished(() -> rootPane.setVisible(true))
        .buildAndPlay();
  }

  public void show(InputScreenData inputData) {

    setData(inputData);
    status = ScreenController.ScreenStatus.VISIBLE;
    show();
  }

  @Override
  public void hide() {

    FXAnimationUtil.fadeOut(rootPane, 0.2)
        .onFinished(
            () -> {
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

      if (boardType == BoardType.DO_TACTIC) {

        btStart.setDisable(true);

        // looking for
        activeTacticGame =
            tacticGameUseCase.execute(AppProperties.getInstance().getActiveUserId(), difficulty);

        startTacticGame();
      }
    }
    if (isButtonPGNEditorClicked(event)) {

      pnlPGNControl.setVisible(!pnlPGNControl.isVisible());
      // El listener de visibleProperty se encarga de actualizar el estilo
    }
  }

  /** Main method to start the Tactic Game. */
  private void startTacticGame() {

    cleanPieces();
    JavaFXGameTimerUtil.clear();

    activeTacticGame.startGame();

    GameState gameState = fenService.parseString(activeTacticGame.getFen());

    showPiecesOnBoard(gameState);

    pauseLoopGame = false;
    piecesHided = false;
    matchedPieces = 0;

    lblBoardType.setText("Estado: Jugando...");
    playTypeWriterEffect("Memoriza la posición...", lblGhostMsg, 0.02);

    pnlStatusControl.setNumExercises(activeTacticGame.getTotalExercises());
    pnlStatusControl.setNumPly(activeTacticGame.getTotalPliesInCurrentExercise());
    pnlStatusControl.setCurrentPly(activeTacticGame.getCurrentPlyNumber());
    pnlStatusControl.setExerciseLevel(activeTacticGame.toString());
    pnlStatusControl.setCurrentExercise(activeTacticGame.getCurrentExerciseNumber() - 1);
    pnlStatusControl.setCurrentPly(activeTacticGame.getCurrentPlyNumber() - 1);
    pnlStatusControl.setPlyState(0, TacticStatusController.STATE_NORMAL);
    pnlStatusControl.setExerciseState(0, TacticStatusController.STATE_NORMAL);
    lblExerciseNum.setText(
        String.valueOf(activeTacticGame.getCurrentExerciseNumber())
            + " - "
            + String.valueOf(
                activeTacticGame.getTotalExercises()) // TODO: defend piece game: total hidde pieces
        // could be total moves , so .. steps

        );

    JavaFXGameTimerUtil.runLoop(this::gameLoop, Duration.millis(100));
  }

  private void showPiecesOnBoard(GameState gameState) {

    List<PiecePosition> pieces = gameState.getPieces();

    pieces.forEach(
        piece -> {
          var pane = boardPanes.get(piece.getPosition());
          addPieceFromPosition(pane, piece.getPiece(), piece.getPosition());
        });
  }



  /** Bucle de juego que controla la lógica del MemoryGame. */
  private void gameLoop() {

    lblStatus.setText(activeTacticGame.getGameState().toString());

    if (pauseLoopGame) {

      return;
    }

    if (activeTacticGame.getGameState() == TacticGame.GameState.GAME_OVER) {

      pauseLoopGame = true;
      JavaFXGameTimerUtil.clear();
      lblBoardType.setText(
          "¡Juego Terminado! " + activeTacticGame.getSuccessPercentage() + "% conseguido.");
      lblGhostMsg.setText("El juego ha terminado. ¡Felicitaciones!");
      btStart.setDisable(false);
      Point screenPos = new Point(80, 240);
      runLater(
          () ->
              manageExerciseResultPanelVisibility(
                  screenPos, activeTacticGame.getSuccessPercentage()));

      // TODO hay que deterner el loop! parece que el juego siempre sigue...
      return;
    }

    pnlStatusControl.setExerciseTime(activeTacticGame.getFormattedElapsedTime());
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

  private boolean isButtonStartClicked(ActionEvent event) {

    return event.getSource() == btStart;
  }

  @FXML
  private void handleKeyPress(KeyEvent event) {}

  private void PGNMove() {

    ChessBoard chessBoard =
        ChessBoardFactory.createFromFEN(Fen.createCustom(pnlPGNControl.fenProperty().get()));

    ChessRules chessrules = new ChessRules();

    // Castling test
    GameMove posChessMove = pgnService.fromAlgebraic(pnlPGNControl.sanProperty().get(), chessBoard);

    if (posChessMove instanceof CastlingMove castlingMove) {

      boolean validMove =
          chessrules.isValidCastlingMove(
              castlingMove.kingMove().getFrom(),
              castlingMove.kingMove().getTo(),
              chessBoard.getFen());

      System.out.println("Is it a valid move castlingMove:" + validMove);

      Pane kingPane = boardPanes.get(castlingMove.kingMove().getFrom());
      Pane rookPane = boardPanes.get(castlingMove.rookMove().getFrom());
      Pane kingPaneTo = boardPanes.get(castlingMove.kingMove().getTo());
      Pane rookPaneTo = boardPanes.get(castlingMove.rookMove().getTo());

      PieceView kingView = (PieceView) kingPane.getChildren().getFirst();
      PieceView rookView = (PieceView) rookPane.getChildren().getFirst();

      freeSquare(kingPane);
      freeSquare(rookPane);

      addPiece(kingPaneTo, kingView, castlingMove.kingMove().getTo());
      addPiece(rookPaneTo, rookView, castlingMove.rookMove().getTo());
    }

    if (posChessMove instanceof RegularMove regularMove) {

      boolean isCapture = regularMove.isCapture();
      boolean validMove =
          chessrules.isValidMoveWithCapture(
              regularMove.getMoves().getFirst().getFrom(),
              regularMove.getMoves().getFirst().getTo(),
              chessBoard.getFen(),
              isCapture);

      System.out.println(
          "Is it a valid move regularMove:"
              + validMove
              + " check:"
              + posChessMove.isCheck()
              + " isCapture:"
              + isCapture);

      if (!validMove) {

        throw new RuntimeException("Is not a valid move:" + regularMove);
      }

      PiecePosition pieceToMove = chessBoard.getPiece(regularMove.getMoves().getFirst().getFrom());

      Pane fromPane = boardPanes.get(regularMove.getMoves().getFirst().getFrom());
      Pane toPane = boardPanes.get(regularMove.getMoves().getFirst().getTo());

      PieceView pieceView = (PieceView) fromPane.getChildren().getFirst();

      freeSquare(fromPane);

      if (isCapture) {

        // TODO aqui deberíamos emitir un sonido de captura
        freeSquare(toPane);
      }

      addPiece(toPane, pieceView, regularMove.getMoves().getFirst().getTo());

      if (regularMove.isCheck() || regularMove.isMate()) {

        PieceColor preMoveMotActiveColor = chessBoard.getGameState().getNotActiveColor();

        // Necesitamos hacer el movimiento en el tablero:
        chessBoard.movePiece(
            pieceToMove.getPiece(),
            regularMove.getMoves().getFirst().getFrom(),
            regularMove.getMoves().getFirst().getTo());
        chessBoard.setTurn(chessBoard.getGameState().getNotActiveColor());

        System.out.println("new FEN:" + chessBoard.getFen().getStringValue());

        if (chessrules.isCheckOrMate(chessBoard.getFen())) {
          System.out.println("check or mate!");
          // where is the king?
          Pos opponentKingPos =
              chessBoard
                  .getAllPiecePositionsOfType(PieceType.KING, preMoveMotActiveColor)
                  .getFirst();
          Pane opponentKingPane = boardPanes.get(opponentKingPos);
          PieceView kingView = (PieceView) opponentKingPane.getChildren().getFirst();

          opponentKingPane.setStyle("-fx-background-color: #FF0000;");
          // TODO aqui deberíamos emitir un sonido de check!
        } else {
          System.out.println("Error on PGN input, is it not check or mate!");
        }
      }

      // TODO validar si es promotionMove!!! -> podría ser el caso anterior (un c8) pero con
      // promoción!!
      // TODO el caso de promotionMove puede interferiro con otros...
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
  private void manageExerciseResultPanelVisibility(final Point screenPos, final int percentage) {

    ScreenController exerciseResultsController =
        getScreen(ScreenFactory.Screens.EXERCISE_RESULTS_PANEL);

    if (exerciseResultsController.isVisible() && !exerciseResultsController.isInitialized()) {

      exerciseResultsController.hide();
    }

    exerciseResultsController.show(ExerciseResultViewInputScreenData.from(screenPos, percentage));
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

    boardPanes.forEach((pos, pane) -> freeSquare(pane));
  }

  private boolean isCloseButtonClicked(ActionEvent event) {

    return event.getSource() == btClose;
  }

  private boolean isButtonPGNEditorClicked(ActionEvent event) {

    return event.getSource() == btnPGNEditor;
  }

  public void setTacticGameUseCase(TacticGameUseCase memoryGameUseCase) {

    this.tacticGameUseCase = memoryGameUseCase;
    this.boardType = BoardType.DO_TACTIC;
    this.btStart.setVisible(true);
    this.lblBoardType.setText("The tactics game!");
    playTypeWriterEffect(
        "¿Preparado para una sesión de táctica?, pulsa en el botón de inicio.", lblGhostMsg, 0.02);
  }

  public void setSaveUserExerciseStatsUseCase(
      SaveUserExerciseStatsUseCase saveUserExerciseStatsUseCase) {
    this.saveUserExerciseStatsUseCase = saveUserExerciseStatsUseCase;
  }

  public void onPieceMoveClicked(String from, String to) {

    System.out.println(
        "onPieceMoveClicked:"
            + from
            + " to:"
            + to
            + " for game:"
            + activeTacticGame.getGameState());

    if (activeTacticGame != null
        && (activeTacticGame.getGameState() == TacticGame.GameState.AWAITING_USER_MOVE)) {

      ChessBoard chessBoard = ChessBoardFactory.createFromFEN(getFenFromActiveBoard());
      ChessRules chessRules = new ChessRules();

      String move =
          pgnService.toAlgebraic(
              Pos.parseString(from), Pos.parseString(to), chessBoard, chessRules, null);

      pauseLoopGame = true;
      boolean result = activeTacticGame.submitMove(move);

      if (result) {

        FXAnimationUtil.fadeIn(boardPanes.get(Pos.parseString(to)), 1.0)
            .onFinished(
                () -> {

                  lblBoardType.setText("¡Correcto!");
                  // boardPanes.get(event.getPos()).getChildren().add(imgOk);
                  boardPanes.get(Pos.parseString(to)).setStyle("-fx-background-color: #00E680;");

                  pnlStatusControl.setPlyState(
                      activeTacticGame.getCurrentPlyNumber()-1 , TacticStatusController.STATE_OK);

                  if (activeTacticGame.getGameState() == TacticGame.GameState.GAME_OVER) {

                      playTypeWriterEffect("Bien hecho, ejercicio terminado!", lblGhostMsg, 0.02);

                      runLater(
                        () ->
                            soundService.playSound(SoundServiceFactory.SoundType.SUCCEED_EXERCISE));


                      pnlStatusControl.setExerciseState(
                        activeTacticGame.getCurrentExerciseNumber() - 2,
                        TacticStatusController.STATE_OK);

                      pnlStatusControl.setCurrentExercise(
                        activeTacticGame.getCurrentExerciseNumber() - 1);

                    return;
                  } else if (activeTacticGame.getGameState()
                      == TacticGame.GameState.READY_FOR_NEXT_EXERCISE) {

                      //Publicamos en el tablero un nuevo ejercicio con su FEN position
                      activeTacticGame.startNextExercise();

                        pnlStatusControl.setExerciseState(
                        activeTacticGame.getCurrentExerciseNumber() - 2,
                            TacticStatusController.STATE_OK);

                        pnlStatusControl.setCurrentExercise(
                            activeTacticGame.getCurrentExerciseNumber() - 1);

                        pnlStatusControl.setNumPly(activeTacticGame.getTotalPliesInCurrentExercise());
                        pnlStatusControl.setCurrentPly(activeTacticGame.getCurrentPlyNumber() - 1);
                        pnlStatusControl.setPlyState(0, TacticStatusController.STATE_NORMAL);
                        pnlStatusControl.resetAllPlyStates();

                        GameState gameState = fenService.parseString(activeTacticGame.getFen());
                        cleanPieces();
                        showPiecesOnBoard(gameState);

                  } else {

                      if (activeTacticGame.hasPendingAutoMove()) {

                          Pos fromPos = Pos.parseString(from);
                          Pos toPos = Pos.parseString(to);
                          PiecePosition movingPiece = chessBoard.getPiece(fromPos);
                          chessBoard.movePiece(movingPiece.getPiece(), fromPos, toPos);
                          chessBoard.setTurn(movingPiece.getPiece().getColor().opposite());

                          // La UI aplica el auto-move y lo consume
                          String moveToMake = activeTacticGame.peekPendingAutoMove();
                          System.out.println("Next move:" + moveToMake);

                          // now we should perform the move!
                          GameMove gameMove = pgnService.fromAlgebraic(moveToMake, chessBoard);

                          System.out.println("gameMove:" + gameMove);

                          Pos nextMoveFrom = gameMove.getMoves().getFirst().getFrom();
                          Pos nextMoveTo = gameMove.getMoves().getFirst().getTo();
                          PiecePosition nextMovingPiece = chessBoard.getPiece(nextMoveFrom);

                          chessBoard.movePiece(nextMovingPiece.getPiece(), nextMoveFrom, nextMoveTo);
                          chessBoard.setTurn(movingPiece.getPiece().getColor().opposite());

                          activeTacticGame.setNewChessBoardState(chessBoard);
                          cleanPieces();
                          GameState gameState = fenService.parseString(chessBoard.getFen());
                          showPiecesOnBoard(gameState);

                          activeTacticGame.consumePendingAutoMove();
                          pnlStatusControl.setCurrentPly(activeTacticGame.getCurrentPlyNumber());

                      }
                  }

                  playTypeWriterEffect("Bien hecho!", lblGhostMsg, 0.02);
                  runLater(
                      () -> soundService.playSound(SoundServiceFactory.SoundType.SUCCEED_EXERCISE));
                  // FXGL.play("correct.wav");

                  if (this.idValidForELO) {

                    insertUserStatsForThisExercise(result);
                  }

                  FXAnimationUtil.fadeIn(lblBoardType, 2.0)
                      .onFinished(() -> pauseLoopGame = false)
                      .buildAndPlay();
                })
            .buildAndPlay();

      } else {

        FXAnimationUtil.fadeIn(boardPanes.get(Pos.parseString(to)), 2.0)
            .onFinished(
                () -> {
                  lblBoardType.setText("Incorrecto");

                  pnlStatusControl.setPlyState(
                      activeTacticGame.getCurrentPlyNumber() - 1,
                      TacticStatusController.STATE_FAIL);

                  // TODO we must show the piece on the board, so we can see the error

                  // boardPanes.get(event.getPos()).getChildren().add(imgFail);
                  boardPanes.get(Pos.parseString(to)).setStyle("-fx-background-color: #FF0071;");
                  playTypeWriterEffect("Más suerte para la próxima!", lblGhostMsg, 0.02);
                  runLater(
                      () -> soundService.playSound(SoundServiceFactory.SoundType.FAIL_EXERCISE));
                  if (this.idValidForELO) {

                    insertUserStatsForThisExercise(result);
                  }
                  FXAnimationUtil.fadeIn(lblBoardType, 2.0)
                      .onFinished(() -> pauseLoopGame = false)
                      .buildAndPlay();

                    if (activeTacticGame.getGameState() == TacticGame.GameState.READY_FOR_NEXT_EXERCISE) {
                        activeTacticGame.startNextExercise();
                        pnlStatusControl.setExerciseState(
                                activeTacticGame.getCurrentExerciseNumber() - 2,
                                TacticStatusController.STATE_FAIL);

                        pnlStatusControl.setCurrentExercise(
                                activeTacticGame.getCurrentExerciseNumber() - 1);

                        pnlStatusControl.setNumPly(activeTacticGame.getTotalPliesInCurrentExercise());
                        pnlStatusControl.setCurrentPly(activeTacticGame.getCurrentPlyNumber() - 1);
                        pnlStatusControl.setPlyState(0, TacticStatusController.STATE_NORMAL);
                        pnlStatusControl.resetAllPlyStates();

                        GameState gameState = fenService.parseString(activeTacticGame.getFen());
                        cleanPieces();
                        showPiecesOnBoard(gameState);

                    }

                })
            .buildAndPlay();
      }
    }
  }

  private Fen getFenFromActiveBoard() {

    return activeTacticGame.getFen();
  }

  protected void insertUserStatsForThisExercise(boolean result) {

    UserExerciseStats userExerciseStats =
        new UserExerciseStats(
            UUID.randomUUID(),
            AppProperties.getInstance().getActiveUserId(),
            activeTacticGame.getCurrentExerciseId(),
            LocalDateTime.now(),
            result,
            1, // //TODO get the partial time taken!
            1, // Attempts
            activeTacticGame.getDifficultyLevel().getId()); // TODO exercise difficulty level!

    saveUserExerciseStatsUseCase.execute(userExerciseStats);
  }

  private void updatePGNEditorButtonStyle() {

    btnPGNEditor.getStyleClass().removeAll("button-regular", "button-regular-pressed");
    if (pnlPGNControl.isVisible()) {
      btnPGNEditor.getStyleClass().add("button-regular-pressed");
    } else {
      btnPGNEditor.getStyleClass().add("button-regular");
    }
  }
}
