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

/**
 * This controller is responsible for the PGN editor control that allows users to edit and copy FEN
 * and PGN strings.
 */
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
  void buttonAction(ActionEvent event) {

    if (isButtonCloseClicked(event)) {

      // TODO event handler should be notified to close the PGN editor

    } else if (isTxtFenPressed(event)) {

      txtFen.copy();
    } else if (idPGNCopyButtonClicked(event)) {

      txtPGN.copy();
    }
  }

  @FXML
  void handleKeyPress(KeyEvent event) {

    if (isFenInputField(event)) {
      // TODO we should notify the event handler that the FEN has changed
    }

    if (isPGNInputField(event)) {

      // TODO we should notify the event handler that the PGN has changed
    }
  }

  private boolean isPGNInputField(KeyEvent event) {

    return event.getSource() == txtPGN;
  }

  private boolean isFenInputField(KeyEvent event) {

    return event.getSource() == txtFen;
  }

  private boolean idPGNCopyButtonClicked(ActionEvent event) {

    return event.getSource() == btnCopyPGN;
  }

  private boolean isTxtFenPressed(ActionEvent event) {

    return event.getSource() == btnCopyFen;
  }

  private boolean isButtonCloseClicked(ActionEvent event) {

    return event.getSource() == btCloseCtrlPGN;
  }
}
