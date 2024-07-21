package com.davidp.chessjourney.domain.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PosTest {

  @Test
  public void posTest() {

    Pos pos1 = Pos.of(Col.E, Row.FOUR);
    assertSame(pos1.getRow(), Row.FOUR);
    assertSame(pos1.getCol(), Col.E);

    Pos pos2 = Pos.parseString("e4");
    assertSame(pos2.getRow(), Row.FOUR);
    assertSame(pos2.getCol(), Col.E);
  }
}
