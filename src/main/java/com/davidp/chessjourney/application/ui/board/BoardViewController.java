package com.davidp.chessjourney.application.ui.board;

import com.almasb.fxgl.dsl.FXGL;
import com.davidp.chessjourney.application.config.GlobalEventBus;
import com.davidp.chessjourney.application.domain.OpenAnalysisBoardEvent;
import com.davidp.chessjourney.application.domain.OpenSettingsFromMenuEvent;
import com.davidp.chessjourney.application.domain.UserSavedAppEvent;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.chess.PieceView;
import com.davidp.chessjourney.application.ui.chess.PieceViewFactory;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.domain.User;
import com.davidp.chessjourney.domain.common.*;
import com.davidp.chessjourney.domain.services.FenService;
import com.davidp.chessjourney.domain.services.FenServiceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.scene.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardViewController implements ScreenController{

  FenService fenService = FenServiceFactory.getFenService();

  @FXML
  private Pane a1;

  @FXML
  private Pane a2;

  @FXML
  private Pane a3;

  @FXML
  private Pane a4;

  @FXML
  private Pane a5;

  @FXML
  private Pane a6;

  @FXML
  private Pane a7;

  @FXML
  private Pane a8;

  @FXML
  private Pane b1;

  @FXML
  private Pane b2;

  @FXML
  private Pane b3;

  @FXML
  private Pane b4;

  @FXML
  private Pane b5;

  @FXML
  private Pane b6;

  @FXML
  private Pane b7;

  @FXML
  private Pane b8;

  @FXML
  private Button btClose;

  @FXML
  private Pane c1;

  @FXML
  private Pane c2;

  @FXML
  private Pane c3;

  @FXML
  private Pane c4;

  @FXML
  private Pane c5;

  @FXML
  private Pane c6;

  @FXML
  private Pane c7;

  @FXML
  private Pane c8;

  @FXML
  private Pane d1;

  @FXML
  private Pane d2;

  @FXML
  private Pane d3;

  @FXML
  private Pane d4;

  @FXML
  private Pane d5;

  @FXML
  private Pane d6;

  @FXML
  private Pane d7;

  @FXML
  private Pane d8;

  @FXML
  private Pane e1;

  @FXML
  private Pane e2;

  @FXML
  private Pane e3;

  @FXML
  private Pane e4;

  @FXML
  private Pane e5;

  @FXML
  private Pane e6;

  @FXML
  private Pane e7;

  @FXML
  private Pane e8;

  @FXML
  private Pane f1;

  @FXML
  private Pane f2;

  @FXML
  private Pane f3;

  @FXML
  private Pane f4;

  @FXML
  private Pane f5;

  @FXML
  private Pane f6;

  @FXML
  private Pane f7;

  @FXML
  private Pane f8;

  @FXML
  private Pane g1;

  @FXML
  private Pane g2;

  @FXML
  private Pane g3;

  @FXML
  private Pane g4;

  @FXML
  private Pane g5;

  @FXML
  private Pane g6;

  @FXML
  private Pane g7;

  @FXML
  private Pane g8;

  @FXML
  private Pane h1;

  @FXML
  private Pane h2;

  @FXML
  private Pane h3;

  @FXML
  private Pane h4;

  @FXML
  private Pane h5;

  @FXML
  private Pane h6;

  @FXML
  private Pane h7;

  @FXML
  private Pane h8;

  @FXML
  private Pane pnlBoard;

  @FXML
  private ImageView imgClose;

  @FXML
  private TextField inFen;

  @FXML
  private Pane rootPane;

  private HashMap<Pos, Node> boardPanes = new HashMap<>();

  private ScreenController.ScreenStatus status;

  public void initialize() {
    status = ScreenController.ScreenStatus.INITIALIZED;
    initializeBoardPanes();


  }

  //TODO improve
  private void initializeBoardPanes() {

    for (Node node : pnlBoard.getChildren()) {

      if (node instanceof Pane) {

        Pane square = (Pane) node;
        String squareId = square.getId();

        if (squareId != null && squareId.length() == 2) {
        /*
          char colChar = squareId.charAt(0);
          char rowChar = squareId.charAt(1);
          Col col = Col.valueOf(String.valueOf(colChar).toUpperCase());
          Row row = Row.valueOf(String.valueOf(rowChar));
          Pos pos = new Pos(col, row);
          */
          var pos = Pos.parseString(squareId);
          square.setUserData(Pos.parseString(squareId));
          boardPanes.put(pos, square);

        }
      }
    }
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

    // Fade in animation when showing
    FXGL.animationBuilder()
            .duration(Duration.seconds(0.2))
            .onFinished( ()-> rootPane.setVisible(true))
            .fadeIn(rootPane)
            .buildAndPlay();
  }

  public void show(InputScreenData inputData){

    setData(inputData);
    status = ScreenController.ScreenStatus.VISIBLE;
    show();
  }

  @Override
  public void hide() {
    FXGL.animationBuilder()
            .duration(Duration.seconds(0.2))
            .onFinished( ()-> {
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
      //TODO if user close the board then the exercise or the game should be over, please show a advert box message.
     // GlobalEventBus.get().post(new UserSavedAppEvent(settingsViewData.getUserId()));
    }


  }

  @FXML
  private void handleKeyPress(KeyEvent event) {

    if (event.getSource() == inFen) {

      if (event.getCode() == KeyCode.ENTER) {


        //TODO validate fen
        final GameState fenParserResponse =
                fenService.parseString(
                        Fen.createCustom(inFen.getText()));

        // Verificar piezas
        List<PiecePosition> pieces = fenParserResponse.getPieces();


        pieces.forEach(
                piece -> {
                  var pane = boardPanes.get(piece.getPosition());
                  addPieceFromPosition((Pane)pane, piece.getPiece(), piece.getPosition());

                  System.out.println(
                          piece.getPiece().getColor()
                                  + " - "
                                  + piece.getPiece().getType()
                                  + " - "
                                  + piece.getPosition());
                });




       // var pane = ((Pane)boardPanes.get(Pos.parseString("d5")));


        System.out.println("Enter pressed");
        //inLastName.requestFocus();
      }
    }
  }

  /**
   * Add a piece to the chessboard view position.
   */
  private void addPieceFromPosition(final Pane e, final Piece piece, final Pos pos) {

    PieceView pieceView = PieceViewFactory.getPiece(piece.getType(),  piece.getColor());
    addPiece(e, pieceView);

  }

 private void addPiece(Pane pane, PieceView pieceView) {

    pane.getChildren().add(pieceView);

}

  private boolean isCloseButtonClicked(ActionEvent event) {
    return event.getSource() == btClose;
  }
}