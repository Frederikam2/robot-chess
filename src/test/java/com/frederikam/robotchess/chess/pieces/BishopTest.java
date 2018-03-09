package com.frederikam.robotchess.chess.pieces;

import com.frederikam.robotchess.chess.Alignment;
import com.frederikam.robotchess.chess.Chessboard;
import com.frederikam.robotchess.chess.TilePosition;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BishopTest {

    @Test
    public void canMoveTo() {
        Chessboard chessboard = new Chessboard();
        Bishop bishop = new Bishop(chessboard, Alignment.WHITE, new TilePosition(5, 5));
        chessboard.put(bishop);

        assertEquals(true, bishop.canMoveTo(4, 4));
        assertEquals(true, bishop.canMoveTo(4, 6));
        assertEquals(true, bishop.canMoveTo(6, 4));
        assertEquals(true, bishop.canMoveTo(6, 6));


        assertEquals(false, bishop.canMoveTo(5, 4));
        assertEquals(false, bishop.canMoveTo(5, 6));
        assertEquals(false, bishop.canMoveTo(6, 5));
        assertEquals(false, bishop.canMoveTo(4, 5));
    }

    @Test
    public void canMoveToBlocked() {
        Chessboard chessboard = new Chessboard();
        Bishop bishop = new Bishop(chessboard, Alignment.WHITE, new TilePosition(5, 5));
        Bishop bishop2 = new Bishop(chessboard, Alignment.BLACK, new TilePosition(4, 4));
        chessboard.put(bishop);
        chessboard.put(bishop2);
        assertEquals(false, bishop.canMoveTo(3, 3));
        bishop2.position = new TilePosition(3, 3);
        assertEquals(true, bishop.canMoveTo(3, 3));
    }
}