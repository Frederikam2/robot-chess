package com.frederikam.robotchess.chess;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChessControlTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void canMoveDirect() {
        Chessboard chessboard = new Chessboard();
        chessboard.populate();
        ChessControl chessControl = new ChessControl(chessboard);

        // Test left bishop moving to the right
        assertEquals(false, chessControl.canMoveDirect(new TilePosition(2, 0), new TilePosition(4, 2)));
        // Test left knight
        assertEquals(false, chessControl.canMoveDirect(new TilePosition(1, 0), new TilePosition(2, 2)));

        // Remove fourth pawn
        chessboard.sendToGraveyard(chessboard.getPieceAt(new TilePosition(3, 1)).get());

        // We should be able to move directly where the pawn was
        assertEquals(true, chessControl.canMoveDirect(new TilePosition(2, 0), new TilePosition(4, 2)));
    }
}