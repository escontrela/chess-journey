package com.davidp.chessjourney.application.factories;

import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.ScreenPanel;
import com.davidp.chessjourney.application.ui.menu.MenuViewController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.ui.settings.SettingsViewController;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

/**
 * This class is responsible for creating the screens that will be added to the main panel on the
 * main STAGE.
 */
public class ScreenFactory {

  /** This is the set of screen that can be added to the main panel on the main STAGE. */
  public enum Screens {
    SETTINGS("/com/davidp/chessjourney/setting-view.fxml"),
    MENU("/com/davidp/chessjourney/menu-view.fxml"),
    BOARD("/com/davidp/chessjourney/board-view-2.fxml");

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


  public ScreenController createScreen(Screens screen)
          throws IOException {
    switch (screen) {
      case MENU:

        return getMenuScreen();
      case SETTINGS:

        return getSettingsScreen();
      case BOARD:

        return getBoardScreen();
      default:
        throw new IllegalArgumentException("Screen not supported: " + screen);
    }
  }

  private ScreenController getSettingsScreen() {

    FxmlBundle<SettingsViewController> objectFxmlBundle = loadFxml(Screens.SETTINGS.resourceName());
    var controller = objectFxmlBundle.getController();
    controller.setGetUserByIdUseCase(UseCaseFactory.createGetUserByIdUseCase());
    controller.setSaveUserUseCase(UseCaseFactory.createSaveUserUseCase());

    return controller;
  }


  protected ScreenController getMenuScreen() {

      FxmlBundle<MenuViewController> objectFxmlBundle = loadFxml(Screens.MENU.resourceName());
      return objectFxmlBundle.getController();
  }

  protected ScreenController getBoardScreen() {

    FxmlBundle<MenuViewController> objectFxmlBundle = loadFxml(Screens.BOARD.resourceName());
    //TODO: set the use cases here
    return objectFxmlBundle.getController();
  }

  protected ScreenPanel<MenuViewController, InputScreenData> createMenuScreen(
      InputScreenData inputData) throws IOException {

    FxmlBundle<MenuViewController> objectFxmlBundle = loadFxml(Screens.MENU.resourceName());
    MenuViewController controller = objectFxmlBundle.getController();

    Pane root = (Pane) objectFxmlBundle.getRoot();
    root.setLayoutX(inputData.getLayoutX());
    root.setLayoutY(inputData.getLayoutY());
    return new ScreenPanel<>(root, controller);
  }

  /**
   * Carga un archivo FXML y devuelve un objeto FxmlBundle que contiene el controlador y el nodo
   * raíz.
   **/
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
