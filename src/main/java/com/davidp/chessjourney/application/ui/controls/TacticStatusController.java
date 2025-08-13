package com.davidp.chessjourney.application.ui.controls;

import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class TacticStatusController extends Pane {

  @FXML private HBox hbExercises;

  @FXML private HBox hbPly;

  @FXML private Label lblAvgTime;

  @FXML private Label lblBlackLevel;

  @FXML private Label lblBlackRating;

  @FXML private Label lblWhiteHR;

  @FXML private Label lblWhiteLevel;

  @FXML private Label lblWhiteName;

  @FXML private Label lblWhiteRating;

  @FXML private Label lblWhiteTime;

  @FXML private Pane pnlBlack;

  @FXML private Pane pnlWhite;

  public TacticStatusController() {

    FXMLLoader fxmlLoader =
        new FXMLLoader(
            getClass().getResource("/com/davidp/chessjourney/tactics-status-control-view.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);

    try {

      fxmlLoader.load();

    } catch (IOException e) {

      throw new RuntimeException(
          "No se pudo cargar el FXML: /com/davidp/chessjourney/tactics-status-control-view.fxml",
          e);
    }
  }

  @FXML
  private void initialize() {
    // Synchronize properties with text fields

  }
}
