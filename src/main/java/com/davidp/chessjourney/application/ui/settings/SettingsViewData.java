package com.davidp.chessjourney.application.ui.settings;

public class SettingsViewData extends InputData {

    private final Long userId;
    private double layoutX;
    private double layoutY;

    public SettingsViewData(Long userId,double layoutX,double layoutY){

        this.userId = userId;
        this.layoutX = layoutX;
        this.layoutY = layoutY;
    }

    public Long getUserId() {
        return userId;
    }

    public double getLayoutX() {
        return layoutX;
    }

    public double getLayoutY() {
        return layoutY;
    }
}
