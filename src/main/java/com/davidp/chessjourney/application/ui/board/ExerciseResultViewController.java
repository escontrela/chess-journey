package com.davidp.chessjourney.application.ui.board;

import com.davidp.chessjourney.application.factories.SoundServiceFactory;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.util.JavaFXAnimationUtil;
import com.davidp.chessjourney.application.util.JavaFXGameTimerUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import static javafx.application.Platform.runLater;

public class ExerciseResultViewController implements ScreenController {

    private ScreenController.ScreenStatus status;

    private ExerciseResultViewInputScreenData exerciseResultViewInputScreenData;

    private final SoundServiceFactory soundService = SoundServiceFactory.getInstance();

    @FXML
    private Button btOk;

    @FXML
    private ImageView imgOk;

    @FXML
    private Label lblPercent;

    @FXML
    private Label lblPercentageText;

    @FXML
    private Pane rootPane;

    @FXML
    private Pane percentageSquarePane;

    @FXML
    private Pane progressBarContainer;

    @FXML
    private Pane progressBarBackground;

    @FXML
    private Polygon progressBarFill;

    @FXML
    void keyPressed(KeyEvent event) {

    }

    @FXML
    void mouseClicked(MouseEvent event) {

    }

    public void initialize() {

        status = ScreenController.ScreenStatus.INITIALIZED;

    }

    @Override
    public void setData(InputScreenData inputData) {

        if (inputData == null) {
            return;
        }

        lblPercent.setText("...");
        progressBarFill.setOpacity(0.0);

        if (inputData.isLayoutInfoValid()) {
            setLayout(inputData.getLayoutX(), inputData.getLayoutY());
        }
        
        this.exerciseResultViewInputScreenData = (ExerciseResultViewInputScreenData) inputData;
        showStarsProgressively(this.exerciseResultViewInputScreenData.getPercentage());
    }

    @Override
    public void setLayout(double layoutX, double layoutY) {

        rootPane.setLayoutX(layoutX);
        rootPane.setLayoutY(layoutY);
    }

    @Override
    public void show() {

        rootPane.setVisible(false);

        // Fade in animation when showing (custom util)
        JavaFXAnimationUtil.animationBuilder()
                .duration(Duration.seconds(0.2))
                .onFinished(() -> {
                    rootPane.setVisible(true);
                    rootPane.toFront();
                })
                .fadeIn(rootPane)
                .buildAndPlay();
    }

    @Override
    public void show(InputScreenData inputData) {

        setData(inputData);
        status = ScreenController.ScreenStatus.VISIBLE;
        show();
    }

    @Override
    public void hide() {

        JavaFXAnimationUtil.animationBuilder()
                .duration(Duration.seconds(0.2))
                .onFinished(() -> {
                    rootPane.setVisible(false);
                    status = ScreenStatus.HIDDEN;
                })
                .fadeOut(rootPane)
                .buildAndPlay();
    }


    @Override
    public boolean isVisible() {
        return rootPane.isVisible();
    }

    @Override
    public boolean isHidden() {
        return !rootPane.isVisible();
    }

    @Override
    public Pane getRootPane() {
        return rootPane;
    }

    @Override
    public ScreenStatus getStatus() {
        return null;
    }

    @Override
    public boolean isInitialized() {

        return status == ScreenController.ScreenStatus.INITIALIZED;
    }

    protected void setExerciseResultViewInputScreenData(ExerciseResultViewInputScreenData exerciseResultViewInputScreenData) {

        this.exerciseResultViewInputScreenData = exerciseResultViewInputScreenData;
    }

    protected ExerciseResultViewInputScreenData getExerciseResultViewInputScreenData() {

        return exerciseResultViewInputScreenData;
    }

    @FXML
    void buttonAction(ActionEvent event) {

        if (event.getSource() == btOk) {

            hide();
        }

    }

    /**
     * Show progress bar progressively based on the percentage
     * @param percent percentage of success to show
     */
    private void showStarsProgressively(double percent) {
        
        // Reset progress bar
        progressBarFill.setOpacity(0.0);
        progressBarFill.getPoints().clear();
        
        // Create rectangular progress bar shape (initially empty)
        double barWidth = 316.0; // Total width minus margin
        double barHeight = 31.0; // Total height minus margin
        double fillWidth = (percent / 100.0) * barWidth;
        
        // Define the initial empty rectangle
        progressBarFill.getPoints().addAll(new Double[]{
            0.0, 0.0,
            0.0, 0.0,
            0.0, barHeight,
            0.0, barHeight
        });
        
        // Make progress bar visible
        progressBarFill.setOpacity(1.0);
        
        // Animate the progress bar growth
        Timeline progressAnimation = new Timeline();
        progressAnimation.getKeyFrames().add(
            new KeyFrame(Duration.millis(1500), e -> {
                // Update the polygon to show full progress
                progressBarFill.getPoints().clear();
                progressBarFill.getPoints().addAll(new Double[]{
                    0.0, 0.0,
                    fillWidth, 0.0,
                    fillWidth, barHeight,
                    0.0, barHeight
                });
            })
        );
        
        // After progress bar animation, show percentage and play sound
        progressAnimation.setOnFinished(e -> {
            runLater(() -> {
                lblPercent.setText(String.format("%.0f%%", percent));
                soundService.playSound(SoundServiceFactory.SoundType.SUCCEED_EXERCISE);
            });
        });
        
        progressAnimation.play();
    }
}