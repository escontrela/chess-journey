package com.davidp.chessjourney.application.ui.controls;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

public class PGNEditorController extends Pane {

  @FXML private Pane rootPane;

  @FXML private Button btCloseCtrlPGN;

  @FXML private Button btnCopyFen;

  @FXML private Button btnCopyPGN;

  @FXML private ImageView imgClose1;

  @FXML private TextField txtFen;

  @FXML private TextField txtPGN;

  @FXML private TextField txtSAN;

  public PGNEditorController() {

    FXMLLoader fxmlLoader =
        new FXMLLoader(getClass().getResource("/com/davidp/chessjourney/pgn-control-view.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);

    try {

      fxmlLoader.load();

    } catch (IOException e) {

      throw new RuntimeException(
          "No se pudo cargar el FXML: /com/davidp/chessjourney/pgn-control-view.fxml", e);
    }
  }

  @FXML
  private void initialize() {}

  @FXML
  void buttonAction(ActionEvent event) {}

  @FXML
  void handleKeyPress(KeyEvent event) {}
}
