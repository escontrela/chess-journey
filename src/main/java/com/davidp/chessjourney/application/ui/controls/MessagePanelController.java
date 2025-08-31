package com.davidp.chessjourney.application.ui.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * This controller is responsible for the message panel control that allows users to display
 * notification messages with OK and Cancel buttons.
 */
public class MessagePanelController extends Pane {

    @FXML private Pane rootPane;
    @FXML private Label lblMessage;
    @FXML private ImageView imgIcon;
    @FXML private Button btOk;
    @FXML private Button btCancel;

    private MessagePanelActionListener actionListener;

    /**
     * Interface for handling message panel button actions.
     */
    public interface MessagePanelActionListener {
        void onOkButtonClicked();
        void onCancelButtonClicked();
    }

    public MessagePanelController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/davidp/chessjourney/message-panel.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el FXML: /com/davidp/chessjourney/message-panel.fxml", e);
        }
    }

    @FXML
    private void initialize() {
        // Initialize with default message if needed
        if (lblMessage.getText() == null || lblMessage.getText().isEmpty()) {
            lblMessage.setText("This is a note that you have to confirm, or not?");
        }
    }

    /**
     * Sets the message text to be displayed.
     *
     * @param message The message text
     */
    public void setMessage(String message) {
        lblMessage.setText(message);
    }

    /**
     * Gets the current message text.
     *
     * @return The current message text
     */
    public String getMessage() {
        return lblMessage.getText();
    }

    /**
     * Sets the action listener for button events.
     *
     * @param listener The action listener
     */
    public void setMessagePanelActionListener(MessagePanelActionListener listener) {
        this.actionListener = listener;
    }

    @FXML
    void buttonAction(ActionEvent event) {
        if (actionListener != null) {
            if (isOkButtonClicked(event)) {
                actionListener.onOkButtonClicked();
            } else if (isCancelButtonClicked(event)) {
                actionListener.onCancelButtonClicked();
            }
        }
    }

    private boolean isOkButtonClicked(ActionEvent event) {
        return event.getSource() == btOk;
    }

    private boolean isCancelButtonClicked(ActionEvent event) {
        return event.getSource() == btCancel;
    }
}