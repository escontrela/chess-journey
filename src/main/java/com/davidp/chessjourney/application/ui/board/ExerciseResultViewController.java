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

        lblPercent.setText("?");
        progressBarFill.setOpacity(0.0);

        // Ocultar y desactivar el botón Ok mientras se ejecuta la animación
        btOk.setVisible(false);
        btOk.setDisable(true);
        btOk.setOpacity(0.0);

        if (inputData.isLayoutInfoValid()) {
            setLayout(inputData.getLayoutX(), inputData.getLayoutY());
        }
        
        this.exerciseResultViewInputScreenData = (ExerciseResultViewInputScreenData) inputData;
        
        // Add a small delay before starting the animation
        Timeline startDelay = new Timeline(new KeyFrame(Duration.millis(300), e -> {
            showProgressBarProgressively(this.exerciseResultViewInputScreenData.getPercentage());
        }));
        startDelay.play();
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

    private void showProgressBarProgressively(double percent) {
        // Reset progress bar
        progressBarFill.setOpacity(0.0);
        progressBarFill.getPoints().clear();

        double barWidth = 312.0;
        double barHeight = 27.0;
        double targetFillWidth = (percent / 100.0) * barWidth;

        double cornerRadius = 8.0;
        double arcSize = cornerRadius * 2.0;

        // Start empty
        progressBarFill.getPoints().addAll(new Double[]{
                0.0, 0.0,
                0.0, 0.0,
                0.0, barHeight,
                0.0, barHeight
        });

        // Ensure rounded clip
        javafx.scene.shape.Rectangle clip;
        if (progressBarFill.getClip() instanceof javafx.scene.shape.Rectangle) {
            clip = (javafx.scene.shape.Rectangle) progressBarFill.getClip();
        } else {
            clip = new javafx.scene.shape.Rectangle(0, 0, 0, barHeight);
            clip.setArcWidth(arcSize);
            clip.setArcHeight(arcSize);
            progressBarFill.setClip(clip);
        }

        // Gradiente izquierda->derecha: blanco -> color final (#3B82F6)
        javafx.scene.paint.LinearGradient gradient = new javafx.scene.paint.LinearGradient(
                0, 0, 1, 0, true,
                javafx.scene.paint.CycleMethod.NO_CYCLE,
                java.util.Arrays.asList(
                        new javafx.scene.paint.Stop(0.0, javafx.scene.paint.Color.web("#FFFFFF")),
                        new javafx.scene.paint.Stop(1.0, javafx.scene.paint.Color.web("#3B82F6"))
                )
        );
        progressBarFill.setFill(gradient);

        // Start blinking label (show "?" blinking until finished)
        lblPercent.setText("?");
        lblPercent.setVisible(true);
        Timeline blinkTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> lblPercent.setVisible(!lblPercent.isVisible())));
        blinkTimeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        blinkTimeline.play();

        progressBarFill.setOpacity(1.0);

        Timeline progressAnimation = new Timeline();

        int animationSteps = 30;
        double stepDuration = 70; // <- aumentado a 100 ms por paso para una animación más lenta

        for (int i = 1; i <= animationSteps; i++) {
            final int step = i;
            double currentFillWidth = (targetFillWidth / animationSteps) * step;

            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(stepDuration * step),
                    e -> {
                        // Update polygon points (rectangular)
                        progressBarFill.getPoints().setAll(
                                0.0, 0.0,
                                currentFillWidth, 0.0,
                                currentFillWidth, barHeight,
                                0.0, barHeight
                        );
                        // Update rounded clip so visible area has rounded corners
                        clip.setWidth(Math.max(1e-6, currentFillWidth));
                        clip.setHeight(barHeight);
                    }
            );
            progressAnimation.getKeyFrames().add(keyFrame);
        }

        progressAnimation.setOnFinished(e -> {
            // Stop blinking and ensure label visible, then set final text and play sound
            blinkTimeline.stop();
            lblPercent.setVisible(true);
            runLater(() -> {
                lblPercent.setText(String.format("%.0f%%", percent));
                soundService.playSound(SoundServiceFactory.SoundType.SUCCEED_EXERCISE);
                // Habilitar y mostrar el botón OK con un fade-in
                btOk.setDisable(false);
                btOk.setVisible(true);
                JavaFXAnimationUtil.animationBuilder()
                        .duration(Duration.seconds(0.25))
                        .fadeIn(btOk)
                        .buildAndPlay();
            });
        });

        progressAnimation.play();
    }


}