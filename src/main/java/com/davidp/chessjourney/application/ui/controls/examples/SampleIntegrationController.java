package com.davidp.chessjourney.application.ui.controls.examples;

import com.davidp.chessjourney.application.ui.controls.MessagePanelController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Sample controller demonstrating practical integration of MessagePanelController
 * in a real application scenario.
 */
public class SampleIntegrationController implements Initializable {

    @FXML private AnchorPane rootPane;
    @FXML private Button btnShowSaveConfirmation;
    @FXML private Button btnShowDeleteConfirmation;
    @FXML private Button btnShowExitConfirmation;
    @FXML private MessagePanelController messagePanel;
    @FXML private MessagePanelController secondaryMessagePanel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupMessagePanels();
    }

    private void setupMessagePanels() {
        // Configure the main message panel
        messagePanel.setMessage("Ready to receive messages!");
        messagePanel.setMessagePanelActionListener(new MessagePanelController.MessagePanelActionListener() {
            @Override
            public void onOkButtonClicked() {
                handleMainPanelOk();
            }

            @Override
            public void onCancelButtonClicked() {
                handleMainPanelCancel();
            }
        });

        // Configure the secondary message panel
        secondaryMessagePanel.setMessagePanelActionListener(new MessagePanelController.MessagePanelActionListener() {
            @Override
            public void onOkButtonClicked() {
                handleSecondaryPanelOk();
            }

            @Override
            public void onCancelButtonClicked() {
                handleSecondaryPanelCancel();
            }
        });
    }

    @FXML
    void showSaveConfirmation(ActionEvent event) {
        messagePanel.setMessage("Are you sure you want to save the current game?");
        messagePanel.setVisible(true);
        System.out.println("Showing save confirmation dialog");
    }

    @FXML
    void showDeleteConfirmation(ActionEvent event) {
        secondaryMessagePanel.setMessage("This action will permanently delete the game. This cannot be undone!");
        secondaryMessagePanel.setVisible(true);
        System.out.println("Showing delete confirmation dialog");
    }

    @FXML
    void showExitConfirmation(ActionEvent event) {
        messagePanel.setMessage("Exit without saving? Any unsaved progress will be lost.");
        messagePanel.setVisible(true);
        System.out.println("Showing exit confirmation dialog");
    }

    private void handleMainPanelOk() {
        String currentMessage = messagePanel.getMessage();
        System.out.println("Main panel OK clicked for message: " + currentMessage);
        
        if (currentMessage.contains("save")) {
            // Handle save operation
            System.out.println("✅ Game saved successfully!");
        } else if (currentMessage.contains("Exit")) {
            // Handle exit operation
            System.out.println("✅ Exiting application...");
        }
        
        messagePanel.setVisible(false);
    }

    private void handleMainPanelCancel() {
        System.out.println("Main panel Cancel clicked - operation cancelled");
        messagePanel.setVisible(false);
    }

    private void handleSecondaryPanelOk() {
        String currentMessage = secondaryMessagePanel.getMessage();
        System.out.println("Secondary panel OK clicked for message: " + currentMessage);
        
        if (currentMessage.contains("delete")) {
            // Handle delete operation
            System.out.println("🗑️ Game deleted!");
        }
        
        secondaryMessagePanel.setVisible(false);
    }

    private void handleSecondaryPanelCancel() {
        System.out.println("Secondary panel Cancel clicked - operation cancelled");
        secondaryMessagePanel.setVisible(false);
    }

    /**
     * Example of programmatically showing a message panel with different content
     */
    public void showCustomMessage(String message, Runnable onOk, Runnable onCancel) {
        messagePanel.setMessage(message);
        messagePanel.setMessagePanelActionListener(new MessagePanelController.MessagePanelActionListener() {
            @Override
            public void onOkButtonClicked() {
                if (onOk != null) {
                    onOk.run();
                }
                messagePanel.setVisible(false);
            }

            @Override
            public void onCancelButtonClicked() {
                if (onCancel != null) {
                    onCancel.run();
                }
                messagePanel.setVisible(false);
            }
        });
        messagePanel.setVisible(true);
    }
}