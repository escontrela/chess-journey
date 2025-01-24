package com.davidp.chessjourney.application.ui.chess;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class PieceView extends ImageView {

    String type;
    String color;
    int posX, posY;

    public PieceView(String color) {
        this.color = color;
        addEventHandler();
    }

    public String getColor() {
        return this.color;
    }

    public void setPiece(Image image) {
        this.setImage(image);
    }

    public void setImage() {
        System.out.println("setting image:" + "sample/pieces/" + this.color +  this.type + ".png");
        this.setPiece(new Image("com/davidp/chessjourney/sample/pieces/" + this.color +  this.type + ".png"));
    }

    private void addEventHandler() {
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });


    }


    @Override
    public String toString() {
        return this.color + " " + this.type;
    }

}
