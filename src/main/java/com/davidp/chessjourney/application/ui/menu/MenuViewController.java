package com.davidp.chessjourney.application.ui.menu;

import com.almasb.fxgl.dsl.FXGL;
import com.davidp.chessjourney.application.config.GlobalEventBus;
import com.davidp.chessjourney.application.domain.OpenSettingsFromMenuEvent;
import com.davidp.chessjourney.application.domain.UserSavedAppEvent;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class MenuViewController implements ScreenController {

  @FXML private Pane rootPane;

  @FXML
  private ImageView imgSettings;

  @FXML
  private Pane pnlOptionAnalysisBoard;

  @FXML
  private Pane pnlOptionSettings;

  private ScreenController.ScreenStatus status;

  @FXML
  private Text txtSettings;

  public void initialize() {

    status = ScreenController.ScreenStatus.INITIALIZED;
  }


  @Override
  public void setData(InputScreenData inputData) {

    if (inputData.isLayoutInfoValid()){

      setLayout(inputData.getLayoutX(), inputData.getLayoutY());
    }
  }

  @Override
  public void setLayout(double layoutX, double layoutY) {

    rootPane.setLayoutX(layoutX);
    rootPane.setLayoutY(layoutY);

  }

  @Override
  public void show() {

    // Fade in animation when showing
    FXGL.animationBuilder()
            .duration(Duration.seconds(0.2))
            .onFinished( ()-> rootPane.setVisible(true))
            .fadeIn(rootPane)
            .buildAndPlay();
  }

  public void show(InputScreenData inputData){

    setData(inputData);
    status = ScreenController.ScreenStatus.VISIBLE;
    show();
  }

  @Override
  public void hide() {
    FXGL.animationBuilder()
            .duration(Duration.seconds(0.2))
            .onFinished( ()-> {
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


  @FXML
  void optionClicked(MouseEvent event) {

    if (isSettingMenuClicked(event)) {

      GlobalEventBus.get().post(new OpenSettingsFromMenuEvent());
    }
  }

  protected boolean isSettingMenuClicked(MouseEvent event) {

    return event.getSource() == pnlOptionSettings || event.getSource() == txtSettings;
  }

}