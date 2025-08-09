package com.davidp.chessjourney.application.ui.user;

import com.davidp.chessjourney.application.config.AppProperties;
import com.davidp.chessjourney.application.config.GlobalEventBus;
import com.davidp.chessjourney.application.domain.*;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.board.PromoteViewInputScreenData;
import com.davidp.chessjourney.application.ui.controls.SelectableCardController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.usecases.GetUsersUseCase;
import com.davidp.chessjourney.application.usecases.SaveActiveUserUseCase;
import com.davidp.chessjourney.application.util.JavaFXAnimationUtil;
import com.davidp.chessjourney.domain.User;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class UserViewController implements ScreenController {

  private GetUsersUseCase getUsersUseCase;
  private SaveActiveUserUseCase saveUserUseCase;

  private ScreenStatus status;

  private PromoteViewInputScreenData promoteViewInputScreenData;

  @FXML private Button btClose;

  @FXML private ImageView imgClose;

  @FXML private Pane rootPane;


  @FXML
  private FlowPane usersFlowPanel;

  public void initialize() {

    status = ScreenStatus.INITIALIZED;
  }

  @Override
  public void setData(InputScreenData inputData) {

    if (inputData.isLayoutInfoValid()) {

      setLayout(inputData.getLayoutX(), inputData.getLayoutY());
    }

    List<User> users = getUsersUseCase.execute();
    long activeUserId = AppProperties.getInstance().getActiveUserId();
    showUsersWithAnimation(users,activeUserId);
  }

  private void showUsersWithAnimation(List<User> users, long activeUserId) {

    usersFlowPanel.getChildren().clear(); // Limpia la lista antes de mostrar nuevos usuarios

    usersFlowPanel.setHgap(20);  // espacio horizontal entre tarjetas
    usersFlowPanel.setVgap(20);  // espacio vertical entre filas
    usersFlowPanel.setPrefWrapLength(400); // ancho máximo antes de crear una nueva fila

      for (int i = 0; i < users.size(); i++) {

        User user = users.get(i);

        SelectableCardController userPane = createUserPane(user, activeUserId);

        userPane.setCardClickListener(this::onUserCardClicked);

        PauseTransition delay = new PauseTransition(Duration.seconds(0.5 * i));
        delay.setOnFinished(
            e -> {
              usersFlowPanel.getChildren().add(userPane);

              JavaFXAnimationUtil.animationBuilder()
                  .duration(Duration.seconds(0.5))
                  .fadeIn(userPane)
                  .buildAndPlay();
            });

        delay.play();
      }

      SelectableCardController addNewUserCard = new SelectableCardController();
      addNewUserCard.setTitle("< add new user >");
      addNewUserCard.setUserData("new_user");
      addNewUserCard.getStyleClass().add("selectable-card");
      String avatarPath = "/com/davidp/chessjourney/avatar/robot-avatar-0.png";
      addNewUserCard.setImageUrl(avatarPath);
      usersFlowPanel.getChildren().add(addNewUserCard);

  }

  private SelectableCardController createUserPane(User user,long activeUserId) {

    SelectableCardController card = new SelectableCardController();
    card.setTitle(user.getFirstname() + " " + user.getLastname() + " (" + user.getInitials() + ")");
    card.getStyleClass().add("selectable-card");
    card.setPrefHeight(135); // Ajusta la altura deseada
    card.setPrefWidth(330);  // Ajusta el ancho deseado si necesitas uniformidad
    card.setUserData(user);

    String idStr = Long.toString(user.getId());
    int lastDigit = Character.getNumericValue(idStr.charAt(idStr.length() - 2));
    int avatarNum = (lastDigit % 4) + 1;
    String avatarPath = "/com/davidp/chessjourney/avatar/robot-avatar-" + avatarNum + ".png";
    card.setImageUrl(avatarPath);

    card.setSubtitles(new ArrayList<>());
    card.getSubtitles().add(user.getEmail());
    if (user.getId() == activeUserId) {

      card.getSubtitles().add("<Active User>");
    }

    //card.setOnMouseClicked(this::optionClicked);

    return card;
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

  private void onUserCardClicked(SelectableCardController card) {

    if ("new_user".equals(card.getUserData())) {
      // Handle the case for adding a new user
      System.out.println("Add new user clicked");
      //TODO new logic to create a new user
      // GlobalEventBus.get().post(new OpenAddNewUserEvent());
      return;
    }

    User selectedUser = (User) card.getUserData();
    saveUserUseCase.execute(selectedUser.getId());
    System.out.println("Active user: " + selectedUser.getEmail() + " was saved on properties.");
    GlobalEventBus.get().post(new UserSavedAppEvent(selectedUser.getId()));
    //TODO animación del OK al nuevo usuario seleccionado
    hide();
  }
}
