package com.frederikam.robotchess.chess.pieces;

import com.frederikam.robotchess.chess.Alignment;
import com.frederikam.robotchess.chess.TilePosition;

public abstract class ChessPiece {

    private final Alignment alignment;
    TilePosition position;

    ChessPiece(Alignment alignment, TilePosition position) {
        this.alignment = alignment;
        this.position = position;
    }

    public boolean canMoveTo(TilePosition newPos) {
        // Check that we are not moving to the same tile
        return !position.equals(newPos) && !newPos.isOutOfBounds();

        // TODO: Check for other friendly piece on target board
    }

    abstract char signatureCharacter();

}
