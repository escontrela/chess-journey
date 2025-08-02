package com.davidp.chessjourney.application.ui.board;

import com.almasb.fxgl.dsl.FXGL;
import com.davidp.chessjourney.application.config.GlobalEventBus;
import com.davidp.chessjourney.application.domain.PromoteSelectedPieceEvent;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.chess.PieceView;
import com.davidp.chessjourney.application.ui.chess.PieceViewFactory;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.ui.util.FXAnimationUtil;
import com.davidp.chessjourney.domain.common.Piece;
import com.davidp.chessjourney.domain.common.PieceColor;
import com.davidp.chessjourney.domain.common.PieceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.Optional;

public class PromoteViewController implements ScreenController {

  private ScreenController.ScreenStatus status;

  private PromoteViewInputScreenData promoteViewInputScreenData;

  private PieceColor selectedPieceColor;

  @FXML
  private Pane pnlBishop;

  @FXML
  private Pane pnlPawn;

  @FXML
  private Pane pnlPromote;

  @FXML
  private Pane pnlQueen;

  @FXML
  private Pane pnlRook;

  @FXML
  private Pane pnlknight;

  @FXML
  private Pane rootPane;

  @FXML
  private Button btChange;

  @FXML
  private Pane pnlKing;

  @FXML
  void keyPressed(KeyEvent event) {

  }

  @FXML
  void mouseClicked(MouseEvent event) {
        // the user clicked on empty space, do nothing and close the window
        event.consume();
        hide();
  }

  public void initialize() {

    status = ScreenController.ScreenStatus.INITIALIZED;

    pnlQueen.setOnMousePressed(this::onPanelPiecePressed);
    pnlBishop.setOnMousePressed(this::onPanelPiecePressed);
    pnlPawn.setOnMousePressed(this::onPanelPiecePressed);
    pnlRook.setOnMousePressed(this::onPanelPiecePressed);
    pnlknight.setOnMousePressed(this::onPanelPiecePressed);
    pnlKing.setOnMousePressed(this::onPanelPiecePressed);
  }

  private void onPanelPiecePressed(MouseEvent event) {

    event.consume(); // This will stop event propagation
    Optional<Pane> selectedSquare = getSquareViewFromMouseEvent(event);

    selectedSquare.ifPresent(
        pane -> {
          pane.setStyle(
              "-fx-border-color: #219ebc; -fx-border-width: 2px; -fx-border-inset: -2px;");
          System.out.println("Selected piece (promote view controller) : " + pane.getChildren().get(0).getUserData());
          GlobalEventBus.get().post(new PromoteSelectedPieceEvent( getPromoteViewInputScreenData().getPiecePos(),
                  (Piece) pane.getChildren().get(0).getUserData()));
          this.hide();
        });
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

    return Optional.empty();
  }

  @Override
  public void setData(InputScreenData inputData) {

    setPromoteViewInputScreenData((PromoteViewInputScreenData) inputData);

    if (inputData.isLayoutInfoValid()) {

      setLayout(inputData.getLayoutX(), inputData.getLayoutY());
    }

    cleanPieces();
    setPieces(getPromoteViewInputScreenData().getPieceColor());
    selectedPieceColor = getPromoteViewInputScreenData().getPieceColor();
  }

  private void cleanPieces() {

    pnlBishop.getChildren().clear();
    pnlPawn.getChildren().clear();
    pnlQueen.getChildren().clear();
    pnlRook.getChildren().clear();
    pnlknight.getChildren().clear();
    pnlKing.getChildren().clear();
  }

  private void setPieces(final PieceColor pieceColor) {

    addPieceToPane(pnlQueen, pieceColor == PieceColor.WHITE
            ? PieceFactory.createWhiteQueen(): PieceFactory.createBlackQueen());

    addPieceToPane(pnlknight,pieceColor == PieceColor.WHITE
            ? PieceFactory.createWhiteKnight(): PieceFactory.createBlackKnight());

    addPieceToPane(pnlBishop,pieceColor == PieceColor.WHITE
            ? PieceFactory.createWhiteBishop(): PieceFactory.createBlackBishop());

    addPieceToPane(pnlRook,pieceColor == PieceColor.WHITE
            ? PieceFactory.createWhiteRook(): PieceFactory.createBlackRook());

    addPieceToPane(pnlPawn,pieceColor == PieceColor.WHITE
            ? PieceFactory.createWhitePawn(): PieceFactory.createBlackPawn());

    addPieceToPane(pnlKing,pieceColor == PieceColor.WHITE
            ? PieceFactory.createWhiteKing(): PieceFactory.createBlackKing());

  }

  /** Add a piece to the chessboard view position. */
  private void addPieceToPane(final Pane e, final Piece piece) {

    PieceView pieceView = PieceViewFactory.getPiece(piece.getType(), piece.getColor());
    pieceView.setUserData(piece);
    addPiece(e, pieceView);
  }

  private void addPiece(Pane pane, PieceView pieceView) {

    pane.getChildren().add(pieceView);
  }

  @Override
  public void setLayout(double layoutX, double layoutY) {

    rootPane.setLayoutX(layoutX);
    rootPane.setLayoutY(layoutY);
  }

  @Override
  public void show() {
    rootPane.setVisible(false);

    FXAnimationUtil.fadeIn(rootPane, 0.2)
            .repeat(1)
            .autoReverse(false)
            .onFinished(() -> {   rootPane.setVisible(true);
              rootPane.toFront();})
            .buildAndPlay();
  }

  @Override
  public void show(InputScreenData inputData) {

    setData(inputData);
    status = ScreenController.ScreenStatus.VISIBLE;
    show();
  }

  @Override
  public void hide() {

    FXAnimationUtil.fadeIn(rootPane, 0.2)
            .repeat(1)
            .autoReverse(false)
            .onFinished(() -> {
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

  protected void setPromoteViewInputScreenData(PromoteViewInputScreenData promoteViewInputScreenData) {

    this.promoteViewInputScreenData = promoteViewInputScreenData;
  }

  protected PromoteViewInputScreenData getPromoteViewInputScreenData() {

    return promoteViewInputScreenData;
  }

  @FXML
  void buttonAction(ActionEvent event) {

    if (event.getSource() == btChange) {

      cleanPieces();
      selectedPieceColor = (selectedPieceColor == PieceColor.WHITE
              ? PieceColor.BLACK : PieceColor.WHITE);

      setPieces(selectedPieceColor);
    }

  }
}
