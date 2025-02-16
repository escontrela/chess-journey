package com.davidp.chessjourney.application.ui.board;

import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.domain.common.Pos;
import java.awt.*;

public class ExerciseResultViewInputScreenData extends InputScreenData {

    private final int percentage;

    public ExerciseResultViewInputScreenData(double layoutX, double layoutY, int percentage) {

        super(layoutX, layoutY);
        this.percentage = percentage;
    }

    public int getPercentage(){

        return percentage;
    }

    public static ExerciseResultViewInputScreenData from(final Point point,final int percentage) {

        return new ExerciseResultViewInputScreenData(point.getX(), point.getY(), percentage);
    }
}