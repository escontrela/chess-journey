package com.davidp.chessjourney;

import com.davidp.chessjourney.application.config.AppProperties;
import com.davidp.chessjourney.application.config.GlobalEventBus;
import com.davidp.chessjourney.application.domain.UserSavedAppEvent;
import com.davidp.chessjourney.application.factories.ScreenFactory;
import com.davidp.chessjourney.application.ui.ScreenPanel;
import com.davidp.chessjourney.application.ui.settings.SettingsViewController;
import com.davidp.chessjourney.application.ui.settings.SettingsViewData;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainSceneController {

    @FXML
    private Pane pnlTitleBar;

    @FXML
    private Pane pnlMessage;

    @FXML
    private Pane mainPane;

    @FXML
    private Pane boardPane;

    @FXML
    private Button btAdd;

    @FXML
    private Button btClose;

    @FXML
    private Button btSettings;

    @FXML
    private Button btMaximize;

    @FXML
    private Button btMinimize;

    @FXML
    private Button btTopOption1;

    @FXML
    private Button btTopOption2;

    @FXML
    private Button btTopOption3;

    @FXML
    private Button exit1131;

    @FXML
    private Button exit11311;

    @FXML
    private ImageView imgAvatarView;

    @FXML
    private ImageView imgClose;

    @FXML
    private ImageView imgLogo;

    @FXML
    private ImageView imgMaximize;

    @FXML
    private ImageView imgMinimize;

    @FXML
    private ImageView imgMinimize1;

    @FXML
    private TextField txtInSearch;

    @FXML
    private Text txtLogonId;

    @FXML
    private Text txtLogonTitle;


    @FXML
    private ImageView avatarView;


    @FXML
    private Button btLeft;

    @FXML
    private Button btRight;


    // Variables para guardar la posición (offset) dentro de la ventana al pulsar el ratón
    private double xOffset = 0;
    private double yOffset = 0;


    @FXML
    void buttonAction(ActionEvent event) {

        if (event.getSource() == btClose) {

            hide();
        }

        if (event.getSource() == btMaximize) {

            maximize();

        }

        if (event.getSource() == btMinimize) {

            minimize();
        }

        if (event.getSource() == btAdd) {


        }
        if (event.getSource() == btLeft || event.getSource() == btRight) {

            showInfoPanel(pnlMessage);
        }
    if (event.getSource() == btSettings) {

      try {

          SettingsViewData inputData = new SettingsViewData(AppProperties.getInstance().getActiveUserId(), 250,250);
          ScreenPanel<SettingsViewController, SettingsViewData>
                  settingsScreenPanel =  ScreenFactory.getInstance().createScreen(ScreenFactory.Screens.SETTINGS,inputData);

         Pane settingsPane = settingsScreenPanel.getRoot();
         mainPane.getChildren().add(settingsPane);
         settingsPane.setLayoutX(250);
         settingsPane.setLayoutY(200);

      } catch (Exception ex) {
            System.out.println("Error al cargar el FXML del tablero: " + ex.getMessage());
            ex.printStackTrace();
      }
    }
  }

  private void showInfoPanel(Pane panel) {

        panel.setVisible(!panel.isVisible());
    }

    @Subscribe
    public void onUserSaved(UserSavedAppEvent event) {

        Platform.runLater(() -> {

            System.out.println("Se guardó el usuario: " + event.getUserId());
            reloadUserData(event.getUserId());
        });
    }

    private void reloadUserData(long userId) {
        // Lógica para refrescar tu pantalla principal
    }

    public MainSceneController() {

        GlobalEventBus.get().register(this);
    }

    /**
     * All the logic starts here!
     */
    @FXML
    public void initialize() {



        // Cuando se pulsa el ratón sobre titleBar:
        pnlTitleBar.setOnMousePressed(event -> {
            // Guardamos la posición del ratón relativa a la ventana
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        // Cuando se arrastra el ratón sobre titleBar:
        pnlTitleBar.setOnMouseDragged(event -> {
            // Obtenemos la ventana (Stage) actual a través del "scene"
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Actualizamos la posición del Stage restando los offsets
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

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

    public Pane getBoardPane() {
        return boardPane;
    }
}