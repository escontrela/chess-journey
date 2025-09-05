# Chart2D Control

A custom 2D chart control for the Chess Journey application that allows visualization of datasets with X and Y dimensions.

## Features

- **Custom 2D visualization**: Displays data points in a 2D coordinate system without using JavaFX built-in chart controls
- **Resizable**: Automatically adapts to container size changes
- **Dataset management**: Methods to set, add, and reset data points
- **App-themed styling**: Follows the Chess Journey application's dark theme and styling
- **Grid and axes**: Automatic grid lines and axis labeling
- **Data range calculation**: Automatically calculates and adjusts to data range with padding

## Usage

### Basic Programmatic Usage

```java
// Create the chart control
Chart2DController chart = new Chart2DController();
chart.setPrefSize(600, 400);

// Create sample data
List<Chart2DController.DataPoint2D> data = new ArrayList<>();
data.add(new Chart2DController.DataPoint2D(10, 20));
data.add(new Chart2DController.DataPoint2D(20, 35));
data.add(new Chart2DController.DataPoint2D(30, 25));

// Set the dataset
chart.setDataset(data);

// Add to your scene
container.getChildren().add(chart);
```

### FXML Usage

You can use the chart control directly in FXML:

```xml
<!-- Direct usage in FXML -->
<Chart2DController fx:id="myChart" prefWidth="600" prefHeight="400" />
```

Then access it in your controller:

```java
@FXML private Chart2DController myChart;

@FXML
private void initialize() {
    // Use the chart
    myChart.addDataPoint(10, 20);
}
```

### Integration with Existing Data

For integration with existing Chess Journey data sources like `AggregatedStats`:

```java
public void updateChartWithStats(List<AggregatedStats> stats) {
    List<Chart2DController.DataPoint2D> chartData = new ArrayList<>();
    
    for (int i = 0; i < stats.size(); i++) {
        AggregatedStats stat = stats.get(i);
        chartData.add(new Chart2DController.DataPoint2D(i, stat.getValue()));
    }
    
    customChart.setDataset(chartData);
}
```

### Methods

- `setDataset(List<DataPoint2D> dataPoints)` - Sets the complete dataset
- `addDataPoint(double x, double y)` - Adds a single data point
- `resetDataset()` - Clears all data points
- `getDataset()` - Returns the current dataset as ObservableList
- `setDataRange(double minX, double maxX, double minY, double maxY)` - Manually set axis ranges

### DataPoint2D Class

The `DataPoint2D` class represents a single point in the 2D space:

```java
DataPoint2D point = new DataPoint2D(x, y);
double xValue = point.getX();
double yValue = point.getY();
```

## Styling

The control follows the Chess Journey application styling:
- Dark theme background with gradients
- Blue accent colors for data points
- Rounded corners and subtle shadows
- "Alatsi" font family for labels
- Grid lines and axes in contrasting colors

## Demo

Run the `Chart2DDemo` application to see the control in action:

```bash
java com.davidp.chessjourney.application.ui.controls.demo.Chart2DDemo
```

The demo shows:
- Sample data visualization
- Adding random points
- Resetting the chart
- Real-time updates as data changes

### Example Files

- `Chart2DDemo.java` - Standalone demo application
- `UserStatsChart2DIntegration.java` - Integration example with existing UserStats data
- `chart-2d-demo.fxml` - FXML example with programmatic chart creation
- `chart-2d-simple.fxml` - Simple FXML example with direct chart declaration

## Implementation Details

The control follows the Chess Journey pattern:
- Extends `Pane` for container functionality
- Uses `FXMLLoader` to load associated FXML layout
- Implements `@FXML` initialization method
- Uses `Canvas` for custom drawing instead of built-in JavaFX charts
- Automatically handles resizing through property binding
- Maintains dataset as `ObservableList` for reactive updates

## Integration Notes

The chart can be easily integrated into existing Chess Journey screens like `UserStatsController` by:
1. Adding the chart to the existing FXML layout
2. Converting `AggregatedStats` data to `DataPoint2D` format
3. Using existing styling classes and color scheme
4. Following the same event handling patterns as other controls