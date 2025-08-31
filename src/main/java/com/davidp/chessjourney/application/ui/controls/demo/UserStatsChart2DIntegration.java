package com.davidp.chessjourney.application.ui.controls.demo;

import com.davidp.chessjourney.application.ui.controls.Chart2DController;
import com.davidp.chessjourney.domain.common.AggregatedStats;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Integration example showing how to use Chart2DController in an existing screen controller.
 * This demonstrates how to integrate the custom 2D chart with existing data sources.
 */
public class UserStatsChart2DIntegration {

    @FXML private VBox chartContainer;
    @FXML private Label chartTitle;

    private Chart2DController customChart;

    @FXML
    private void initialize() {
        setupCustomChart();
    }

    private void setupCustomChart() {
        // Create the custom chart
        customChart = new Chart2DController();
        customChart.setPrefSize(600, 300);

        // Add to container
        chartContainer.getChildren().add(customChart);

        // Set title
        chartTitle.setText("Success Rate Over Time - Custom 2D Chart");
        chartTitle.getStyleClass().add("text-white-medium");
    }

    /**
     * Convert AggregatedStats data to Chart2D format.
     * This shows how to integrate with existing data sources like UserRepository stats.
     */
    public void updateChartWithStats(List<AggregatedStats> stats) {
        List<Chart2DController.DataPoint2D> chartData = new ArrayList<>();

        for (int i = 0; i < stats.size(); i++) {
            AggregatedStats stat = stats.get(i);
            // Use index as X (days) and the aggregated value as Y
            chartData.add(new Chart2DController.DataPoint2D(i, stat.getValue()));
        }

        customChart.setDataset(chartData);
    }

    /**
     * Alternative method to show date-based data conversion.
     */
    public void updateChartWithDateBasedStats(List<AggregatedStats> stats) {
        List<Chart2DController.DataPoint2D> chartData = new ArrayList<>();

        LocalDate baseDate = LocalDate.now().minusDays(stats.size());

        for (int i = 0; i < stats.size(); i++) {
            AggregatedStats stat = stats.get(i);
            LocalDate statDate = stat.getDate();
            
            // Convert date to days since base date
            long daysSinceBase = java.time.temporal.ChronoUnit.DAYS.between(baseDate, statDate);
            
            chartData.add(new Chart2DController.DataPoint2D(daysSinceBase, stat.getValue()));
        }

        customChart.setDataset(chartData);
    }

    /**
     * Example of adding real-time data points (e.g., from game results).
     */
    public void addRealTimeDataPoint(double gameResult, double timeSpent) {
        customChart.addDataPoint(gameResult, timeSpent);
    }

    /**
     * Reset chart data (useful for changing time periods or filters).
     */
    public void resetChart() {
        customChart.resetDataset();
    }

    /**
     * Set custom range for specific data visualization needs.
     */
    public void setCustomRange(double minX, double maxX, double minY, double maxY) {
        customChart.setDataRange(minX, maxX, minY, maxY);
    }
}