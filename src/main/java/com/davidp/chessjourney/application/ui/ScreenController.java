package com.davidp.chessjourney.application.ui;

import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import javafx.scene.layout.Pane;

public interface ScreenController {

    void setData(InputScreenData inputData);
    void setLayout(double layoutX, double layoutY);
    void show();
    void hide();
    boolean isVisible();
    Pane getRootPane();

}
