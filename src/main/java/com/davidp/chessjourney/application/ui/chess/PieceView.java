package com.davidp.chessjourney.application.ui.chess;

import com.davidp.chessjourney.domain.common.Piece;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class PieceView extends ImageView {

    String type;
    String color;
    int posX, posY;

    public void setDomainPiece(Piece domainPiece) {

        this.domainPiece = domainPiece;
    }

    public Piece getDomainPiece() {

        return this.domainPiece;
    }

  Piece domainPiece;

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
                System.out.println("Piece clicked: " + PieceView.this.toString());
            }
        });

    }


    @Override
    public String toString() {
        return this.color + " " + this.type;
    }

}
