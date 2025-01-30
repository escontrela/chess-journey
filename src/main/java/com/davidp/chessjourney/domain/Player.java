package com.davidp.chessjourney.domain;

import java.util.Objects;

public class Player {

  private final String name;
  private final Long userId;

  public Player(final String name,final Long userId) {
    this.name = name;
    this.userId = userId;
  }

  public String getName() {

    return name;
  }

  public Long getUserId(){

    return userId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Player player = (Player) o;
    return Objects.equals(name, player.name);
  }

  @Override
  public int hashCode() {

    return Objects.hash(name);
  }

  @Override
  public String toString() {

    return "Player{" + "name='" + name + "', userId=" + userId + "}'";
  }
}
