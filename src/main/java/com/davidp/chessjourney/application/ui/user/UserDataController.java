package com.davidp.chessjourney.application.ui.user;

import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.ui.settings.SettingsViewInputScreenData;
import com.davidp.chessjourney.application.ui.util.FXAnimationUtil;
import com.davidp.chessjourney.application.usecases.GetUserDataUseCase;
import com.davidp.chessjourney.domain.User;
import com.davidp.chessjourney.domain.lichess.LichessUser;
import com.davidp.chessjourney.domain.lichess.UserData;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Controller for the user data screen that displays both local and Lichess user information.
 */
public class UserDataController implements ScreenController {

  private GetUserDataUseCase getUserDataUseCase;
  private ScreenStatus status;
  private SettingsViewInputScreenData inputScreenData;

  @FXML private Button btClose;
  @FXML private ImageView imgClose;
  @FXML private Pane rootPane;

  // Local user data labels
  @FXML private Label lblLocalUserName;
  @FXML private Label lblLocalUserNameValue;
  @FXML private Label lblLocalUserEmail;
  @FXML private Label lblLocalUserEmailValue;
  @FXML private Label lblLocalUserId;
  @FXML private Label lblLocalUserIdValue;
  @FXML private Label lblLocalUserInitials;
  @FXML private Label lblLocalUserInitialsValue;

  // Lichess data labels
  @FXML private Label lblLichessStatus;
  @FXML private Label lblLichessUsername;
  @FXML private Label lblLichessUsernameValue;
  @FXML private Label lblLichessId;
  @FXML private Label lblLichessIdValue;
  @FXML private Label lblLichessEmail;
  @FXML private Label lblLichessEmailValue;
  @FXML private Label lblLichessStats;
  @FXML private Label lblLichessStatsValue;
  @FXML private Label lblLichessWins;
  @FXML private Label lblLichessWinsValue;
  @FXML private Label lblLichessLosses;
  @FXML private Label lblLichessLossesValue;
  @FXML private Label lblLichessDraws;
  @FXML private Label lblLichessDrawsValue;

  public void initialize() {
    status = ScreenStatus.INITIALIZED;
  }

  @Override
  public void setData(InputScreenData inputData) {
    this.inputScreenData = (SettingsViewInputScreenData) inputData;
    if (inputData != null) {
      setLayout(inputData.getLayoutX(), inputData.getLayoutY());
    }
    if (getUserDataUseCase != null) {
      loadUserData();
    }
  }

  @Override
  public void setLayout(double layoutX, double layoutY) {
    rootPane.setLayoutX(layoutX);
    rootPane.setLayoutY(layoutY);
  }

  @Override
  public void show() {
    FXAnimationUtil.fadeIn(rootPane, 0.2)
        .repeat(1)
        .autoReverse(false)
        .onFinished(() -> {
          rootPane.setVisible(true);
          rootPane.toFront();
        })
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
    FXAnimationUtil.fadeOut(rootPane, 0.2)
        .repeat(1)
        .autoReverse(false)
        .onFinished(() -> {
          rootPane.setVisible(false);
          status = ScreenStatus.HIDDEN;
        })
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
    return status;
  }

  @Override
  public boolean isInitialized() {
    return status != null && !status.equals(ScreenStatus.NOT_INITIALIZED);
  }

  @FXML
  void buttonAction(ActionEvent event) {
    if (event.getSource() == btClose) {
      hide();
    }
  }

  public void setGetUserDataUseCase(GetUserDataUseCase getUserDataUseCase) {
    this.getUserDataUseCase = getUserDataUseCase;
  }

  private void loadUserData() {
    if (getUserDataUseCase == null) {
      System.err.println("⚠️ GetUserDataUseCase not set");
      return;
    }

    try {
      UserData userData = getUserDataUseCase.execute();
      populateLocalUserData(userData.getLocalUser());
      populateLichessData(userData);
    } catch (Exception e) {
      System.err.println("⚠️ Error loading user data: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void populateLocalUserData(User localUser) {
    if (localUser != null) {
      lblLocalUserNameValue.setText(localUser.getFirstname() + " " + localUser.getLastname());
      lblLocalUserEmailValue.setText(localUser.getEmail() != null ? localUser.getEmail() : "N/A");
      lblLocalUserIdValue.setText(String.valueOf(localUser.getId()));
      lblLocalUserInitialsValue.setText(localUser.getInitials() != null ? localUser.getInitials() : "N/A");
    } else {
      lblLocalUserNameValue.setText("No user data");
      lblLocalUserEmailValue.setText("N/A");
      lblLocalUserIdValue.setText("N/A");
      lblLocalUserInitialsValue.setText("N/A");
    }
  }

  private void populateLichessData(UserData userData) {
    if (userData.isLichessConnected() && userData.getLichessUser() != null) {
      lblLichessStatus.setText("Status: Connected ✅");
      LichessUser lichessUser = userData.getLichessUser();
      
      lblLichessUsernameValue.setText(lichessUser.getUsername() != null ? lichessUser.getUsername() : "N/A");
      lblLichessIdValue.setText(lichessUser.getId() != null ? lichessUser.getId() : "N/A");
      lblLichessEmailValue.setText(lichessUser.getEmail() != null ? lichessUser.getEmail() : "N/A");
      
      if (lichessUser.getCount() != null) {
        lblLichessStatsValue.setText(String.valueOf(lichessUser.getCount().getAll()));
        lblLichessWinsValue.setText(String.valueOf(lichessUser.getCount().getWin()));
        lblLichessLossesValue.setText(String.valueOf(lichessUser.getCount().getLoss()));
        lblLichessDrawsValue.setText(String.valueOf(lichessUser.getCount().getDraw()));
      } else {
        lblLichessStatsValue.setText("N/A");
        lblLichessWinsValue.setText("N/A");
        lblLichessLossesValue.setText("N/A");
        lblLichessDrawsValue.setText("N/A");
      }
    } else {
      lblLichessStatus.setText("Status: Not Connected ❌");
      if (userData.getLichessError() != null) {
        lblLichessStatus.setText("Status: Error - " + userData.getLichessError());
      }
      
      // Clear all Lichess data
      lblLichessUsernameValue.setText("N/A");
      lblLichessIdValue.setText("N/A");
      lblLichessEmailValue.setText("N/A");
      lblLichessStatsValue.setText("N/A");
      lblLichessWinsValue.setText("N/A");
      lblLichessLossesValue.setText("N/A");
      lblLichessDrawsValue.setText("N/A");
    }
  }
}