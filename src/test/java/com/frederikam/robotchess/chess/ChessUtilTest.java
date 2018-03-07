package com.frederikam.robotchess.chess;

import org.junit.Assert;
import org.junit.Test;

public class ChessUtilTest {

    @Test
    public void getPiecesInRectangle() {
        Chessboard chessboard = new Chessboard();
        chessboard.populate();
        Assert.assertEquals(ChessUtil.getPiecesInRectangle(
                chessboard,
                new TilePosition(1, 1),
                new TilePosition(6, 6)
        ).size(), 12);
    }
}