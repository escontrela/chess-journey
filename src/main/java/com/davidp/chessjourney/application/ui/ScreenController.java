package com.davidp.chessjourney.application.ui;

import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import javafx.scene.layout.Pane;

public interface ScreenController {

    enum ScreenStatus {
        INITIALIZED,
        VISIBLE,
        HIDDEN
    }

    void setData(InputScreenData inputData);
    void setLayout(double layoutX, double layoutY);
    void show();
    void show(InputScreenData inputData);
    void hide();
    Pane getRootPane();
    ScreenStatus getStatus();
    boolean isInitialized();
    boolean isVisible();
    boolean isHidden();

}
