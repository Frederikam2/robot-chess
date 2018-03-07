package com.frederikam.robotchess.chess.pieces;

import com.frederikam.robotchess.chess.Alignment;
import com.frederikam.robotchess.chess.Chessboard;
import com.frederikam.robotchess.chess.TilePosition;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PawnTest {

    @Test
    public void canMoveTo() {
        Chessboard chessboard = new Chessboard();
        Pawn pawn = new Pawn(chessboard, Alignment.WHITE, new TilePosition(5, 5));
        chessboard.put(pawn);

        assertEquals(pawn.canMoveTo(new TilePosition(5, 6)), true);
        assertEquals(pawn.canMoveTo(new TilePosition(4, 6)), false);
        assertEquals(pawn.canMoveTo(new TilePosition(6, 6)), false);
        assertEquals(pawn.canMoveTo(new TilePosition(5, 4)), false);
        assertEquals(pawn.canMoveTo(new TilePosition(5, 7)), false);

        // Make sure blocking works
        chessboard.put(new Pawn(chessboard, Alignment.WHITE, new TilePosition(4, 6)));
        chessboard.put(new Pawn(chessboard, Alignment.WHITE, new TilePosition(5, 6)));
        chessboard.put(new Pawn(chessboard, Alignment.WHITE, new TilePosition(6, 6)));
        assertEquals(pawn.canMoveTo(new TilePosition(4, 6)), false);
        assertEquals(pawn.canMoveTo(new TilePosition(5, 6)), false);
        assertEquals(pawn.canMoveTo(new TilePosition(6, 6)), false);

        chessboard.getPieces().clear();
        chessboard.put(pawn);

        // Now test again with enemy pawns
        chessboard.put(new Pawn(chessboard, Alignment.BLACK, new TilePosition(4, 6)));
        chessboard.put(new Pawn(chessboard, Alignment.BLACK, new TilePosition(5, 6)));
        chessboard.put(new Pawn(chessboard, Alignment.BLACK, new TilePosition(6, 6)));
        assertEquals(pawn.canMoveTo(new TilePosition(4, 6)), true);
        assertEquals(pawn.canMoveTo(new TilePosition(5, 6)), false);
        assertEquals(pawn.canMoveTo(new TilePosition(6, 6)), true);

        pawn.setPosition(new TilePosition(5, 1));
        // Test moving forward by two
        assertEquals(pawn.canMoveTo(new TilePosition(5, 3)), true);
        Pawn blocking = new Pawn(chessboard, Alignment.BLACK, new TilePosition(5, 3));
        chessboard.put(blocking);
        assertEquals(pawn.canMoveTo(new TilePosition(5, 3)), false);
        blocking.setPosition(new TilePosition(5, 2));
        assertEquals(pawn.canMoveTo(new TilePosition(5, 3)), false);

        // Test black pawn
        chessboard.onTurnEnd();
        Pawn black = new Pawn(chessboard, Alignment.BLACK, new TilePosition(5, 6));
        assertEquals(black.canMoveTo(new TilePosition(5, 5)), true);
        assertEquals(black.canMoveTo(new TilePosition(5, 4)), true);
    }
}