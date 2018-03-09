package com.frederikam.robotchess.chess.pieces;

import com.frederikam.robotchess.chess.Alignment;
import com.frederikam.robotchess.chess.Chessboard;
import com.frederikam.robotchess.chess.TilePosition;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KnightTest {

    @Test
    public void canMoveTo() {
        Chessboard chessboard = new Chessboard();
        Knight knight = new Knight(chessboard, Alignment.WHITE, new TilePosition(5, 5));
        chessboard.put(knight);

        assertEquals(true,  knight.canMoveTo(3, 4));
        assertEquals(false, knight.canMoveTo(3, 5));
        assertEquals(true,  knight.canMoveTo(3, 6));
        assertEquals(true,  knight.canMoveTo(4, 7));
        assertEquals(false, knight.canMoveTo(5, 7));
        assertEquals(true,  knight.canMoveTo(6, 7));

        assertEquals(false, knight.canMoveTo(1, 1));
        assertEquals(false, knight.canMoveTo(4, 5));
        assertEquals(false, knight.canMoveTo(5, 5));
        assertEquals(false, knight.canMoveTo(5, 6));
    }

}
