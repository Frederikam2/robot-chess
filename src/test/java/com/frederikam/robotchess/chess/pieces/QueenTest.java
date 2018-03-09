package com.frederikam.robotchess.chess.pieces;

import com.frederikam.robotchess.chess.Alignment;
import com.frederikam.robotchess.chess.Chessboard;
import com.frederikam.robotchess.chess.TilePosition;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QueenTest {

    @Test
    public void canMoveTo() {
        Chessboard chessboard = new Chessboard();
        Queen queen = new Queen(chessboard, Alignment.WHITE, new TilePosition(5, 5));
        chessboard.put(queen);

        assertEquals(true, queen.canMoveTo(new TilePosition(5, 7)));
        assertEquals(true, queen.canMoveTo(new TilePosition(7, 7)));
        assertEquals(true, queen.canMoveTo(new TilePosition(7, 5)));
        assertEquals(true, queen.canMoveTo(new TilePosition(7, 3)));
        assertEquals(true, queen.canMoveTo(new TilePosition(5, 3)));
        assertEquals(true, queen.canMoveTo(new TilePosition(3, 3)));
        assertEquals(true, queen.canMoveTo(new TilePosition(3, 5)));
        assertEquals(true, queen.canMoveTo(new TilePosition(3, 7)));

        // Blind spots in a 5x5 with the queen in the center
        assertEquals(false, queen.canMoveTo(new TilePosition(6, 7)));
        assertEquals(false, queen.canMoveTo(new TilePosition(7, 6)));
        assertEquals(false, queen.canMoveTo(new TilePosition(7, 4)));
        assertEquals(false, queen.canMoveTo(new TilePosition(6, 3)));
        assertEquals(false, queen.canMoveTo(new TilePosition(4, 3)));
        assertEquals(false, queen.canMoveTo(new TilePosition(3, 4)));
        assertEquals(false, queen.canMoveTo(new TilePosition(3, 6)));
        assertEquals(false, queen.canMoveTo(new TilePosition(4, 7)));
    }
}