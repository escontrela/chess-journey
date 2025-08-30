package com.davidp.chessjourney.application.ui.user;

import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.controls.SelectableCardController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.ui.settings.SettingsViewInputScreenData;
import com.davidp.chessjourney.application.util.JavaFXAnimationUtil;
import com.davidp.chessjourney.application.usecases.GetUserTacticSuiteGamesUseCase;
import com.davidp.chessjourney.domain.games.tactic.TacticSuiteGame;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.List;

public class UserTacticSuiteGamesController implements ScreenController {

    private GetUserTacticSuiteGamesUseCase getUserTacticSuiteGamesUseCase;

    private ScreenStatus status;

    private SettingsViewInputScreenData inputScreenData;

    @FXML private Button btClose;

    @FXML private ImageView imgClose;

    @FXML private Pane rootPane;

    @FXML private FlowPane tacticSuitesFlowPanel;

    public void initialize() {
        // Initialize any components if needed
    }

    @Override
    public void setData(InputScreenData inputData) {

        this.inputScreenData = (SettingsViewInputScreenData) inputData;

        if (inputData != null) {
            setLayout(inputData.getLayoutX(), inputData.getLayoutY());
        }
        if (getUserTacticSuiteGamesUseCase != null) {
            List<TacticSuiteGame> tacticSuiteGames = getUserTacticSuiteGamesUseCase.execute(this.inputScreenData.getUserId());
            showTacticSuiteGamesWithAnimation(tacticSuiteGames);
        }
    }

    private void showTacticSuiteGamesWithAnimation(List<TacticSuiteGame> tacticSuiteGames) {

        tacticSuitesFlowPanel.getChildren().clear();

        tacticSuitesFlowPanel.setHgap(20);  // espacio horizontal entre tarjetas
        tacticSuitesFlowPanel.setVgap(20);  // espacio vertical entre filas
        tacticSuitesFlowPanel.setPrefWrapLength(400); // ancho máximo antes de crear una nueva fila


        for (int i = 0; i < tacticSuiteGames.size(); i++) {
            TacticSuiteGame tacticSuiteGame = tacticSuiteGames.get(i);
            SelectableCardController tacticSuitePane = createTacticSuiteGamePane(tacticSuiteGame);

            tacticSuitesFlowPanel.getChildren().add(tacticSuitePane);

            // Add animation delay
            PauseTransition delay = new PauseTransition(Duration.seconds(i * 0.1));
            final SelectableCardController finalPane = tacticSuitePane;
            delay.setOnFinished(event -> {
                JavaFXAnimationUtil.animationBuilder()
                        .duration(Duration.seconds(0.3))
                        .fadeIn(finalPane)
                        .buildAndPlay();
            });

            delay.play();
        }
    }

    private SelectableCardController createTacticSuiteGamePane(TacticSuiteGame tacticSuiteGame) {
        SelectableCardController card = new SelectableCardController();
        card.setTitle(tacticSuiteGame.getName());
        card.getSubtitles().add("Type: " + tacticSuiteGame.getType().name());
        card.getSubtitles().add("Created: " + tacticSuiteGame.getCreatedAt().toLocalDate().toString());
        card.setUserData(tacticSuiteGame);
        card.getStyleClass().add("selectable-card");
        
        // Set a default icon for tactic suites
        String iconPath = "/com/davidp/chessjourney/avatar/robot-avatar-0.png";
        card.setImageUrl(iconPath);
        
        card.setCardClickListener(this::onTacticSuiteGameCardClicked);

        return card;
    }

    private void onTacticSuiteGameCardClicked(SelectableCardController card) {
        TacticSuiteGame selectedTacticSuiteGame = (TacticSuiteGame) card.getUserData();
        optionClicked(selectedTacticSuiteGame);
    }

    @FXML
    void optionClicked(MouseEvent event) {
        // This is for FXML event handling if needed
        Pane selectedPane = (Pane) event.getSource();
        TacticSuiteGame selectedTacticSuiteGame = (TacticSuiteGame) selectedPane.getUserData();
        optionClicked(selectedTacticSuiteGame);
    }

    private void optionClicked(TacticSuiteGame selectedTacticSuiteGame) {
        System.out.println(selectedTacticSuiteGame);
        // Future: Could trigger navigation to specific tactic suite game
    }

    @FXML
    void buttonAction(ActionEvent event) {
        if (event.getSource() == btClose) {
            rootPane.setVisible(false);
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
        status = ScreenStatus.VISIBLE;
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
    public Pane getRootPane() {
        return rootPane;
    }

    @Override
    public ScreenStatus getStatus() {
        return status;
    }

    @Override
    public boolean isInitialized() {
        return status == ScreenStatus.INITIALIZED;
    }

    @Override
    public boolean isVisible() {
        return status == ScreenStatus.VISIBLE;
    }

    @Override
    public boolean isHidden() {
        return status == ScreenStatus.HIDDEN;
    }

    public void setGetUserTacticSuiteGamesUseCase(GetUserTacticSuiteGamesUseCase getUserTacticSuiteGamesUseCase) {
        this.getUserTacticSuiteGamesUseCase = getUserTacticSuiteGamesUseCase;
    }
}