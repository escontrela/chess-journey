package com.davidp.chessjourney.application.ui.board;

import com.almasb.fxgl.dsl.FXGL;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class ExerciseResultViewController implements ScreenController {

    private ScreenController.ScreenStatus status;

    private ExerciseResultViewInputScreenData exerciseResultViewInputScreenData;

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

        lblPercent.setText("...");
        if (inputData != null) {

          this.exerciseResultViewInputScreenData = (ExerciseResultViewInputScreenData) inputData;
          lblPercent.setText(this.exerciseResultViewInputScreenData.toString());
        }
    }

    @Override
    public void setLayout(double layoutX, double layoutY) {

        rootPane.setLayoutX(layoutX);
        rootPane.setLayoutY(layoutY);
    }

    @Override
    public void show() {

        rootPane.setVisible(false);

        // Fade in animation when showing
        FXGL.animationBuilder()
                .duration(Duration.seconds(0.2))
                .onFinished(
                        () -> {
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

        FXGL.animationBuilder()
                .duration(Duration.seconds(0.2))
                .onFinished(
                        () -> {
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
}
