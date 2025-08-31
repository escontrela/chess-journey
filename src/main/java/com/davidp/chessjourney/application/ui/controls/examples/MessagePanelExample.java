package com.davidp.chessjourney.application.ui.controls.examples;

import com.davidp.chessjourney.application.ui.controls.MessagePanelController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Example usage of the MessagePanelController.
 * This demonstrates how to integrate and use the message panel control in any FXML or controller.
 */
public class MessagePanelExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the message panel control
        MessagePanelController messagePanel = new MessagePanelController();
        
        // Set a custom message
        messagePanel.setMessage("Are you sure you want to save this game?");
        
        // Set up event listeners
        messagePanel.setMessagePanelActionListener(new MessagePanelController.MessagePanelActionListener() {
            @Override
            public void onOkButtonClicked() {
                System.out.println("User clicked OK - proceeding with save");
                // Add your OK button logic here
                // For example: save the game, close dialog, etc.
            }

            @Override
            public void onCancelButtonClicked() {
                System.out.println("User clicked Cancel - operation cancelled");
                // Add your Cancel button logic here
                // For example: close dialog without saving, etc.
            }
        });

        // You can also use lambda expressions for cleaner code:
        // messagePanel.setMessagePanelActionListener(new MessagePanelController.MessagePanelActionListener() {
        //     @Override
        //     public void onOkButtonClicked() -> System.out.println("OK clicked"),
        //     @Override
        //     public void onCancelButtonClicked() -> System.out.println("Cancel clicked")
        // });

        // Create a simple layout to display the control
        VBox root = new VBox(20);
        root.getChildren().add(messagePanel);
        root.setStyle("-fx-padding: 20; -fx-background-color: #2a2a2a;");

        Scene scene = new Scene(root, 600, 200);
        
        // Load the custom CSS styles
        scene.getStylesheets().add(getClass().getResource("/com/davidp/chessjourney/custom-styles.css").toExternalForm());
        
        primaryStage.setTitle("Message Panel Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}