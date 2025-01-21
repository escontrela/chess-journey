package com.davidp.chessjourney.application.ui.settings;

import com.davidp.chessjourney.application.config.GlobalEventBus;
import com.davidp.chessjourney.application.domain.UserSavedAppEvent;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.usecases.GetUserByIdUseCase;
import com.davidp.chessjourney.application.usecases.SaveUserUseCase;
import com.davidp.chessjourney.domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class SettingsViewController  implements ScreenController {

  @FXML private Button btClose;

  @FXML private Button btOption1;

  @FXML private Button btSave;

  @FXML private ImageView imgClose;

  @FXML private TextField inEmail;

  @FXML private TextField inLastName;

  @FXML private TextField inName;

  @FXML private Label lbUser;

  @FXML private Pane pnlTitleBar1111;

  @FXML private Pane pnlTitleBar11111;

  @FXML private Pane pnlTitleBar11112;

  @FXML private Pane rootPane;

  private GetUserByIdUseCase getUserByIdUseCase;
  private SaveUserUseCase saveUserUseCase;
  private SettingsViewInputScreenData settingsViewData;


  private ScreenController.ScreenStatus status;

  public void setGetUserByIdUseCase(GetUserByIdUseCase getUserByIdUseCase) {

    this.getUserByIdUseCase = getUserByIdUseCase;
  }

  public void setSaveUserUseCase(SaveUserUseCase saveUserUseCase) {

    this.saveUserUseCase = saveUserUseCase;
  }

  public void setSettingsViewData(SettingsViewInputScreenData settingsViewData) {

    this.settingsViewData = settingsViewData;
  }

  public void refreshUserInfo() {

    User user = getUserByIdUseCase.execute(settingsViewData.getUserId());
    inName.setText(user.getFirstname());
    inLastName.setText(user.getLastname());
    inEmail.setText(user.getEmail());
    lbUser.setText(user.getInitials());
  }

  public void initialize() {


    status = ScreenController.ScreenStatus.INITIALIZED;
  }

  @FXML
  void buttonAction(ActionEvent event) {

    if (event.getSource() == btSave) {

      User user = buildUserFromForm();
      saveUserUseCase.execute(user);
      GlobalEventBus.get().post(new UserSavedAppEvent(settingsViewData.getUserId()));
    }

    rootPane.setVisible(false);
  }

  private User buildUserFromForm() {

    // TODO validate user data..
    // long id, String email, String firstname, String lastname
    User user =
        new User(
            settingsViewData.getUserId(),
            inEmail.getText(),
            inName.getText(),
            inLastName.getText());
    return user;
  }





  public void setData(InputScreenData inputData) {

    setSettingsViewData( (SettingsViewInputScreenData) inputData);

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
    rootPane.setVisible(true);
  }

  public void show(InputScreenData inputData){

    setData(inputData);
    status = ScreenController.ScreenStatus.VISIBLE;
    show();
  }

  @Override
  public void hide() {
    status = ScreenController.ScreenStatus.HIDDEN;
    rootPane.setVisible(false);
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
}
