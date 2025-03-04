package com.davidp.chessjourney.application.factories;

import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.board.BoardViewController;
import com.davidp.chessjourney.application.ui.board.ExerciseResultViewController;
import com.davidp.chessjourney.application.ui.main.MainSceneController;
import com.davidp.chessjourney.application.ui.menu.MenuViewController;
import com.davidp.chessjourney.application.ui.settings.SettingsViewController;
import java.io.IOException;
import java.net.URL;

import com.davidp.chessjourney.application.ui.user.UserViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * This class is responsible for creating the screens that will be added to the main panel on the
 * main STAGE.
 */
public class ScreenFactory {

  /** This is the set of screen that can be added to the main panel on the main STAGE. */
  public enum Screens {
    MAIN_STAGE("/com/davidp/chessjourney/main-scene-3.fxml"),
    SETTINGS("/com/davidp/chessjourney/setting-view.fxml"),
    MENU("/com/davidp/chessjourney/menu-view.fxml"),
    BOARD("/com/davidp/chessjourney/board-view-2.fxml"),
    MEMORY_GAME("/com/davidp/chessjourney/board-view-2.fxml"),
    EXERCISE_RESULTS_PANEL("/com/davidp/chessjourney/exercise-result-view.fxml"),
    PROMOTE_PANEL("/com/davidp/chessjourney/promote-view-2.fxml"),
    CHANGE_USER("/com/davidp/chessjourney/user-change.fxml");;

    private final String resourcePath;

    Screens(final String resourcePath) {

      this.resourcePath = resourcePath;
    }

    public String resourceName() {

      return this.resourcePath;
    }
  }

  private static volatile ScreenFactory instance;

  private ScreenFactory() {}

  public static ScreenFactory getInstance() {

    if (instance == null) {
      synchronized (ScreenFactory.class) {
        if (instance == null) {
          instance = new ScreenFactory();
        }
      }
    }
    return instance;
  }

  public ScreenController createScreen(Screens screen) throws IOException {
    switch (screen) {
      case MENU:
        return getMenuScreen();
      case SETTINGS:
        return getSettingsScreen();
      case BOARD:
        return getBoardScreen();
      case MAIN_STAGE:
        return getMainScreen();
      case MEMORY_GAME:
        return getMemoryGameScreen();
      case PROMOTE_PANEL:
        return getPromotePanelScreen();
      case EXERCISE_RESULTS_PANEL:
        return getExerciseResultPanelScreen();
      case CHANGE_USER:
        return getChangeUserScreen();
      default:
        throw new IllegalArgumentException("Screen not supported: " + screen);
    }
  }

  private ScreenController getChangeUserScreen() {
    FxmlBundle<UserViewController> objectFxmlBundle = loadFxml(Screens.CHANGE_USER.resourceName());
    var controller = objectFxmlBundle.getController();
    controller.setGetUsersUseCase(UseCaseFactory.createGetUsersUseCase());
    controller.setSaveUserUseCase(UseCaseFactory.createSaveActiveUserUseCase());
    return objectFxmlBundle.getController();
  }

  private ScreenController getExerciseResultPanelScreen() {

    FxmlBundle<ExerciseResultViewController> objectFxmlBundle = loadFxml(Screens.EXERCISE_RESULTS_PANEL.resourceName());
    return objectFxmlBundle.getController();
  }

  private ScreenController getSettingsScreen() {

    FxmlBundle<SettingsViewController> objectFxmlBundle = loadFxml(Screens.SETTINGS.resourceName());
    var controller = objectFxmlBundle.getController();
    controller.setGetUserByIdUseCase(UseCaseFactory.createGetUserByIdUseCase());
    controller.setSaveUserUseCase(UseCaseFactory.createSaveUserUseCase());
    controller.setGetAllTagsUseCase(UseCaseFactory.createGetAllTagsUseCase());

    return controller;
  }

  protected ScreenController getMenuScreen() {

    FxmlBundle<MenuViewController> objectFxmlBundle = loadFxml(Screens.MENU.resourceName());
    return objectFxmlBundle.getController();
  }

  protected ScreenController getBoardScreen() {

    FxmlBundle<BoardViewController> objectFxmlBundle = loadFxml(Screens.BOARD.resourceName());
    // TODO: set the use cases here
    return objectFxmlBundle.getController();
  }

  protected ScreenController getMemoryGameScreen() {

    FxmlBundle<BoardViewController> objectFxmlBundle = loadFxml(Screens.MEMORY_GAME.resourceName());
    var controller = objectFxmlBundle.getController();
    controller.setMemoryGameUseCase(UseCaseFactory.createMemoryGameUseCase());
    controller.setSaveUserExerciseStatsUseCase(UseCaseFactory.createSaveUserExerciseStatsUseCase());
    return objectFxmlBundle.getController();
  }

  protected ScreenController getPromotePanelScreen() {

    FxmlBundle<MainSceneController> objectFxmlBundle = loadFxml(Screens.PROMOTE_PANEL.resourceName());

    return objectFxmlBundle.getController();
  }

  protected ScreenController getMainScreen() {

    FxmlBundle<MainSceneController> objectFxmlBundle = loadFxml(Screens.MAIN_STAGE.resourceName());
    // TODO: set the use cases here
    return objectFxmlBundle.getController();
  }

  /**
   * Carga un archivo FXML y devuelve un objeto FxmlBundle que contiene el controlador y el nodo
   * raíz.
   */
  protected static <T> FxmlBundle<T> loadFxml(String fxmlPath) {
    try {

      URL resource = ScreenFactory.class.getResource(fxmlPath);
      FXMLLoader loader = new FXMLLoader(resource);
      Parent root = loader.load();

      @SuppressWarnings("unchecked")
      T controller = loader.getController();

      return new FxmlBundle<>(controller, root);

    } catch (IOException e) {
      throw new RuntimeException("No se pudo cargar el FXML: " + fxmlPath, e);
    }
  }

  public static class FxmlBundle<T> {

    private final T controller;
    private final Parent root;

    public FxmlBundle(T controller, Parent root) {

      this.controller = controller;
      this.root = root;
    }

    public T getController() {

      return controller;
    }

    public Parent getRoot() {

      return root;
    }
  }
}
