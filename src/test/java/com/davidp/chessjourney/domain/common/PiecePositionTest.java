package com.davidp.chessjourney.domain.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class PiecePositionTest {

  @Test
  public void piecePositionSetTest() {

    Piece pieceKing = PieceFactory.createWhiteKing();
    Pos positionKing = new Pos(Row.ONE, Col.E);
    PiecePosition piecePositionKing = new PiecePosition(pieceKing, positionKing);

    Piece pieceQueen = PieceFactory.createWhiteQueen();
    Pos positionQueen = new Pos(Row.ONE, Col.D);
    PiecePosition piecePositionQueen = new PiecePosition(pieceQueen, positionQueen);

    List<PiecePosition> pieces = new ArrayList<>();
    pieces.add(piecePositionKing);
    pieces.add(piecePositionQueen);

    Set<Pos> pos = PiecePosition.findPiecePosition(PieceType.KING, PieceColor.WHITE, pieces);

    assertEquals(1, pos.size());
    assertEquals(pos.iterator().next(), positionKing);

    assertEquals(2, pieces.size());

    pieces.forEach(System.out::println);
  }

  @Test
  public void piecePositionSetOfRooksTest() {

    Piece pieceRook1 = PieceFactory.createWhiteRook();
    Pos positionRook1 = new Pos(Row.ONE, Col.H);
    PiecePosition piecePositionRook1 = new PiecePosition(pieceRook1, positionRook1);

    Piece pieceRook2 = PieceFactory.createWhiteRook();
    Pos positionRook2 = new Pos(Row.ONE, Col.A);
    PiecePosition piecePositionRook2 = new PiecePosition(pieceRook2, positionRook2);

    Set<PiecePosition> pieces = new HashSet<>();
    pieces.add(piecePositionRook1);
    pieces.add(piecePositionRook2);

    Set<Pos> pos = PiecePosition.findPiecePosition(PieceType.ROOK, PieceColor.WHITE, pieces);

    List<Pos> listOfRooks = new ArrayList<>(pos);

    assertEquals(2, pos.size());

    assertTrue(
        listOfRooks.get(0).equals(positionRook1) || listOfRooks.get(0).equals(positionRook2));
    assertTrue(
        listOfRooks.get(1).equals(positionRook1) || listOfRooks.get(1).equals(positionRook2));
  }
}
