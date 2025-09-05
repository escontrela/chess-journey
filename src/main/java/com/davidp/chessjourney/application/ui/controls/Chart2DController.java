package com.davidp.chessjourney.application.ui.controls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.io.IOException;
import java.util.List;

/**
 * A custom 2D chart control that displays data points in a 2D coordinate system.
 * This control allows visualization of datasets with X and Y dimensions.
 */
public class Chart2DController extends Pane {
    
    @FXML private Canvas chartCanvas;
    
    private final ObservableList<DataPoint2D> dataset = FXCollections.observableArrayList();
    
    // Chart styling constants matching the app theme
    private static final Color BACKGROUND_COLOR = Color.web("#232232"); // panel-gray background
    private static final Color GRID_COLOR = Color.web("#404040");
    private static final Color AXIS_COLOR = Color.web("#b3b3b3");
    private static final Color DATA_POINT_COLOR = Color.web("#3b82f6"); // Blue gradient from theme
    private static final Color BAR_BORDER_COLOR = Color.web("#4f46e5"); // Darker blue for contrast
    private static final Color TEXT_COLOR = Color.web("#ffffff");
    private static final Color CHART_AREA_COLOR = Color.web("#161620"); // panel-menu background
    
    // Chart margins and padding
    private static final double MARGIN_LEFT = 60;
    private static final double MARGIN_RIGHT = 30;
    private static final double MARGIN_TOP = 30;
    private static final double MARGIN_BOTTOM = 60;
    
    // Data range variables
    private double minX = 0, maxX = 100;
    private double minY = 0, maxY = 100;
    
    public Chart2DController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/davidp/chessjourney/chart-2d.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el FXML: /com/davidp/chessjourney/chart-2d.fxml", e);
        }
    }
    
    @FXML
    private void initialize() {
        // Set up canvas resize behavior
        chartCanvas.widthProperty().bind(this.widthProperty());
        chartCanvas.heightProperty().bind(this.heightProperty());
        
        // Redraw when canvas size changes
        chartCanvas.widthProperty().addListener((obs, oldVal, newVal) -> redrawChart());
        chartCanvas.heightProperty().addListener((obs, oldVal, newVal) -> redrawChart());
        
        // Initial draw
        redrawChart();
    }
    
    /**
     * Sets the dataset for the chart and redraws it.
     * @param dataPoints List of data points to display
     */
    public void setDataset(List<DataPoint2D> dataPoints) {
        dataset.clear();
        if (dataPoints != null) {
            dataset.addAll(dataPoints);
            calculateDataRange();
        }
        redrawChart();
    }
    
    /**
     * Adds a single data point to the chart.
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void addDataPoint(double x, double y) {
        dataset.add(new DataPoint2D(x, y));
        calculateDataRange();
        redrawChart();
    }
    
    /**
     * Clears all data points from the chart.
     */
    public void resetDataset() {
        dataset.clear();
        minX = 0; maxX = 100;
        minY = 0; maxY = 100;
        redrawChart();
    }
    
    /**
     * Gets the current dataset.
     * @return Observable list of data points
     */
    public ObservableList<DataPoint2D> getDataset() {
        return dataset;
    }
    
    /**
     * Sets the data range for the chart axes.
     * @param minX Minimum X value
     * @param maxX Maximum X value
     * @param minY Minimum Y value
     * @param maxY Maximum Y value
     */
    public void setDataRange(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        redrawChart();
    }
    
    private void calculateDataRange() {
        if (dataset.isEmpty()) {
            return;
        }
        
        minX = dataset.stream().mapToDouble(DataPoint2D::getX).min().orElse(0);
        maxX = dataset.stream().mapToDouble(DataPoint2D::getX).max().orElse(100);
        minY = dataset.stream().mapToDouble(DataPoint2D::getY).min().orElse(0);
        maxY = dataset.stream().mapToDouble(DataPoint2D::getY).max().orElse(100);
        
        // Add some padding to the range
        double xPadding = (maxX - minX) * 0.1;
        double yPadding = (maxY - minY) * 0.1;
        
        minX -= xPadding;
        maxX += xPadding;
        minY -= yPadding;
        maxY += yPadding;
    }
    
    private void redrawChart() {
        if (chartCanvas == null) return;
        
        GraphicsContext gc = chartCanvas.getGraphicsContext2D();
        double width = chartCanvas.getWidth();
        double height = chartCanvas.getHeight();
        
        // Clear canvas
        gc.clearRect(0, 0, width, height);
        gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0, 0, width, height);
        
        // Draw chart area background
        double chartWidth = width - MARGIN_LEFT - MARGIN_RIGHT;
        double chartHeight = height - MARGIN_TOP - MARGIN_BOTTOM;
        
        gc.setFill(CHART_AREA_COLOR);
        gc.fillRoundRect(MARGIN_LEFT, MARGIN_TOP, chartWidth, chartHeight, 10, 10);
        
        // Draw grid
        drawGrid(gc, chartWidth, chartHeight);
        
        // Draw axes
        drawAxes(gc, chartWidth, chartHeight);
        
        // Draw data points
        drawDataPoints(gc, chartWidth, chartHeight);
        
        // Draw axis labels
        drawAxisLabels(gc, width, height, chartWidth, chartHeight);
    }
    
    private void drawGrid(GraphicsContext gc, double chartWidth, double chartHeight) {
        gc.setStroke(GRID_COLOR);
        gc.setLineWidth(0.3); // Make grid lines more subtle
        
        // Vertical grid lines
        int verticalLines = Math.min(10, dataset.size()); // Match data points
        if (verticalLines > 0) {
            for (int i = 0; i <= verticalLines; i++) {
                double x = MARGIN_LEFT + (chartWidth * i / verticalLines);
                gc.strokeLine(x, MARGIN_TOP, x, MARGIN_TOP + chartHeight);
            }
        }
        
        // Horizontal grid lines
        int horizontalLines = 4; // Match Y-axis labels
        for (int i = 0; i <= horizontalLines; i++) {
            double y = MARGIN_TOP + (chartHeight * i / horizontalLines);
            gc.strokeLine(MARGIN_LEFT, y, MARGIN_LEFT + chartWidth, y);
        }
    }
    
    private void drawAxes(GraphicsContext gc, double chartWidth, double chartHeight) {
        gc.setStroke(AXIS_COLOR);
        gc.setLineWidth(2);
        
        // X axis
        gc.strokeLine(MARGIN_LEFT, MARGIN_TOP + chartHeight, MARGIN_LEFT + chartWidth, MARGIN_TOP + chartHeight);
        
        // Y axis
        gc.strokeLine(MARGIN_LEFT, MARGIN_TOP, MARGIN_LEFT, MARGIN_TOP + chartHeight);
    }
    
    private void drawDataPoints(GraphicsContext gc, double chartWidth, double chartHeight) {
        if (dataset.isEmpty()) return;
        
        gc.setFill(DATA_POINT_COLOR);
        gc.setStroke(BAR_BORDER_COLOR);
        gc.setLineWidth(1);
        
        // Calculate bar width based on available space and number of data points
        double barWidth = (chartWidth * 0.8) / dataset.size(); // Use 80% of chart width
        double barSpacing = (chartWidth * 0.2) / (dataset.size() + 1); // Remaining 20% for spacing
        
        for (int i = 0; i < dataset.size(); i++) {
            DataPoint2D point = dataset.get(i);
            
            // Calculate bar position
            double x = MARGIN_LEFT + barSpacing + (i * (barWidth + barSpacing));
            double barHeight = ((point.getY() - minY) / (maxY - minY)) * chartHeight;
            double y = MARGIN_TOP + chartHeight - barHeight;
            
            // Draw bar with rounded corners
            gc.fillRoundRect(x, y, barWidth, barHeight, 4, 4);
            gc.strokeRoundRect(x, y, barWidth, barHeight, 4, 4);
        }
    }
    
    private void drawAxisLabels(GraphicsContext gc, double width, double height, double chartWidth, double chartHeight) {
        gc.setFill(TEXT_COLOR);
        gc.setFont(Font.font("Alatsi", 12));
        
        // X axis labels - show day indices as integers
        int maxXLabels = Math.min(10, dataset.size()); // Limit to 10 labels to avoid crowding
        if (maxXLabels > 0) {
            for (int i = 0; i <= maxXLabels; i++) {
                double x = MARGIN_LEFT + (chartWidth * i / maxXLabels);
                double value = minX + ((maxX - minX) * i / maxXLabels);
                String label = String.format("%.0f", value); // Show as integer
                gc.fillText(label, x - 10, height - MARGIN_BOTTOM + 20);
            }
        }
        
        // Y axis labels - show percentages
        int yLabels = 4;
        for (int i = 0; i <= yLabels; i++) {
            double y = MARGIN_TOP + chartHeight - (chartHeight * i / yLabels);
            double value = minY + ((maxY - minY) * i / yLabels);
            String label = String.format("%.0f%%", value); // Show as percentage
            gc.fillText(label, MARGIN_LEFT - 45, y + 5);
        }
    }
    
    /**
     * Data point class for 2D coordinates.
     */
    public static class DataPoint2D {
        private final double x;
        private final double y;
        
        public DataPoint2D(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        public double getX() {
            return x;
        }
        
        public double getY() {
            return y;
        }
        
        @Override
        public String toString() {
            return String.format("(%.2f, %.2f)", x, y);
        }
    }
}