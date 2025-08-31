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

### Basic Usage

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