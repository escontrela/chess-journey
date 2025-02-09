package com.davidp.chessjourney.application.ui.board;

import com.almasb.fxgl.dsl.FXGL;
import com.davidp.chessjourney.application.config.GlobalEventBus;
import com.davidp.chessjourney.application.domain.PromoteSelectedPieceEvent;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.chess.PieceView;
import com.davidp.chessjourney.application.ui.chess.PieceViewFactory;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.domain.common.Piece;
import com.davidp.chessjourney.domain.common.PieceColor;
import com.davidp.chessjourney.domain.common.PieceFactory;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.Optional;

public class PromoteViewController implements ScreenController {

  private ScreenController.ScreenStatus status;

  private PromoteViewInputScreenData promoteViewInputScreenData;

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
  void keyPressed(KeyEvent event) {

  }

  @FXML
  void mouseClicked(MouseEvent event) {

  }

  public void initialize() {

    status = ScreenController.ScreenStatus.INITIALIZED;

    pnlQueen.setOnMousePressed(this::onPanelPiecePressed);
    pnlBishop.setOnMousePressed(this::onPanelPiecePressed);
    pnlPawn.setOnMousePressed(this::onPanelPiecePressed);
    pnlRook.setOnMousePressed(this::onPanelPiecePressed);
    pnlknight.setOnMousePressed(this::onPanelPiecePressed);
    //TODO miss the King!!!
  }

  private void onPanelPiecePressed(MouseEvent event) {
    event.consume(); // This will stop event propagation
    Optional<Pane> selectedSquare = getSquareViewFromMouseEvent(event);

    selectedSquare.ifPresent(
        pane -> {
          pane.setStyle(
              "-fx-border-color: #219ebc; -fx-border-width: 2px; -fx-border-inset: -2px;");
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

    setPieces(getPromoteViewInputScreenData());
  }

  private void setPieces(PromoteViewInputScreenData promoteViewInputScreenData) {

    addPieceToPane(pnlQueen, promoteViewInputScreenData.getPieceColor() == PieceColor.WHITE
            ? PieceFactory.createWhiteQueen(): PieceFactory.createBlackQueen());

    addPieceToPane(pnlknight,promoteViewInputScreenData.getPieceColor() == PieceColor.WHITE
            ? PieceFactory.createWhiteKnight(): PieceFactory.createBlackKnight());

    addPieceToPane(pnlBishop,promoteViewInputScreenData.getPieceColor() == PieceColor.WHITE
            ? PieceFactory.createWhiteBishop(): PieceFactory.createBlackBishop());

    addPieceToPane(pnlRook,promoteViewInputScreenData.getPieceColor() == PieceColor.WHITE
            ? PieceFactory.createWhiteRook(): PieceFactory.createBlackRook());

    addPieceToPane(pnlPawn,promoteViewInputScreenData.getPieceColor() == PieceColor.WHITE
            ? PieceFactory.createWhitePawn(): PieceFactory.createBlackPawn());

    //TODO left the King!!!

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

    // Fade in animation when showing
    FXGL.animationBuilder()
            .duration(Duration.seconds(0.2))
            .onFinished(
                    () -> {
                      rootPane.setVisible(true);
                      rootPane.toFront();
                    })
            .fadeIn(rootPane)
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

  protected void setPromoteViewInputScreenData(PromoteViewInputScreenData promoteViewInputScreenData) {

    this.promoteViewInputScreenData = promoteViewInputScreenData;
  }

  protected PromoteViewInputScreenData getPromoteViewInputScreenData() {

    return promoteViewInputScreenData;
  }
}
