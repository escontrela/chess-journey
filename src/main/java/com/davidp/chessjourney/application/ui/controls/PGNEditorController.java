package com.davidp.chessjourney.application.ui.controls;

import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
  @FXML private Button btMaxMin;
  @FXML private Button btnCopyPGN;

  @FXML private ImageView imgClose1;

  @FXML private TextField txtFen;

  @FXML private TextField txtPGN;

  @FXML private TextField txtSAN;

  private PGNEditorActionListener actionListener;
  private PGNEditorKeyListener keyListener;

  /** Property synchronized with txtFen's text. */
  private final StringProperty fenProperty = new SimpleStringProperty();

  /** Property synchronized with txtPGN's text. */
  private final StringProperty pgnProperty = new SimpleStringProperty();

  /** Property synchronized with txtSAN's text. */
  private final StringProperty sanProperty = new SimpleStringProperty();

  public interface PGNEditorActionListener {
    void onCloseButtonClicked();

    void onFenCopyClicked();

    void onPGNCopyClicked();

    void onMaxMinButtonClicked();
  }

  public interface PGNEditorKeyListener {
    void onFenChanged(String newFen);

    void onSANChanged(String newSAN);

    void onPGNChanged(String newPGN);
  }

  public void setPGNEditorActionListener(PGNEditorActionListener listener) {
    this.actionListener = listener;
  }

  public void setPGNEditorKeyListener(PGNEditorKeyListener listener) {
    this.keyListener = listener;
  }

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

  /** Property synchronized with txtFen's text. */
  public StringProperty fenProperty() {
    return fenProperty;
  }

  /** Property synchronized with txtPGN's text. */
  public StringProperty pgnProperty() {
    return pgnProperty;
  }

  /** Property synchronized with txtSAN's text. */
  public StringProperty sanProperty() {
    return sanProperty;
  }

  @FXML
  private void initialize() {
    // Synchronize properties with text fields
    fenProperty.bindBidirectional(txtFen.textProperty());
    pgnProperty.bindBidirectional(txtPGN.textProperty());
    sanProperty.bindBidirectional(txtSAN.textProperty());
  }

  @FXML
  void buttonAction(ActionEvent event) {

    if (isButtonCloseClicked(event)) {

      if (actionListener != null) {
        actionListener.onCloseButtonClicked();
      }

    } else if (isTxtFenPressed(event)) {

      txtFen.copy();
      if (actionListener != null) {
        actionListener.onFenCopyClicked();
      }
    } else if (idPGNCopyButtonClicked(event)) {

      txtPGN.copy();
      if (actionListener != null) {
        actionListener.onPGNCopyClicked();
      }
    } else if (isMaxMinButtonClicked(event)) {

      if (actionListener != null) {
        actionListener.onMaxMinButtonClicked();
      }
    }
  }



  @FXML
  void handleKeyPress(KeyEvent event) {

    if (isFenInputField(event)) {
      if (keyListener != null) {
        keyListener.onFenChanged(txtFen.getText());
      }
    }

    if (isPGNInputField(event)) {
      if (keyListener != null) {
        keyListener.onPGNChanged(txtPGN.getText());
      }
    }
    if (isSANInputField(event)) {
      if (keyListener != null) {
        keyListener.onSANChanged(txtSAN.getText());
      }
    }
  }

  private boolean isSANInputField(KeyEvent event) {
    return event.getSource() == txtSAN;
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

  private boolean isMaxMinButtonClicked(ActionEvent event) {

    return btMaxMin == event.getSource();
  }

  private boolean isTxtFenPressed(ActionEvent event) {

    return event.getSource() == btnCopyFen;
  }

  private boolean isButtonCloseClicked(ActionEvent event) {

    return event.getSource() == btCloseCtrlPGN;
  }
}
