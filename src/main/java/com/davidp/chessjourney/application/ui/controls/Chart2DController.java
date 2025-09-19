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
    // NUEVO: soporte multi-serie (tomamos como máximo 2)
    private final List<ObservableList<DataPoint2D>> datasets = new ArrayList<>();
    // NUEVO: nombres de series (máx. 2) y título
    private final List<String> seriesNames = new ArrayList<>();
    private String chartTitle = "";

    // Chart styling constants matching the app theme

    private static final Color BACKGROUND_COLOR = Color.TRANSPARENT;
    private static final Color AXIS_COLOR = Color.web("#b3b3b3");
    private static final Color DATA_POINT_COLOR = Color.web("#3b82f6"); // Azul
    private static final Color BAR_BORDER_COLOR = Color.web("#4f46e5"); // Azul oscuro
    private static final Color TEXT_COLOR = Color.web("#ffffff");

    private static final Color CHART_AREA_COLOR = Color.TRANSPARENT;

    // Colores por serie (2 series máximo: azul, verde)
    private static final Color[] SERIES_FILL_COLORS = new Color[] {
        Color.web("#3b82f6"), // azul
        Color.web("#10b981")  // verde
    };
    private static final Color[] SERIES_BORDER_COLORS = new Color[] {
        Color.web("#1d4ed8"), // azul oscuro
        Color.web("#047857")  // verde oscuro
    };

    // Chart margins and padding
    private static final double MARGIN_LEFT = 60;
    private static final double MARGIN_RIGHT = 30;
    private static final double MARGIN_TOP = 30;
    private static final double MARGIN_BOTTOM = 60;
    
    // Data range variables
    private double minX = 0, maxX = 100;
    private double minY = 0, maxY = 100;
    
    // Animation progress
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
     * Establece múltiples datasets (hasta 2). Si se pasan más, se ignoran a partir del tercero.
     * Cada lista interna representa una serie, y cada índice i representa el mismo punto X.
     */
    public void setDatasets(List<List<DataPoint2D>> multi) {
        // Limpiar ambos modos para evitar estados mezclados
        dataset.clear();
        datasets.clear();

        if (multi != null && !multi.isEmpty()) {
            int take = Math.min(2, multi.size());
            for (int i = 0; i < take; i++) {
                List<DataPoint2D> serie = multi.get(i);
                if (serie != null) {
                    datasets.add(FXCollections.observableArrayList(serie));
                } else {
                    datasets.add(FXCollections.observableArrayList());
                }
            }
        }
        calculateDataRange();
        playShowAnimation();
    }

    /**
     * Establece múltiples datasets (hasta 2) junto con etiquetas del eje X.
     */
    public void setDatasets(List<List<DataPoint2D>> multi, List<String> xLabels) {
        setDatasets(multi);
        setXAxisLabels(xLabels);
    }

    /**
     * Establece etiquetas del eje X (útil tanto para una serie como para multi-serie).
     */
    public void setXAxisLabels(List<String> labels) {
        xAxisLabels.clear();
        if (labels != null) {
            xAxisLabels.addAll(labels);
        }
        redrawChart();
    }

    /**
     * Clears all data points from the chart.
     */
    public void resetDataset() {
        dataset.clear();
        datasets.clear(); // limpiar también multi-serie
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

    /**
     * Establece los nombres de las series (máximo 2). Si faltan, se usarán nombres por defecto.
     */
    public void setSeriesNames(List<String> names) {
        seriesNames.clear();
        if (names != null) {
            int take = Math.min(2, names.size());
            for (int i = 0; i < take; i++) {
                String n = names.get(i);
                seriesNames.add(n != null ? n : "");
            }
        }
        redrawChart();
    }

    /**
     * Establece el título de la gráfica (esquina superior izquierda).
     */
    public void setChartTitle(String title) {
        this.chartTitle = title != null ? title : "";
        redrawChart();
    }

    /**
     * Calculates the data range based on the current dataset(s).
     */
    private void calculateDataRange() {

        // Si hay multi-serie, calcular sobre todas las series
        if (!datasets.isEmpty()) {
            // Buscar primer dataset no vacío como base
            ObservableList<DataPoint2D> base = null;
            for (ObservableList<DataPoint2D> ds : datasets) {
                if (ds != null && !ds.isEmpty()) { base = ds; break; }
            }
            if (base == null) {
                // No hay datos
                minX = 0; maxX = 1; minY = 0; maxY = 1;
                return;
            }
            minX = base.stream().mapToDouble(DataPoint2D::getX).min().orElse(0);
            maxX = base.stream().mapToDouble(DataPoint2D::getX).max().orElse(100);
            minY = base.stream().mapToDouble(DataPoint2D::getY).min().orElse(0);
            maxY = base.stream().mapToDouble(DataPoint2D::getY).max().orElse(100);

            for (ObservableList<DataPoint2D> ds : datasets) {
                if (ds == null || ds.isEmpty()) continue;
                minX = Math.min(minX, ds.stream().mapToDouble(DataPoint2D::getX).min().orElse(minX));
                maxX = Math.max(maxX, ds.stream().mapToDouble(DataPoint2D::getX).max().orElse(maxX));
                minY = Math.min(minY, ds.stream().mapToDouble(DataPoint2D::getY).min().orElse(minY));
                maxY = Math.max(maxY, ds.stream().mapToDouble(DataPoint2D::getY).max().orElse(maxY));
            }
        } else {
            // modo una sola serie (existente)
            if (dataset.isEmpty()) {
                return;
            }

            minX = dataset.stream().mapToDouble(DataPoint2D::getX).min().orElse(0);
            maxX = dataset.stream().mapToDouble(DataPoint2D::getX).max().orElse(100);
            minY = dataset.stream().mapToDouble(DataPoint2D::getY).min().orElse(0);
            maxY = dataset.stream().mapToDouble(DataPoint2D::getY).max().orElse(100);
        }

        // Padding
        double xPadding = (maxX - minX) * 0.1;
        double yPadding = (maxY - minY) * 0.1;
        
        minX -= xPadding;
        maxX += xPadding;
        minY -= yPadding;
        maxY += yPadding;

        // Rango válido
        if (maxX <= minX) {
            maxX = minX + 1;
        }
        if (maxY <= minY) {
            maxY = minY + 1;
        }
    }

    /**
     * Redraws the entire bar chart on the canvas.
     */
    private void redrawChart() {

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

        // NUEVO: título y leyenda
        drawTitle(gc, width);
        drawLegend(gc, width, height, chartWidth, chartHeight);
    }

    /**
     * Draws the X and Y axes on the chart.
     * @param gc is the GraphicsContext to draw on
     * @param chartWidth is the width of the chart area
     * @param chartHeight is the height of the chart area
     */
    private void drawAxes(GraphicsContext gc, double chartWidth, double chartHeight) {

        gc.setStroke(AXIS_COLOR);
        gc.setLineWidth(1);
        // X axis
        gc.strokeLine(MARGIN_LEFT, MARGIN_TOP + chartHeight, MARGIN_LEFT + chartWidth, MARGIN_TOP + chartHeight);
        // Y axis
        gc.strokeLine(MARGIN_LEFT, MARGIN_TOP, MARGIN_LEFT, MARGIN_TOP + chartHeight);
    }

    /**
     * Draws the data points as bars on the chart.
     */
    private void drawDataPoints(GraphicsContext gc, double chartWidth, double chartHeight) {

        // Modo multi-serie (hasta 2)
        if (!datasets.isEmpty()) {
            // Número de puntos = máximo tamaño entre series
            int numSeries = Math.min(2, datasets.size());
            int maxPoints = 0;
            for (int s = 0; s < numSeries; s++) {
                maxPoints = Math.max(maxPoints, datasets.get(s).size());
            }
            if (maxPoints == 0) return;

            gc.setLineWidth(1);
            double totalBarGroupWidth = (chartWidth * 0.8) / maxPoints; // 80% para barras
            double groupSpacing = (chartWidth * 0.2) / (maxPoints + 1);   // 20% para espacios

            // Gap sutil entre barras del mismo grupo
            double intraGap = (numSeries <= 1) ? 0.0 : Math.max(1.0, totalBarGroupWidth * 0.06);
            double barsTotalWidth = Math.max(0.0, totalBarGroupWidth - intraGap * (numSeries - 1));
            double barWidth = (numSeries > 0) ? (barsTotalWidth / numSeries) : 0.0;             // ancho por serie dentro del grupo

            for (int i = 0; i < maxPoints; i++) {
                double groupX = MARGIN_LEFT + groupSpacing + (i * (totalBarGroupWidth + groupSpacing));
                for (int s = 0; s < numSeries; s++) {
                    ObservableList<DataPoint2D> ds = datasets.get(s);
                    if (i >= ds.size()) continue; // esta serie no tiene este punto
                    DataPoint2D point = ds.get(i);

                    double rawBarHeight = ((point.getY() - minY) / (maxY - minY)) * chartHeight;
                    double barHeight = rawBarHeight * animationProgress.get();
                    double x = groupX + s * (barWidth + intraGap);
                    double y = MARGIN_TOP + chartHeight - barHeight;

                    Stop[] stops = new Stop[] {
                        new Stop(0, SERIES_BORDER_COLORS[s]),
                        new Stop(1, SERIES_FILL_COLORS[s])
                    };
                    LinearGradient lg = new LinearGradient(0, 1, 0, 0, true, CycleMethod.NO_CYCLE, stops);

                    gc.setFill(lg);
                    gc.fillRoundRect(x, y, barWidth, barHeight, 4, 4);
                    gc.setStroke(SERIES_BORDER_COLORS[s]);
                    gc.strokeRoundRect(x, y, barWidth, barHeight, 4, 4);
                }
            }
            return;
        }

        if (dataset.isEmpty()){
            return;
        }
        
        gc.setLineWidth(1);


        double barWidth = (chartWidth * 0.5) / Math.max(1, dataset.size());
        double barSpacing = (chartWidth * 0.5) / (dataset.size() + 1);

        for (int i = 0; i < dataset.size(); i++) {

            DataPoint2D point = dataset.get(i);
            
            // Calculate bar position
            double x = MARGIN_LEFT + barSpacing + (i * (barWidth + barSpacing));
            double rawBarHeight = ((point.getY() - minY) / (maxY - minY)) * chartHeight;

            // Scale  bar height by animation progress (0.0 to 1.0)
            double barHeight = rawBarHeight * animationProgress.get();
            double y = MARGIN_TOP + chartHeight - barHeight;
            
            // Draw bar with gradient fill
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

    /**
     * Draws the axis labels on the chart.
     */
    private void drawAxisLabels(GraphicsContext gc, double width, double height, double chartWidth, double chartHeight) {

        gc.setFill(TEXT_COLOR);
        gc.setFont(Font.font("Alatsi", 12));
        
        // Modo multi-serie: centrar etiquetas en el centro del grupo
        if (!datasets.isEmpty()) {
            int numSeries = Math.min(2, datasets.size());
            int maxPoints = 0;
            for (int s = 0; s < numSeries; s++) {
                maxPoints = Math.max(maxPoints, datasets.get(s).size());
            }
            if (maxPoints == 0) return;

            double totalBarGroupWidth = (chartWidth * 0.8) / maxPoints;
            double groupSpacing = (chartWidth * 0.2) / (maxPoints + 1);

            if (!xAxisLabels.isEmpty()) {
                int maxLabels = Math.min(10, Math.min(xAxisLabels.size(), maxPoints));
                int step = Math.max(1, maxPoints / maxLabels);
                for (int i = 0; i < maxPoints; i += step) {
                    if (i >= xAxisLabels.size()) break;
                    double groupCenterX = MARGIN_LEFT + groupSpacing + (i * (totalBarGroupWidth + groupSpacing)) + (totalBarGroupWidth / 2);
                    String formatted = tryParseAndFormatLabel(xAxisLabels.get(i));
                    double approxChar = gc.getFont().getSize() * 0.6;
                    double textWidth = formatted.length() * approxChar;
                    gc.fillText(formatted, groupCenterX - textWidth / 2, height - MARGIN_BOTTOM + 20);
                }
            } else {
                // Fallback: valores numéricos espaciados
                int maxXLabels = Math.min(10, maxPoints);
                if (maxXLabels > 0) {
                    for (int i = 0; i < maxXLabels; i++) {
                        double groupCenterX = MARGIN_LEFT + groupSpacing + (i * (totalBarGroupWidth + groupSpacing)) + (totalBarGroupWidth / 2);
                        double value = minX + ((maxX - minX) * i / maxXLabels);
                        String label = String.format("%.0f", value);
                        double approxChar = gc.getFont().getSize() * 0.6;
                        double textWidth = label.length() * approxChar;
                        gc.fillText(label, groupCenterX - textWidth / 2, height - MARGIN_BOTTOM + 20);
                    }
                }
            }

            // Etiquetas Y (porcentaje)
            int yLabels = 4;
            for (int i = 0; i <= yLabels; i++) {
                double y = MARGIN_TOP + chartHeight - (chartHeight * i / yLabels);
                double value = minY + ((maxY - minY) * i / yLabels);
                String label = String.format("%.0f%%", value);
                gc.fillText(label, MARGIN_LEFT - 45, y + 5);
            }
            return;
        }

        // Compute bar sizing to center labels
        double barWidth = (chartWidth * 0.5) / Math.max(1, dataset.size());
        double barSpacing = (chartWidth * 0.5) / (dataset.size() + 1);

        // Decide if X values look like epoch timestamps (seconds or milliseconds)
        boolean epochMillis = (maxX > 1e11) || (minX > 1e11);
        boolean epochSeconds = !epochMillis && ((maxX > 1e9) || (minX > 1e9));

        // X axis labels - use custom labels if available, otherwise show formatted values
        if (!xAxisLabels.isEmpty()) {

            // Show up to 10 labels for better readability
            int maxLabels = Math.min(10, xAxisLabels.size());
            int step = Math.max(1, xAxisLabels.size() / maxLabels);
            
            for (int i = 0; i < xAxisLabels.size(); i += step) {

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

    /**
     * Dibuja el título en la parte superior izquierda (en el margen superior).
     */
    private void drawTitle(GraphicsContext gc, double width) {

        if (chartTitle == null || chartTitle.isEmpty()){
            return;
        }
        gc.setFill(TEXT_COLOR);
        Font prev = gc.getFont();
        gc.setFont(Font.font("Alatsi", 16));
        double x = MARGIN_LEFT + 10;
        double y = Math.max(30, MARGIN_TOP - 10); // dentro del margen superior
        gc.fillText(chartTitle, x, y);
        gc.setFont(prev);
    }

    /**
     * Dibuja la leyenda debajo del eje X, alineada a la derecha, con hasta 2 elementos.
     */
    private void drawLegend(GraphicsContext gc, double width, double height, double chartWidth, double chartHeight) {
        int numSeries;
        if (!datasets.isEmpty()) {
            numSeries = Math.min(2, datasets.size());
        } else if (!dataset.isEmpty()) {
            numSeries = 1; // modo una sola serie
        } else {
            return; // nada que mostrar
        }

        // Preparar etiquetas por serie
        String[] labels = new String[numSeries];
        for (int i = 0; i < numSeries; i++) {
            String fallback = "Serie " + (i + 1);
            labels[i] = (i < seriesNames.size() && seriesNames.get(i) != null && !seriesNames.get(i).isEmpty())
                ? seriesNames.get(i) : fallback;
        }

        // Medidas
        Font prev = gc.getFont();
        gc.setFont(Font.font("Alatsi", 12));
        double box = 12;
        double gapBoxText = 6;
        double itemSpacing = 20;
        double approxChar = gc.getFont().getSize() * 0.6;

        // Calcular ancho total de leyenda
        double totalLegendWidth = 0;
        for (int i = 0; i < numSeries; i++) {
            totalLegendWidth += box + gapBoxText + labels[i].length() * approxChar;
            if (i < numSeries - 1) totalLegendWidth += itemSpacing;
        }

        double startX = MARGIN_LEFT + chartWidth - totalLegendWidth; // alineado a derecha dentro del área bajo eje X
        double y = height - MARGIN_BOTTOM + 50; // debajo del eje X

        // Dibujar
        double x = startX;
        for (int i = 0; i < numSeries; i++) {

            Color fill = SERIES_FILL_COLORS[i];
            Color border = SERIES_BORDER_COLORS[i];
            gc.setFill(fill);
            gc.fillRect(x, y - box + 2, box, box);
            gc.setStroke(border);
            gc.strokeRect(x, y - box + 2, box, box);
            x += box + gapBoxText;
            gc.setFill(TEXT_COLOR);
            gc.fillText(labels[i], x, y);
            x += labels[i].length() * approxChar + itemSpacing;
        }
        gc.setFont(prev);
    }

    /**
     * Plays the show animation for the chart bars.
     */
    private void playShowAnimation() {

        if (showTimeline != null) {
            showTimeline.stop();
        }
        animationProgress.set(0.0);

        showTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(animationProgress, 0.0)),
            new KeyFrame(Duration.millis(650), new KeyValue(animationProgress, 1.0, Interpolator.EASE_OUT))
        );

        // This listener will redraw the chart as the animation progresses
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
