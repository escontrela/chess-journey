package com.davidp.chessjourney.application.ui.board;


import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.chess.PieceView;
import com.davidp.chessjourney.application.ui.chess.PieceViewFactory;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.domain.common.*;
import com.davidp.chessjourney.domain.services.FenService;
import com.davidp.chessjourney.domain.services.FenServiceFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BoardViewController implements ScreenController{

  FenService fenService = FenServiceFactory.getFenService();

  @FXML
  private Button btClose;

  @FXML
  private Pane pnlBoard;

  @FXML
  private TextField inFen;

  @FXML
  private Pane rootPane;

  @FXML
  private Pane pnlTime;

  private final HashMap<Pos, Pane> boardPanes = new HashMap<>();

  private ScreenController.ScreenStatus status;

  public void initialize() {

    status = ScreenController.ScreenStatus.INITIALIZED;
    initializeBoardPanes();


    pnlBoard.setOnMousePressed(event -> {

      Optional<Pane> selectedSquare = getSquareViewFromMouseEvent(event);

        selectedSquare.ifPresent(
                pane ->
                        pane.setStyle("-fx-border-color: #FFFFFF; -fx-border-width: 2px; -fx-border-inset: -2px;")
        );
    });

  }

  //TODO improve
  private void initializeBoardPanes() {



    for (Node node : pnlBoard.getChildren()) {

      if (node instanceof Pane) {

        Pane square = (Pane) node;


        node.setOnDragOver(new EventHandler<DragEvent>() {

            public void handle(DragEvent event) {

              System.out.println("Entered");
              event.acceptTransferModes(TransferMode.ANY);
              event.consume();
          }});

        node.setOnDragEntered(new EventHandler<DragEvent>() {
          public void handle(DragEvent event) {

            System.out.println("Entered");
            square.setStyle("-fx-border-color: #FF0000; -fx-border-width: 2px; -fx-border-inset: -2px;");
            event.consume();
          }
        });

        node.setOnDragExited(new EventHandler<DragEvent>() {
          public void handle(DragEvent event) {

            System.out.println("Exited");
            square.setStyle("-fx-border-color: #000000; -fx-border-width: 0px; -fx-border-inset: -2px;");
            event.consume();
          }
        });



        node.setOnDragDropped(new EventHandler<DragEvent>() {
          public void handle(DragEvent event) {

            System.out.println("Dropped");
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
              String droppedPos = db.getString();  // esto sí existirá
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

  /*
   * Move a piece with drag and drop
   */
  private EventHandler<? super DragEvent> getDragDropManager(final Pane toSquare, Pos pos) {

    System.out.println("The piece  was dropped on " + toSquare);

    return (DragEvent event) -> {

      System.out.println("Dropped");

      PieceView fromPiece = (PieceView) event.getGestureSource();
      Pane fromSquare = (Pane)fromPiece.getParent();

      // if movement is allowed
      freeSquare(fromSquare);
      addPieceFromPosition(toSquare, fromPiece.getDomainPiece() , pos);

     //  soundService.play(EAT_PIECE);
     //  soundService.play(MOVE_ERR);

      event.consume();
    };
  }

  private void freeSquare(Pane pane) {

    pane.getChildren().clear();
  }

  @Override
  public void setData(InputScreenData inputData) {

    if (inputData.isLayoutInfoValid()){

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

    rootPane.setVisible(true);
/*
    // Fade in animation when showing
    FXGL.animationBuilder()
            .duration(Duration.seconds(0.2))
            .onFinished( ()-> rootPane.setVisible(true))
            .fadeIn(rootPane)
            .buildAndPlay();

 */
  }

  public void show(InputScreenData inputData){

    setData(inputData);
    status = ScreenController.ScreenStatus.VISIBLE;
    show();
  }

  @Override
  public void hide() {

    rootPane.setVisible(false);
    status = ScreenStatus.HIDDEN;
    /*
    FXGL.animationBuilder()
            .duration(Duration.seconds(0.2))
            .onFinished( ()-> {
              rootPane.setVisible(false);

              status = ScreenStatus.HIDDEN;
            })
            .fadeOut(rootPane)
            .buildAndPlay();
    */
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
      //TODO if user close the board then the exercise or the game should be over, please show a advert box message.
     // GlobalEventBus.get().post(new UserSavedAppEvent(settingsViewData.getUserId()));
    }


  }

  @FXML
  private void handleKeyPress(KeyEvent event) {

    if (isFenInputField(event)) {

      if (event.getCode() == KeyCode.ENTER) {

        cleanPieces();

        //TODO validate fen
        final GameState fenParserResponse =
                fenService.parseString(
                        Fen.createCustom(inFen.getText()));

        List<PiecePosition> pieces = fenParserResponse.getPieces();

        pieces.forEach(

                piece -> {
                  var pane = boardPanes.get(piece.getPosition());
                  addPieceFromPosition(pane, piece.getPiece(),piece.getPosition());
                });

      }
    }
  }

  /**
   * Add a piece to the chessboard view position.
   */
  private void addPieceFromPosition(final Pane e, final Piece piece,final Pos position) {

    PieceView pieceView = PieceViewFactory.getPiece(piece.getType(),  piece.getColor());
    addPiece(e, pieceView,position);

  }

 private void addPiece(Pane pane, PieceView pieceView, Pos position) {

    pane.getChildren().add(pieceView);

    pieceView.setOnDragDetected(event -> {


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

  /**
   * Clean the chessboard view.
   */
  private void cleanPieces(){

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
}