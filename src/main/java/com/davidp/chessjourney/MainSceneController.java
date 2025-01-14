package com.davidp.chessjourney;

import com.almasb.fxgl.dsl.FXGL;
import com.davidp.chessjourney.application.config.AppProperties;
import com.davidp.chessjourney.application.config.GlobalEventBus;
import com.davidp.chessjourney.application.domain.UserSavedAppEvent;
import com.davidp.chessjourney.application.factories.ScreenFactory;
import com.davidp.chessjourney.application.factories.UseCaseFactory;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.ScreenPanel;
import com.davidp.chessjourney.application.ui.menu.MenuViewController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.ui.settings.SettingsViewController;
import com.davidp.chessjourney.application.ui.settings.SettingsViewInputScreenData;
import com.davidp.chessjourney.application.usecases.GetUserByIdUseCase;
import com.davidp.chessjourney.domain.User;
import com.google.common.eventbus.Subscribe;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainSceneController {

  @FXML private Button btClose;

  @FXML private Button btLeft;

  @FXML private Button btRight;

  @FXML private Button btSettings;

  @FXML private Label lbUserInitials;

  @FXML private Pane mainPane;

  @FXML private Pane pnlMessage;

  @FXML private Pane pnlTitleBar;

  // Variables para guardar la posición (offset) dentro de la ventana al pulsar el ratón
  private double xOffset = 0;
  private double yOffset = 0;

  // Mapa para cachear pantallas (según tu enum Screens)
  private final Map<ScreenFactory.Screens, ScreenPanel<?, ?>> screenCache = new HashMap<>();

  private  ScreenController screenMenuController = null;

  @FXML
  void buttonAction(ActionEvent event) {

    if (event.getSource() == btClose) {

      hide();
    }

    if (event.getSource() == btLeft || event.getSource() == btRight) {

      showInfoPanel(pnlMessage);
    }
    if (event.getSource() == btSettings) {

      try {

        ScreenPanel<SettingsViewController, SettingsViewInputScreenData> settingsScreenPanel;

        SettingsViewInputScreenData inputData =
            new SettingsViewInputScreenData(
                AppProperties.getInstance().getActiveUserId(), 250, 250);


        if (!screenCache.containsKey(ScreenFactory.Screens.SETTINGS)) {
          settingsScreenPanel =
              ScreenFactory.getInstance().createScreen(ScreenFactory.Screens.SETTINGS, inputData);
          screenCache.put(ScreenFactory.Screens.SETTINGS, settingsScreenPanel);
          mainPane.getChildren().add(settingsScreenPanel.getRoot());

        } else {

          @SuppressWarnings("unchecked")
          ScreenPanel<SettingsViewController, SettingsViewInputScreenData> temp =
              (ScreenPanel<SettingsViewController, SettingsViewInputScreenData>)
                  screenCache.get(ScreenFactory.Screens.SETTINGS);

          settingsScreenPanel = temp;

        }



        settingsScreenPanel.setData(inputData);
        settingsScreenPanel.getController().setSettingsViewData(inputData);
        settingsScreenPanel.getRoot().setVisible(true);
        settingsScreenPanel.getRoot().toFront();

      } catch (Exception ex) {
        System.out.println("Error al cargar el FXML del tablero: " + ex.getMessage());
        ex.printStackTrace();
      }
    }
  }

  @FXML
  public void handleButtonClick(MouseEvent event) {

    if (screenMenuController != null) {

      if (screenMenuController.isVisible()){

        screenMenuController.hide();
        return;
      }

      screenMenuController.show();
    }

  }


  void handleButtonClickdd(MouseEvent event) {


    try {
      boolean shouldHide = false;

      InputScreenData inputData = new InputScreenData(20, 465);

      // Revisa si ya existe la pantalla en el mapa
      ScreenPanel<MenuViewController, InputScreenData> menuScreenPanel;

      if (!screenCache.containsKey(ScreenFactory.Screens.MENU)) {

        menuScreenPanel =
            ScreenFactory.getInstance().createScreen(ScreenFactory.Screens.MENU, inputData);
        screenCache.put(ScreenFactory.Screens.MENU, menuScreenPanel);
        mainPane.getChildren().add(menuScreenPanel.getRoot());

      } else {

        @SuppressWarnings("unchecked")
        ScreenPanel<MenuViewController, InputScreenData> temp =
            (ScreenPanel<MenuViewController, InputScreenData>)
                screenCache.get(ScreenFactory.Screens.MENU);
        menuScreenPanel = temp;

        shouldHide = menuScreenPanel.getRoot().isVisible();
      }


      if (shouldHide){

        menuScreenPanel.getRoot().setVisible(false);
        return;
      }

      menuScreenPanel.setData(inputData);
      menuScreenPanel.getRoot().setVisible(true);
      menuScreenPanel.getRoot().toFront();

    } catch (Exception ex) {

      System.out.println("Error al cargar el FXML: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  private void showInfoPanel(Pane panel) {

    panel.setVisible(!panel.isVisible());
  }

  @Subscribe
  public void onUserSaved(UserSavedAppEvent event) {

    Platform.runLater(
        () -> {
          System.out.println("Se guardó el usuario: " + event.getUserId());
          reloadUserInitials(event.getUserId());
        });
  }

  public MainSceneController() {

    GlobalEventBus.get().register(this);
  }

  /** All the logic starts here! */
  @FXML
  public void initialize() {

    // Cuando se pulsa el ratón sobre titleBar:
    pnlTitleBar.setOnMousePressed(
        event -> {
          // Guardamos la posición del ratón relativa a la ventana
          xOffset = event.getSceneX();
          yOffset = event.getSceneY();
        });

    // Cuando se arrastra el ratón sobre titleBar:
    pnlTitleBar.setOnMouseDragged(
        event -> {
          // Obtenemos la ventana (Stage) actual a través del "scene"
          Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

          // Actualizamos la posición del Stage restando los offsets
          stage.setX(event.getScreenX() - xOffset);
          stage.setY(event.getScreenY() - yOffset);
        });

    reloadUserInitials(AppProperties.getInstance().getActiveUserId());

    //Create the menu


      try {

        screenMenuController = ScreenFactory.getInstance().createScreen2(ScreenFactory.Screens.MENU);
        screenMenuController.setData( new InputScreenData(20, 465));
        screenMenuController.hide();
        mainPane.getChildren().add(screenMenuController.getRootPane());

      } catch (IOException e) {
          throw new RuntimeException(e);
      }

  }

  private void reloadUserInitials(long userId) {

    // TODO move createGetUserByUseCase to the constructor... to minimize dependencies
    GetUserByIdUseCase getUserByIdUseCase = UseCaseFactory.createGetUserByIdUseCase();
    User loggedUser = getUserByIdUseCase.execute(userId);
    lbUserInitials.setText(loggedUser.getInitials());

    FXGL.animationBuilder()
        .duration(Duration.seconds(0.1))
        .repeat(2)
        .autoReverse(true)
        .fadeOut(lbUserInitials)
        .buildAndPlay();
  }

  private Stage stage;

  public void setStage(final Stage fxStage) {

    this.stage = fxStage;
  }

  public Stage getStage() {

    return this.stage;
  }

  public void hide() {

    stage.hide();
  }

  public void maximize() {

    if (stage.isMaximized()) {

      stage.setMaximized(false);

    } else {

      stage.setMaximized(true);
    }
  }

  public void minimize() {

    stage.setIconified(true);
  }
}
