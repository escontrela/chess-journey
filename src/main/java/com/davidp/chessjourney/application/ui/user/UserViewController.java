package com.davidp.chessjourney.application.ui.user;

import com.davidp.chessjourney.application.config.AppProperties;
import com.davidp.chessjourney.application.config.GlobalEventBus;
import com.davidp.chessjourney.application.domain.*;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.board.PromoteViewInputScreenData;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.usecases.GetUsersUseCase;
import com.davidp.chessjourney.application.usecases.SaveActiveUserUseCase;
import com.davidp.chessjourney.application.util.JavaFXAnimationUtil;
import com.davidp.chessjourney.domain.User;
import java.util.List;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class UserViewController implements ScreenController {

  private GetUsersUseCase getUsersUseCase;
  private SaveActiveUserUseCase saveUserUseCase;

  private ScreenStatus status;

  private PromoteViewInputScreenData promoteViewInputScreenData;

  @FXML private Button btClose;

  @FXML private ImageView imgClose;

  @FXML private Pane rootPane;

  @FXML private VBox userListContainer; // Contenedor donde se agregarán los usuarios dinámicamente

  public void initialize() {

    status = ScreenStatus.INITIALIZED;
  }

  @Override
  public void setData(InputScreenData inputData) {

    if (inputData.isLayoutInfoValid()) {

      setLayout(inputData.getLayoutX(), inputData.getLayoutY());
    }

    List<User> users = getUsersUseCase.execute();
    Long activeUserId = AppProperties.getInstance().getActiveUserId();
    showUsersWithAnimation(users,activeUserId);
  }

  private void showUsersWithAnimation(List<User> users, long activeUserId) {

    userListContainer.getChildren().clear(); // Limpia la lista antes de mostrar nuevos usuarios

    for (int i = 0; i < users.size(); i++) {
      User user = users.get(i);

      Pane userPane = createUserPane(user,activeUserId);

      PauseTransition delay = new PauseTransition(Duration.seconds(0.5 * i));
      delay.setOnFinished(
          e -> {
            userListContainer.getChildren().add(userPane);

            JavaFXAnimationUtil
                .animationBuilder()
                .duration(Duration.seconds(0.5))
                .fadeIn(userPane)
                .buildAndPlay();
          });

      delay.play();
    }
  }

  private Pane createUserPane(User user,long activeUserId) {

    Pane userPane = new Pane();
    userPane.setUserData(user);
    userPane.setPrefWidth(70);
    userPane.setPrefHeight(454);
    userPane.setLayoutX(28);
    userPane.getStyleClass().add("panel-gray-for-menu");
    userPane.setOnMouseClicked(this::optionClicked);
    // userPane.setLayoutY(100 + i * 50);

    // Nombre
    Label nameLabel = new Label(user.getFirstname() + " " + user.getLastname());
    nameLabel.getStyleClass().add("text-white-medium");
    nameLabel.setLayoutX(73);
    nameLabel.setLayoutY(16);

    // Active user
    Label activeUser = new Label("<active>");
    activeUser.getStyleClass().add("text-white-medium");
    activeUser.setLayoutY(24);
    activeUser.setLayoutX(400);

    // Email
    Label emailLabel = new Label(user.getEmail());
    emailLabel.setStyle("-fx-text-fill: #888888;");
    emailLabel.getStyleClass().add("text-gray-small");
    emailLabel.setLayoutX(73);
    emailLabel.setLayoutY(36);

    // Iniciales
    StackPane initialsPane = new StackPane();
    initialsPane.getStyleClass().add("avatar-container-inside");
    initialsPane.setPrefSize(40, 40);
    initialsPane.setPrefWidth(39);
    initialsPane.setPrefHeight(37);
    initialsPane.setLayoutX(14);
    initialsPane.setLayoutY(17);

    Label initialsLabel = new Label(user.getInitials());
    // initialsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
    initialsPane.getChildren().add(initialsLabel);

    if (user.getId() == activeUserId) {

      userPane.getChildren().addAll(initialsPane, nameLabel,activeUser, emailLabel);
    } else{

      userPane.getChildren().addAll(initialsPane, nameLabel, emailLabel);
    }
    return userPane;
  }

  @Override
  public void setLayout(double layoutX, double layoutY) {

    rootPane.setLayoutX(layoutX);
    rootPane.setLayoutY(layoutY);
  }

  @Override
  public void show() {

    rootPane.setVisible(false);

    JavaFXAnimationUtil.animationBuilder()
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
    status = ScreenStatus.VISIBLE;
    show();
  }

  @Override
  public void hide() {

    JavaFXAnimationUtil.animationBuilder()
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

    return status == ScreenStatus.INITIALIZED;
  }

  protected void setPromoteViewInputScreenData(
      PromoteViewInputScreenData promoteViewInputScreenData) {

    this.promoteViewInputScreenData = promoteViewInputScreenData;
  }

  protected PromoteViewInputScreenData getPromoteViewInputScreenData() {

    return promoteViewInputScreenData;
  }

  @FXML
  void optionClicked(MouseEvent event) {

    Pane userSelectedPane = (Pane) event.getSource();
    User userSelected = (User) userSelectedPane.getUserData();
    saveUserUseCase.execute(userSelected.getId());

    System.out.println("Active user: " + userSelected.getEmail() + "was saved on properties.");

    GlobalEventBus.get().post(new UserSavedAppEvent(userSelected.getId()));
    hide();
  }

  @FXML
  void buttonAction(ActionEvent event) {

    if (event.getSource() == btClose) {

      rootPane.setVisible(false);
    }
  }

  public void setGetUsersUseCase(final GetUsersUseCase getUsersUseCase) {

    this.getUsersUseCase = getUsersUseCase;
  }

  public void setSaveUserUseCase(final SaveActiveUserUseCase saveUserUseCase) {

    this.saveUserUseCase = saveUserUseCase;
  }
}
