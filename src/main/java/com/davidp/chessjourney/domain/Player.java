package com.davidp.chessjourney.domain;

import com.davidp.chessjourney.domain.common.PieceColor;
import java.util.Objects;

public class Player {

  private final String name;
  private final PieceColor color;

  public Player(final String name, final PieceColor color) {
    this.name = name;
    this.color = color;
  }

  public String getName() {

    return name;
  }

  public PieceColor getColor() {

    return color;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Player player = (Player) o;
    return Objects.equals(name, player.name) && color == player.color;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, color);
  }

  @Override
  public String toString() {
    return "Player{" + "name='" + name + '\'' + ", color=" + color + '}';
  }
}
