package com.davidp.chessjourney.domain.common;

public class TimeControl {
  private final int minutes;
  private final int increment;

  public TimeControl(int minutes, int increment) {
    if (minutes < 0 || increment < 0) {
      throw new IllegalArgumentException("Time values must be non-negative.");
    }
    this.minutes = minutes;
    this.increment = increment;
  }

  public int getMinutes() {
    return minutes;
  }

  public int getIncrement() {
    return increment;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TimeControl that = (TimeControl) o;

    if (minutes != that.minutes) return false;
    return increment == that.increment;
  }

  @Override
  public int hashCode() {
    int result = minutes;
    result = 31 * result + increment;
    return result;
  }

  @Override
  public String toString() {
    return "TimeControl{" + "minutes=" + minutes + ", increment=" + increment + '}';
  }

  public static TimeControl onePlusZero() {
    return new TimeControl(1, 0);
  }

  public static TimeControl threePlusOne() {
    return new TimeControl(3, 1);
  }

  public static TimeControl threePlusZero() {
    return new TimeControl(3, 0);
  }

  public static TimeControl threePlusTwo() {
    return new TimeControl(3, 2);
  }

  public static TimeControl fivePlusZero() {
    return new TimeControl(5, 0);
  }

  public static TimeControl fivePlusThree() {
    return new TimeControl(5, 3);
  }

  public static TimeControl tenPlusZero() {
    return new TimeControl(10, 0);
  }

  public static TimeControl tenPlusFive() {
    return new TimeControl(10, 5);
  }

  public static TimeControl fifteenPlusTen() {
    return new TimeControl(15, 10);
  }

  public static TimeControl thirtyPlusZero() {
    return new TimeControl(30, 0);
  }

  public static TimeControl thirtyPlusTwenty() {
    return new TimeControl(30, 20);
  }
}
