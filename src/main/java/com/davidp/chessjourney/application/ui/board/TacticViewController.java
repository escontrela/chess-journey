package com.davidp.chessjourney.application.ui.board;

import static javafx.application.Platform.runLater;

import com.davidp.chessjourney.application.config.AppProperties;
import com.davidp.chessjourney.application.config.GlobalEventBus;
import com.davidp.chessjourney.application.domain.PromoteSelectedPieceEvent;
import com.davidp.chessjourney.application.factories.ScreenFactory;
import com.davidp.chessjourney.application.factories.SoundServiceFactory;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.chess.PieceView;
import com.davidp.chessjourney.application.ui.chess.PieceViewFactory;
import com.davidp.chessjourney.application.ui.controls.PGNEditorController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.ui.util.FXAnimationUtil;
import com.davidp.chessjourney.application.usecases.MemoryGameUseCase;
import com.davidp.chessjourney.application.usecases.SaveUserExerciseStatsUseCase;
import com.davidp.chessjourney.domain.ChessBoard;
import com.davidp.chessjourney.domain.ChessBoardFactory;
import com.davidp.chessjourney.domain.ChessRules;
import com.davidp.chessjourney.domain.games.memory.DefendMemoryGame;
import com.davidp.chessjourney.domain.games.memory.GuessMemoryGame;
import com.davidp.chessjourney.domain.games.memory.MemoryGame;
import com.davidp.chessjourney.domain.common.*;
import com.davidp.chessjourney.domain.services.FenService;
import com.davidp.chessjourney.domain.services.FenServiceFactory;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

import com.davidp.chessjourney.domain.services.PGNService;
import com.davidp.chessjourney.domain.services.PGNServiceFactory;
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

import com.davidp.chessjourney.application.util.JavaFXSchedulerUtil;
import com.davidp.chessjourney.application.util.JavaFXGameTimerUtil;

public class TacticViewController implements ScreenController {

  private enum BoardType {
    CHESS,
    MEMORY
  }

  protected BoardType boardType = BoardType.CHESS;
  protected MemoryGame<?> activeMemoryGameOld;
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

  private ImageView imgOk =
      new ImageView("com/davidp/chessjourney/img-white/ic_data_usage_white_24dp.png");
  private ImageView imgFail =
      new ImageView("com/davidp/chessjourney/img-white/baseline_clear_white_24dp.png");

  private boolean piecesHided = false;
  private int matchedPieces = 0;

  private final HashMap<Pos, Pane> boardPanes = new HashMap<>();

  private ScreenController.ScreenStatus status;

  protected MemoryGameUseCase memoryGameUseCase;
  protected SaveUserExerciseStatsUseCase saveUserExerciseStatsUseCase;

  protected boolean pauseLoopGame = false;

  protected boolean idValidForELO = true;

  public void initialize() {

    GlobalEventBus.get().register(this);

    status = ScreenController.ScreenStatus.INITIALIZED;
    initializeBoardPanes();

    pnlBoard.setOnMousePressed(this::onMousePressed);

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
            //TODO place the new PGN on the board
            // This is not implemented yet, but we can use the PGNService to parse
          }
        });

    pnlPGNControl.setPGNEditorActionListener(new PGNEditorController.PGNEditorActionListener() {
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
    });
  }

  private void onMousePressed(MouseEvent event) {

    Optional<Pane> selectedSquare = getSquareViewFromMouseEvent(event);

    selectedSquare.ifPresent(
        pane -> {
          if (pane.getId() == pnlBoard.getId()) {

            return;
          }

          pane.setStyle(
              "-fx-border-color: #FFFFFF; -fx-border-width: 2px; -fx-border-inset: -2px;");

          int x = (int) event.getX();

          if (event.getX() < 80) {
            x = 80;
          }
          if (event.getX() > 480) {
            x = 480;
          }
          int y = (int) event.getY();
          if (event.getY() > 80) {
            y = 80;
          }

          Point screenPos = new Point(x, y);

          // TODO: defend piece game: check if the game is memory or memory defend

          if (activeMemoryGameOld != null
              && activeMemoryGameOld.getGameState() == MemoryGame.GameState.GUESSING_PIECES
              && activeMemoryGameOld instanceof GuessMemoryGame) {

            String squareId = selectedSquare.get().getId();
            var pos = Pos.parseString(squareId);

            PieceColor pieceColor = getPieceColorFromFENPosition(activeMemoryGameOld.getFen());
            managePromotePanelVisibility(screenPos, pos, pieceColor);
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

                if (activeMemoryGameOld != null
                    && activeMemoryGameOld.getGameKind() == MemoryGame.GameKind.GUESS_MEMORY_GAME) {
                  event.consume();
                  return;
                }

                if (activeMemoryGameOld != null
                    && activeMemoryGameOld.getGameKind() == MemoryGame.GameKind.DEFEND_MEMORY_GAME
                    && activeMemoryGameOld.getGameState() != MemoryGame.GameState.GUESSING_PIECES) {
                  event.consume();
                  return;
                }

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
                  onDefendedGameClicked(fromPane.getId(), toPane.getId());
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

      if (boardType == BoardType.MEMORY) {

        btStart.setDisable(true);
        System.out.println("Start Memory Game:" + difficulty);
        // TODO: defend piece game: the use case should accept the kind of memory game that we are
        // looking for
        activeMemoryGameOld =
            memoryGameUseCase.execute(AppProperties.getInstance().getActiveUserId(), difficulty);

        startMemoryGame();
      }
    }

    if (isButtonPGNEditorClicked(event)) {

      pnlPGNControl.setVisible(!pnlPGNControl.isVisible());
    }
  }

  private void startMemoryGame() {

    cleanPieces();
    JavaFXGameTimerUtil.clear();
    // TODO, if the results game is shown, then we need to hide it.

    GameState gameState = fenService.parseString(activeMemoryGameOld.getFen());

    showPiecesOnBoard(gameState);

    pauseLoopGame = false;
    piecesHided = false;
    matchedPieces = 0;
    activeMemoryGameOld.startGame();
    lblBoardType.setText("Estado: Jugando...");
    playTypeWriterEffect("Memoriza la posición...", lblGhostMsg, 0.02);
    lblExerciseNum.setText(
        String.valueOf(activeMemoryGameOld.getCurrentExerciseNumber())
            + " - "
            + String.valueOf(
                activeMemoryGameOld
                    .getTotalStepsPerExercise()) // TODO: defend piece game: total hidde pieces
        // could be total moves , so .. steps
        );
    // Iniciar el bucle de juego con JavaFXGameTimerUtil
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

  private void hidePiecesOnBoard(List<PiecePosition> pieces) {
    pieces.forEach(
        piece -> {
          var pane = boardPanes.get(piece.getPosition());
          freeSquare(pane);
        });
  }

  /** Bucle de juego que controla la lógica del MemoryGame. */
  private void gameLoop() {

    lblStatus.setText(activeMemoryGameOld.getGameState().toString());

    if (pauseLoopGame) {

      return;
    }

    if (activeMemoryGameOld.getGameState() == MemoryGame.GameState.GAME_OVER) {

      pauseLoopGame = true;
      JavaFXGameTimerUtil.clear();
      lblBoardType.setText(
          "¡Juego Terminado! " + activeMemoryGameOld.getSuccessPercentage() + "% conseguido.");
      lblGhostMsg.setText("El juego ha terminado. ¡Felicitaciones!");
      btStart.setDisable(false);
      Point screenPos = new Point(80, 240);
      runLater(
          () ->
              manageExerciseResultPanelVisibility(
                  screenPos, activeMemoryGameOld.getSuccessPercentage()));

      // TODO hay que deterner el loop! parece que el juego siempre sigue...
      return;
    }

    if (activeMemoryGameOld.getGameState() == MemoryGame.GameState.GUESSING_PIECES) {

      // TODO: Improve this logic, the activeMemoryGame should be responsible to manage the game
      // state
      // TODO: defend piece game: gesspieces count should be guess steps count, and hiddenpieces
      // positions should be total steps positions
      if (activeMemoryGameOld.isTimeToMoveToNextExercise()) {

        matchedPieces = 0;
        piecesHided = false;
        activeMemoryGameOld.nextExercise();
        runLater(() -> soundService.playSound(SoundServiceFactory.SoundType.NEW_GAME));
        GameState gameState = fenService.parseString(activeMemoryGameOld.getFen());
        cleanPieces();
        showPiecesOnBoard(gameState);
        lblExerciseNum.setText(
            String.valueOf(activeMemoryGameOld.getCurrentExerciseNumber())
                + " - "
                + String.valueOf(
                    activeMemoryGameOld
                        .getTotalStepsPerExercise()) // TODO: defend piece game: total hidde pieces
            // could be total moves , so .. steps
            );
      }
    }

    if (activeMemoryGameOld.getGameState() == MemoryGame.GameState.SHOWING_PIECES
        && activeMemoryGameOld.isTimeToHidePiecesOnTheCurrentExercise()) {

      hidePiecesOnBoard(
          activeMemoryGameOld
              .getHiddenPiecePositions()); // //TODO: defend piece game: should be nice on defend
      // game, we should hide the attack pieces...
      piecesHided = true;
      lblBoardType.setText("Piezas ocultas. Adivina la posición.");
      playTypeWriterEffect("Adivina la posición de las piezas ocultas.", lblGhostMsg, 0.02);
    }

    lblBlackTime.setText(activeMemoryGameOld.getFormatedElapsedTime());
  }

  private void playTypeWriterEffect(String text, Label textNode, double charInterval) {
    textNode.setText(""); // Asegurarse de que el Text esté vacío al iniciar
    StringBuilder currentText = new StringBuilder();

    for (int i = 0; i < text.length(); i++) {
      int index = i;
      JavaFXSchedulerUtil.runOnce(
          () -> {
            currentText.append(text.charAt(index)); // Añadir la siguiente letra
            textNode.setText(currentText.toString());
          },
          javafx.util.Duration.seconds(i * charInterval));
    }
  }

  private boolean isButtonStartClicked(ActionEvent event) {

    return event.getSource() == btStart;
  }

  @FXML
  private void handleKeyPress(KeyEvent event) {



  }

  private void PGNMove() {
    ChessBoard chessBoard = ChessBoardFactory.createFromFEN(Fen.createCustom(pnlPGNControl.fenProperty().get()));

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

      PiecePosition pieceToMove =
          chessBoard.getPiece(regularMove.getMoves().getFirst().getFrom());

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
  private void managePromotePanelVisibility(
      final Point screenPos, final Pos pos, final PieceColor pieceColor) {

    ScreenController contextMenuController = getScreen(ScreenFactory.Screens.PROMOTE_PANEL);

    if (contextMenuController.isVisible() && !contextMenuController.isInitialized()) {

      contextMenuController.hide();
    }

    contextMenuController.show(PromoteViewInputScreenData.from(screenPos, pos, pieceColor));
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


  public <T extends MemoryGame<?>> void setMemoryGameUseCase(
      MemoryGameUseCase<T> memoryGameUseCase) {
    this.memoryGameUseCase = memoryGameUseCase;
    this.boardType = BoardType.MEMORY;
    this.btStart.setVisible(true);
    this.lblBoardType.setText("The tactics game!");
    playTypeWriterEffect("¿Preparado?, pulsa en el botón de inicio.", lblGhostMsg, 0.02);
  }

  public void setSaveUserExerciseStatsUseCase(
      SaveUserExerciseStatsUseCase saveUserExerciseStatsUseCase) {
    this.saveUserExerciseStatsUseCase = saveUserExerciseStatsUseCase;
  }

  public void onDefendedGameClicked(String from, String to) {

    System.out.println("onDefendedGameClicked:" + from + " to:" + to);

    if (activeMemoryGameOld != null
        && activeMemoryGameOld instanceof DefendMemoryGame
        && activeMemoryGameOld.getGameState() == MemoryGame.GameState.GUESSING_PIECES) {

      // TODO DEBERÍA COGER EL FEN DE LA POSICIÓN, PORQUE SI SE HA MOVIDO ALGUNA PIEZA PREVIAMENTE.

      ChessBoard chessBoard = ChessBoardFactory.createFromFEN(getFenFromActiveBoard());

      ChessRules chessRules = new ChessRules();

      String move =
          pgnService.toAlgebraic(
              Pos.parseString(from), Pos.parseString(to), chessBoard, chessRules, null);

      System.out.println("move:" + move);

      pauseLoopGame = true;

      // TODO: defend piece game: this is only for memory game, for defend game we need to change
      // the piece on the board, so .. guessMove method better
      boolean result = ((MemoryGame<String>) activeMemoryGameOld).submitAnswer(move);

      if (result) {

        FXAnimationUtil.fadeIn(boardPanes.get(Pos.parseString(to)), 2.0)
            .onFinished(
                () -> {
                  lblBoardType.setText("¡Correcto!");
                  // boardPanes.get(event.getPos()).getChildren().add(imgOk);
                  boardPanes.get(Pos.parseString(to)).setStyle("-fx-background-color: #00E680;");

                  playTypeWriterEffect("Bien hecho!", lblGhostMsg, 0.02);
                  runLater(
                      () -> soundService.playSound(SoundServiceFactory.SoundType.SUCCEED_EXERCISE));
                  // FXGL.play("correct.wav");

                  if (this.idValidForELO) {

                    insertUserStatsForThisExercise(result);
                  }

                  matchedPieces++;
                  showHiddenPieces();
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
                  // boardPanes.get(event.getPos()).getChildren().add(imgFail);
                  boardPanes.get(Pos.parseString(to)).setStyle("-fx-background-color: #FF0071;");
                  playTypeWriterEffect("Más suerte para la próxima!", lblGhostMsg, 0.02);
                  runLater(
                      () -> soundService.playSound(SoundServiceFactory.SoundType.FAIL_EXERCISE));
                  if (this.idValidForELO) {

                    insertUserStatsForThisExercise(result);
                  }
                  matchedPieces++;
                  showHiddenPieces();
                  FXAnimationUtil.fadeIn(lblBoardType, 2.0)
                      .onFinished(() -> pauseLoopGame = false)
                      .buildAndPlay();
                })
            .buildAndPlay();
      }
    }
  }

  private Fen getFenFromActiveBoard() {

    return activeMemoryGameOld.getFen();
  }

  @Subscribe
  public void onMemoryGameClicked(PromoteSelectedPieceEvent event) {

    System.out.println("MemoryGameClicked:" + event.getSelectedPiece());
    System.out.println("MemoryGameClicked pos:" + event.getPos());

    pauseLoopGame = true;

    // TODO: defend piece game: this is only for memory game, for defend game we need to change the
    // piece on the board, so .. guessMove method better
    boolean result =
        ((MemoryGame<PiecePosition>) activeMemoryGameOld)
            .submitAnswer(new PiecePosition(event.getSelectedPiece(), event.getPos()));

    if (result) {

      FXAnimationUtil.fadeIn(boardPanes.get(event.getPos()), 0.2)
          .repeat(1)
          .autoReverse(false)
          .onFinished(
              () -> {
                lblBoardType.setText("¡Correcto!");
                // boardPanes.get(event.getPos()).getChildren().add(imgOk);
                boardPanes.get(event.getPos()).setStyle("-fx-background-color: #00E680;");

                runLater(
                    () -> soundService.playSound(SoundServiceFactory.SoundType.SUCCEED_EXERCISE));
                // FXGL.play("correct.wav");

                runLater(
                    () -> soundService.playSound(SoundServiceFactory.SoundType.SUCCEED_EXERCISE));
                // FXGL.play("correct.wav");

                if (this.idValidForELO) {

                  insertUserStatsForThisExercise(result);
                }

                matchedPieces++;
                showHiddenPieces();

                FXAnimationUtil.fadeIn(lblBoardType, 2.0)
                    .onFinished(() -> pauseLoopGame = false)
                    .buildAndPlay();
              })
          .buildAndPlay();

    } else {

      FXAnimationUtil.fadeIn(boardPanes.get(event.getPos()), 0.2)
          .onFinished(
              () -> {
                lblBoardType.setText("Incorrecto");
                // boardPanes.get(event.getPos()).getChildren().add(imgFail);
                boardPanes.get(event.getPos()).setStyle("-fx-background-color: #FF0071;");
                playTypeWriterEffect("Más suerte para la próxima!", lblGhostMsg, 0.02);
                runLater(() -> soundService.playSound(SoundServiceFactory.SoundType.FAIL_EXERCISE));
                if (this.idValidForELO) {

                  insertUserStatsForThisExercise(result);
                }
                matchedPieces++;
                showHiddenPieces();
                FXAnimationUtil.fadeIn(lblBoardType, 2.0)
                    .onFinished(() -> pauseLoopGame = false)
                    .buildAndPlay();
              })
          .buildAndPlay();
    }
  }

  protected void insertUserStatsForThisExercise(boolean result) {

    UserExerciseStats userExerciseStats =
        new UserExerciseStats(
            UUID.randomUUID(),
            AppProperties.getInstance().getActiveUserId(),
            activeMemoryGameOld.getCurrentExerciseId(),
            LocalDateTime.now(),
            result,
            1, // //TODO get the partial time taken!
            1, // Attempts
            activeMemoryGameOld.getDifficultyLevel().getId()); // TODO exercise difficulty level!

    saveUserExerciseStatsUseCase.execute(userExerciseStats);
  }

  private void showHiddenPieces() {

    // TODO: defend piece game: this is only for memory game, for defend game we need to change the
    // piece on the board.
    if (matchedPieces == activeMemoryGameOld.getHiddenPiecePositions().size()) {

      List<PiecePosition> hiddenPieces = activeMemoryGameOld.getHiddenPiecePositions();
      hiddenPieces.forEach(
          piece -> {
            var pane = boardPanes.get(piece.getPosition());
            addPieceFromPosition(pane, piece.getPiece(), piece.getPosition());
          });
    }
  }
}
