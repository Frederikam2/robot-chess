package com.frederikam.robotchess.chess.pieces;

import com.frederikam.robotchess.chess.Alignment;
import com.frederikam.robotchess.chess.Chessboard;
import com.frederikam.robotchess.chess.TilePosition;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class RookTest {

    private static final Logger log = LoggerFactory.getLogger(RookTest.class);

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

    @Test
    public void canMoveToBlocked() {
        Chessboard chessboard = new Chessboard();
        Rook rook = new Rook(chessboard, Alignment.WHITE, new TilePosition(5, 5));
        Rook rook2 = new Rook(chessboard, Alignment.BLACK, new TilePosition(5, 4));
        chessboard.put(rook);
        chessboard.put(rook2);
        assertEquals(false, rook.canMoveTo(5, 3));
        assertEquals(true, rook.canMoveTo(5, 4));
    }

}
