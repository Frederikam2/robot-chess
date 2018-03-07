package com.frederikam.robotchess.chess;

import com.frederikam.robotchess.chess.pieces.ChessPiece;
import com.frederikam.robotchess.chess.pieces.Pawn;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class DiagonalDirectionTest {

    @Test
    public void getTilesInDirection() {
        LinkedList<TilePosition> expected = new LinkedList<>();
        expected.add(new TilePosition(5, 5));
        expected.add(new TilePosition(6, 6));
        expected.add(new TilePosition(7, 7));
        assertEquals(expected, DiagonalDirection.TOP_RIGHT.getTilesInDirection(new TilePosition(4, 4)));

        expected = new LinkedList<>();
        expected.add(new TilePosition(5, 4));
        expected.add(new TilePosition(4, 3));
        expected.add(new TilePosition(3, 2));
        expected.add(new TilePosition(2, 1));
        expected.add(new TilePosition(1, 0));
        assertEquals(expected, DiagonalDirection.BOTTOM_LEFT.getTilesInDirection(new TilePosition(6, 5)));
    }

    @Test
    public void getPiecesInDirection() {
        Chessboard chessboard = new Chessboard();
        Pawn pawn = new Pawn(chessboard, Alignment.WHITE, new TilePosition(5, 5));
        chessboard.put(pawn);

        LinkedList<ChessPiece> expected = new LinkedList<>();
        expected.add(pawn);
        assertEquals(expected, DiagonalDirection.TOP_RIGHT.getPiecesInDirection(chessboard, new TilePosition(0,0)));
    }
}