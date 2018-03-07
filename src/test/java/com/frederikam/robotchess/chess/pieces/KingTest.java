package com.frederikam.robotchess.chess.pieces;

import com.frederikam.robotchess.chess.Alignment;
import com.frederikam.robotchess.chess.Chessboard;
import com.frederikam.robotchess.chess.TilePosition;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KingTest {

    @Test
    public void canMoveTo() {
        Chessboard chessboard = new Chessboard();
        King king = new King(chessboard, Alignment.WHITE, new TilePosition(5, 5));
        chessboard.put(king);

        assertEquals(king.canMoveTo(new TilePosition(4, 4)), true);
        assertEquals(king.canMoveTo(new TilePosition(5, 4)), true);
        assertEquals(king.canMoveTo(new TilePosition(6, 6)), true);

        assertEquals(king.canMoveTo(new TilePosition(5, 5)), false);
        assertEquals(king.canMoveTo(new TilePosition(7, 5)), false);
        assertEquals(king.canMoveTo(new TilePosition(0, 0)), false);
    }
}