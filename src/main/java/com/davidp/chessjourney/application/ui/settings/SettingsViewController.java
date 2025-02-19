package com.davidp.chessjourney.application.ui.settings;

import com.almasb.fxgl.dsl.FXGL;
import com.davidp.chessjourney.application.config.GlobalEventBus;
import com.davidp.chessjourney.application.domain.UserSavedAppEvent;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.usecases.GetAllTagsUseCase;
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
import javafx.util.Duration;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SettingsViewController implements ScreenController {

  @FXML private Button btClose;


  @FXML
  private Button btProfile;


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


  @FXML
  private Pane pnlProfile;


  @FXML
  private Pane pnlTags;


  @FXML
  private TextField inTag;

  @FXML
  private Button btOptionTags;


  @FXML
  private Button btAddTag;


  private GetAllTagsUseCase getAllTagsUseCase;
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

  public void setGetAllTagsUseCase(GetAllTagsUseCase getAllTagsUseCase) {

    this.getAllTagsUseCase = getAllTagsUseCase;
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

  public void refreshTags() {

    // pnlTags.getChildren().filtered(p-> "tag".equalsIgnoreCase(p.getId())).clear();

    AtomicInteger pos = new AtomicInteger(10);
    getAllTagsUseCase
            .execute()
            .forEach(
                    tag -> {
                      Label label = new Label(tag.getName());
                      label.setId("tag");
                      label.getStyleClass().add("text-white-medium");
                      label.setText(tag.getName());
                      label.setLayoutX(10);
                      label.setLayoutY(pos.get());
                      pos.set(pos.get() + 25);
                      pnlTags.getChildren().add(label);
                    });
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
      rootPane.setVisible(false);
    }

    if (event.getSource() == btClose) {

      rootPane.setVisible(false);
    }

    if (event.getSource() == btOptionTags) {

      pnlProfile.setVisible(false);
      FXGL.animationBuilder()
              .duration(Duration.seconds(0.2))
              .onFinished(
                      () -> {
                        refreshTags();
                        pnlTags.setVisible(true);
                        //TODO set status = ....
                      })
              .fadeIn(pnlTags)
              .buildAndPlay();
    }

    if (event.getSource() == btProfile) {
      pnlTags.setVisible(false);
      pnlProfile.setVisible(true);
    }


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

    setSettingsViewData((SettingsViewInputScreenData) inputData);

    if (inputData.isLayoutInfoValid()) {

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
    rootPane.toFront();
  }

  public void show(InputScreenData inputData) {

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
