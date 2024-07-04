package com.davidp.chessjourney;

import java.util.Objects;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ChessPiece {

  private ImageView imageView;

  public ChessPiece(String imageFile) {
    Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imageFile)));
    imageView = new ImageView(image);
    imageView.setFitWidth(100);
    imageView.setFitHeight(100);
  }

  public ImageView getImageView() {
    return imageView;
  }
}
