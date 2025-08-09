package com.davidp.chessjourney.application.ui.board;

import com.davidp.chessjourney.application.factories.SoundServiceFactory;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.util.JavaFXAnimationUtil;
import com.davidp.chessjourney.application.util.JavaFXGameTimerUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.util.Arrays;
import java.util.List;

import static javafx.application.Platform.runLater;

public class ExerciseResultViewController implements ScreenController {

    private ScreenController.ScreenStatus status;

    private ExerciseResultViewInputScreenData exerciseResultViewInputScreenData;

    private final SoundServiceFactory soundService = SoundServiceFactory.getInstance();

    @FXML
    private Button btOk;

    @FXML
    private ImageView imgNotice;

    @FXML
    private ImageView imgOk;

    @FXML
    private ImageView imgStar1;

    @FXML
    private ImageView imgStar2;

    @FXML
    private ImageView imgStar3;

    @FXML
    private ImageView imgStar4;

    @FXML
    private ImageView imgStar5;

    @FXML
    private ImageView imgStar6;

    @FXML
    private Label lblPercent;

    @FXML
    private Pane rootPane;

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

        if (inputData.isLayoutInfoValid()) {

                setLayout(inputData.getLayoutX(), inputData.getLayoutY());
        }
        this.exerciseResultViewInputScreenData = (ExerciseResultViewInputScreenData) inputData;
        lblPercent.setText(this.exerciseResultViewInputScreenData.getPercentage() + " %!");
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
     * Show stars progressively based on the percentage
     * @param percent percentage of stars to show
     */
    private void showStarsProgressively(double percent) {

        int starsToShow = (int) Math.round((percent / 100.0) * 6);
        List<ImageView> stars = Arrays.asList(imgStar1, imgStar2, imgStar3, imgStar4, imgStar5, imgStar6);
        stars.forEach(star -> star.setImage(new Image("com/davidp/chessjourney/img-gray/stars_48dp_gray.png")));

        // Show stars progressively
        for (int i = 0; i < starsToShow; i++) {
            int starIndex = i;
            JavaFXGameTimerUtil.runLoop(
                    () -> {
                        stars.get(starIndex).setImage(new Image("com/davidp/chessjourney/img-gray/stars_48dp_purple.png"));
                        runLater(() -> soundService.playSound(SoundServiceFactory.SoundType.SUCCEED_EXERCISE));
                    },
                    Duration.millis(400 * (i + 1))

            );
        }
    }
}