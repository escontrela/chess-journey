package com.davidp.chessjourney.domain.common;

import java.util.Objects;

/**
 * A FEN record contains six fields, each separated by a space. The fields are as follows:[5]
 *
 * <p>Value Object
 */
public class Fen {

  static final String INITIAL = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
  static final String EMPTY_BOARD = "8/8/8/8/8/8/8/8 w - - 0 1";

  private final String fenString;

  public Fen(final String fenString) {

    Objects.requireNonNull(fenString, "fenString cannot be null");

    if (fenString.split(" ").length != 6) {

      throw new IllegalArgumentException("Fen string must have 6 fields");
    }

    this.fenString = fenString;
  }

  public String getStringValue() {

    return this.fenString;
  }

  public static Fen createCustom(final String fenString) {

    return new Fen(fenString);
  }

  public static Fen createEmptyBoard() {

    return new Fen(EMPTY_BOARD);
  }

  public static Fen createInitial() {

    return new Fen(INITIAL);
  }

  // TOO Fen createRuyLopezOpening() {

}
