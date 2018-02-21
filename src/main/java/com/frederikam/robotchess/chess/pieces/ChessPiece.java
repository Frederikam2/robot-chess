package com.frederikam.robotchess.chess.pieces;

import com.frederikam.robotchess.chess.TilePosition;

public abstract class ChessPiece {

    TilePosition position;

    public boolean canMoveTo(TilePosition newPos) {
        // Check that we are not moving to the same tile
        return !position.equals(newPos) && !newPos.isOutOfBounds();

        // TODO: Check for other friendly piece on target board
    }

}
