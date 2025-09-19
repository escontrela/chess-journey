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
import java.util.ArrayList;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Interpolator;
import javafx.util.Duration;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * A custom 2D chart control that displays data points in a 2D coordinate system.
 * This control allows visualization of datasets with X and Y dimensions.
 */
public class Chart2DController extends Pane {
    
    @FXML private Canvas chartCanvas;
    
    private final ObservableList<DataPoint2D> dataset = FXCollections.observableArrayList();
    private final List<String> xAxisLabels = new ArrayList<>();
    
    // Chart styling constants matching the app theme
    // Fondo general transparente
    private static final Color BACKGROUND_COLOR = Color.TRANSPARENT;
    private static final Color GRID_COLOR = Color.web("#404040");
    private static final Color AXIS_COLOR = Color.web("#b3b3b3");
    private static final Color DATA_POINT_COLOR = Color.web("#3b82f6"); // Blue gradient from theme
    private static final Color BAR_BORDER_COLOR = Color.web("#4f46e5"); // Darker blue for contrast
    private static final Color TEXT_COLOR = Color.web("#ffffff");
    // Área del chart transparente
    private static final Color CHART_AREA_COLOR = Color.TRANSPARENT;

    // Chart margins and padding
    private static final double MARGIN_LEFT = 60;
    private static final double MARGIN_RIGHT = 30;
    private static final double MARGIN_TOP = 30;
    private static final double MARGIN_BOTTOM = 60;
    
    // Data range variables
    private double minX = 0, maxX = 100;
    private double minY = 0, maxY = 100;
    
    // Animación: progreso 0..1 usado para escalar la altura de las barras
    private final DoubleProperty animationProgress = new SimpleDoubleProperty(1.0);
    private Timeline showTimeline;

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
        
        // Ensure initial animation state is completed (no animation at startup unless dataset set)
        animationProgress.set(1.0);

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
        playShowAnimation();
    }
    
    /**
     * Sets the dataset with custom X-axis labels for the chart and redraws it.
     * @param dataPoints List of data points to display
     * @param xLabels List of string labels for X-axis (should match dataPoints size)
     */
    public void setDataset(List<DataPoint2D> dataPoints, List<String> xLabels) {
        dataset.clear();
        xAxisLabels.clear();
        if (dataPoints != null) {
            dataset.addAll(dataPoints);
            calculateDataRange();
        }
        if (xLabels != null) {
            xAxisLabels.addAll(xLabels);
        }
        playShowAnimation();
    }
    
    /**
     * Adds a single data point to the chart.
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void addDataPoint(double x, double y) {
        dataset.add(new DataPoint2D(x, y));
        calculateDataRange();
        playShowAnimation();
    }
    
    /**
     * Clears all data points from the chart.
     */
    public void resetDataset() {
        dataset.clear();
        xAxisLabels.clear();
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

        // Evitar división por cero si los valores son iguales
        if (maxX <= minX) {
            maxX = minX + 1;
        }
        if (maxY <= minY) {
            maxY = minY + 1;
        }
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
        
        // Draw axes
        drawAxes(gc, chartWidth, chartHeight);
        
        // Draw data points
        drawDataPoints(gc, chartWidth, chartHeight);
        
        // Draw axis labels
        drawAxisLabels(gc, width, height, chartWidth, chartHeight);
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
        
        gc.setLineWidth(1);
        // Hacer barras más estrechas: usar 60% del ancho para barras y 40% para espaciado
        double barWidth = (chartWidth * 0.6) / Math.max(1, dataset.size());
        double barSpacing = (chartWidth * 0.4) / (dataset.size() + 1);

        for (int i = 0; i < dataset.size(); i++) {
            DataPoint2D point = dataset.get(i);
            
            // Calculate bar position
            double x = MARGIN_LEFT + barSpacing + (i * (barWidth + barSpacing));
            double rawBarHeight = ((point.getY() - minY) / (maxY - minY)) * chartHeight;
            // Escalar la altura según el progreso de la animación (0..1)
            double barHeight = rawBarHeight * animationProgress.get();
            double y = MARGIN_TOP + chartHeight - barHeight;
            
            // Gradiente de abajo hacia arriba: color más oscuro en la base, más claro arriba
            Stop[] stops = new Stop[] {
                new Stop(0, BAR_BORDER_COLOR), // bottom
                new Stop(1, DATA_POINT_COLOR)  // top
            };
            LinearGradient lg = new LinearGradient(0, 1, 0, 0, true, CycleMethod.NO_CYCLE, stops);
            gc.setFill(lg);
            gc.fillRoundRect(x, y, barWidth, barHeight, 4, 4);
            gc.setStroke(BAR_BORDER_COLOR);
            gc.strokeRoundRect(x, y, barWidth, barHeight, 4, 4);
        }
    }
    
    private void drawAxisLabels(GraphicsContext gc, double width, double height, double chartWidth, double chartHeight) {
        gc.setFill(TEXT_COLOR);
        gc.setFont(Font.font("Alatsi", 12));
        
        // Compute bar sizing to center labels
        double barWidth = (chartWidth * 0.6) / Math.max(1, dataset.size());
        double barSpacing = (chartWidth * 0.4) / (dataset.size() + 1);

        // Decide if X values look like epoch timestamps (seconds or milliseconds)
        boolean epochMillis = (maxX > 1e11) || (minX > 1e11);
        boolean epochSeconds = !epochMillis && ((maxX > 1e9) || (minX > 1e9));

        // X axis labels - use custom labels if available, otherwise show formatted values
        if (!xAxisLabels.isEmpty()) {
            // Show up to 10 labels for better readability
            int maxLabels = Math.min(10, xAxisLabels.size());
            int step = Math.max(1, xAxisLabels.size() / maxLabels);
            
            for (int i = 0; i < xAxisLabels.size(); i += step) {
                if (i >= xAxisLabels.size()) break;
                // Center label on the bar's center
                double x = MARGIN_LEFT + barSpacing + (i * (barWidth + barSpacing)) + (barWidth / 2);
                String label = xAxisLabels.get(i);
                // Try to parse known numeric date formats (01/09) and convert to 01/Sep
                String formatted = tryParseAndFormatLabel(label);
                // Center the label text
                double approxChar = gc.getFont().getSize() * 0.6;
                double textWidth = formatted.length() * approxChar;
                gc.fillText(formatted, x - textWidth / 2, height - MARGIN_BOTTOM + 20);
            }
        } else {
            // Fallback to original behavior but format as date when X looks like epoch
            int maxXLabels = Math.min(10, dataset.size()); // Limit to 10 labels to avoid crowding
            if (maxXLabels > 0) {
                for (int i = 0; i <= maxXLabels; i++) {
                    // Choose an index to center over bars
                    int index = Math.min(dataset.size() - 1, Math.max(0, (int)Math.round((double)i * (dataset.size()-1) / maxXLabels)));
                    double x = MARGIN_LEFT + barSpacing + (index * (barWidth + barSpacing)) + (barWidth / 2);
                    double value = minX + ((maxX - minX) * i / maxXLabels);
                    String label;
                    if (epochMillis || epochSeconds) {
                        long epochMs = (epochMillis) ? (long)Math.round(value) : (long)Math.round(value * 1000.0);
                        label = formatEpochToLabel(epochMs);
                    } else {
                        // Fallback numeric label
                        label = String.format("%.0f", value);
                    }
                    double approxChar = gc.getFont().getSize() * 0.6;
                    double textWidth = label.length() * approxChar;
                    gc.fillText(label, x - textWidth / 2, height - MARGIN_BOTTOM + 20);
                }
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
    
    // Crea y reproduce la animación de "elevación" de las barras
    private void playShowAnimation() {
        if (showTimeline != null) {
            showTimeline.stop();
        }
        animationProgress.set(0.0);
        showTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(animationProgress, 0.0)),
            new KeyFrame(Duration.millis(650), new KeyValue(animationProgress, 1.0, Interpolator.EASE_OUT))
        );
        // Redibujar en cada frame para actualizar las barras
        animationProgress.addListener((obs, oldV, newV) -> redrawChart());
        showTimeline.play();
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

    /**
     * Intenta parsear etiquetas en formato dd/MM, dd/MM/yy o dd/MM/yyyy y devolver "dd/MMM" (ej. 01/Sep).
     * Si no se puede parsear, devuelve el label original.
     */
    private String tryParseAndFormatLabel(String label) {
        if (label == null) return "";
        label = label.trim();
        // Primero intentar el caso simple dd/MM (p. ej. "01/09" o "1/9")
        if (label.matches("^\\d{1,2}\\s*[/]\\s*\\d{1,2}$")) {
            try {
                String[] parts = label.split("/");
                int day = Integer.parseInt(parts[0].trim());
                int month = Integer.parseInt(parts[1].trim());
                LocalDate date = LocalDate.of(LocalDate.now().getYear(), month, day);
                DateTimeFormatter out = DateTimeFormatter.ofPattern("dd/MMM", Locale.ENGLISH);
                return date.format(out);
            } catch (Exception ex) {
                // fallback a intentar otros patrones
            }
        }
        // Intentar otros patrones más completos
        String[] patterns = new String[] { "dd/MM/yy", "dd/MM/yyyy" };
        for (String p : patterns) {
            try {
                DateTimeFormatter parser = DateTimeFormatter.ofPattern(p).withLocale(Locale.ENGLISH);
                LocalDate date = LocalDate.parse(label, parser);
                DateTimeFormatter out = DateTimeFormatter.ofPattern("dd/MMM", Locale.ENGLISH);
                return date.format(out);
            } catch (DateTimeParseException ex) {
                // intentar siguiente patrón
            }
        }
        return label;
    }

    /**
     * Formatea epoch milliseconds a "dd/MMM" (ej. 01/Sep).
     */
    private String formatEpochToLabel(long epochMillis) {
        LocalDate date = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter out = DateTimeFormatter.ofPattern("dd/MMM", Locale.ENGLISH);
        return date.format(out);
    }
}

