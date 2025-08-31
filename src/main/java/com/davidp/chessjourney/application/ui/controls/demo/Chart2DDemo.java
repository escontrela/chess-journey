package com.davidp.chessjourney.application.ui.controls.demo;

import com.davidp.chessjourney.application.ui.controls.Chart2DController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Demo application to showcase the Chart2DController functionality.
 * This demonstrates how to use the custom 2D chart control.
 */
public class Chart2DDemo extends Application {

    private Chart2DController chart;
    private Random random = new Random();

    @Override
    public void start(Stage primaryStage) {
        // Create the chart control
        chart = new Chart2DController();
        chart.setPrefSize(600, 400);

        // Create control buttons
        Button btnAddSampleData = new Button("Add Sample Data");
        Button btnAddRandomPoint = new Button("Add Random Point");
        Button btnReset = new Button("Reset Chart");

        btnAddSampleData.setOnAction(e -> addSampleData());
        btnAddRandomPoint.setOnAction(e -> addRandomPoint());
        btnReset.setOnAction(e -> chart.resetDataset());

        // Apply button styling
        btnAddSampleData.getStyleClass().add("button-regular");
        btnAddRandomPoint.getStyleClass().add("button-regular");
        btnReset.getStyleClass().add("button-red");

        HBox buttonBar = new HBox(10, btnAddSampleData, btnAddRandomPoint, btnReset);
        buttonBar.setPadding(new Insets(10));

        Label titleLabel = new Label("2D Chart Control Demo");
        titleLabel.getStyleClass().add("text-white-big");

        Label instructionLabel = new Label("This chart control can display 2D datasets and is fully resizable");
        instructionLabel.getStyleClass().add("text-gray-ref-small");

        VBox root = new VBox(10, titleLabel, instructionLabel, buttonBar, chart);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root-gemini-style");

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/com/davidp/chessjourney/custom-styles.css").toExternalForm());

        primaryStage.setTitle("Chart2D Control Demo - Chess Journey");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Add initial sample data
        addSampleData();
    }

    private void addSampleData() {
        List<Chart2DController.DataPoint2D> sampleData = new ArrayList<>();
        
        // Add some sample data points representing a curve
        for (int i = 0; i <= 20; i++) {
            double x = i * 2;
            double y = Math.sin(x * 0.3) * 20 + 50 + random.nextGaussian() * 5;
            sampleData.add(new Chart2DController.DataPoint2D(x, y));
        }
        
        chart.setDataset(sampleData);
    }

    private void addRandomPoint() {
        double x = random.nextDouble() * 50;
        double y = random.nextDouble() * 100;
        chart.addDataPoint(x, y);
    }

    public static void main(String[] args) {
        launch(args);
    }
}