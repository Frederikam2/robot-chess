package com.frederikam.robotchess.chess.pieces;

import com.frederikam.robotchess.chess.Alignment;
import com.frederikam.robotchess.chess.Chessboard;
import com.frederikam.robotchess.chess.TilePosition;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RookTest {

    @Test
    public void canMoveTo() {
        Chessboard chessboard = new Chessboard();
        Rook rook = new Rook(chessboard, Alignment.WHITE, new TilePosition(5, 5));
        chessboard.put(rook);

        assertEquals(true, rook.canMoveTo(0, 5));
        assertEquals(true, rook.canMoveTo(7, 5));
        assertEquals(true, rook.canMoveTo(5, 0));
        assertEquals(true, rook.canMoveTo(5, 7));

        assertEquals(false, rook.canMoveTo(4, 4));
        assertEquals(false, rook.canMoveTo(2, 3));
        assertEquals(false, rook.canMoveTo(0, 4));
        assertEquals(false, rook.canMoveTo(6, 6));
    }

}
