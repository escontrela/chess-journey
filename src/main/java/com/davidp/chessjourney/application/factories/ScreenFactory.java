package com.davidp.chessjourney.application.factories;

import com.davidp.chessjourney.application.ui.settings.InputData;
import com.davidp.chessjourney.application.ui.settings.SettingsViewController;
import com.davidp.chessjourney.application.ui.ScreenPanel;
import com.davidp.chessjourney.application.ui.settings.SettingsViewData;
import javafx.fxml.FXMLLoader;

import javafx.scene.layout.Pane;

import java.io.IOException;


public class ScreenFactory {

    public enum Screens {
        SETTINGS,
        BOARD
    }

    private static volatile ScreenFactory instance;

    private ScreenFactory() {
        // Constructor privado para evitar instancias
    }

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

    /**
     * Carga la pantalla solicitada (FXML) y devuelve un ScreenPanel
     * que contiene el root + controller + datos (opcional).
     */
    public <C,D> ScreenPanel<C, D> createScreen(Screens screen, InputData inputData) throws IOException {
        switch (screen) {
            case SETTINGS:
                return (ScreenPanel<C, D>) createSettingsScreen((SettingsViewData) inputData);

            case BOARD:
                // Aún no implementado
                throw new RuntimeException("Not implemented yet!.");

            default:
                throw new IllegalArgumentException("Screen not supported: " + screen);
        }
    }



    // Método privado para encapsular la creación de la pantalla SETTINGS
    private ScreenPanel<SettingsViewController, SettingsViewData> createSettingsScreen(SettingsViewData inputData) throws IOException {

        //TODO al controler habrá que pasarle varias cosas:
            // - El objeto de entrada Settings View Data con los parémtros de entrada.
            // - La posición en la que queremos mostrar la pantalla
            // - Los casos de uso instanciados para que su contoller resuelva los casos de uso de la aplicación



        // Asume que setting-view.fxml está en el mismo package que esta clase
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/davidp/chessjourney/setting-view.fxml"));
        Pane root = loader.load();
        SettingsViewController controller = loader.getController();

        // Creamos el ScreenPanel con controlador + root
        ScreenPanel<SettingsViewController, SettingsViewData> screenPanel = new ScreenPanel<>(root, controller);

        controller.setSettingsViewData(inputData);
        controller.setGetUserByIdUseCase(UseCaseFactory.createGetUserByIdUseCase());
        controller.setSaveUserUseCase(UseCaseFactory.createSaveUserUseCase());
        root.setLayoutX(inputData.getLayoutX());
        root.setLayoutY(inputData.getLayoutY());
        controller.refreshUserInfo();

        return screenPanel;
    }



}
