package com.davidp.chessjourney.domain.common;

public class IllegalMoveException extends RuntimeException {
  public IllegalMoveException() {
    super();
  }

  public IllegalMoveException(String message) {
    super(message);
  }

  public IllegalMoveException(String message, Throwable cause) {
    super(message, cause);
  }

  public IllegalMoveException(Throwable cause) {
    super(cause);
  }
}
