package com.frederikam.robotchess.chess.pieces;

import com.frederikam.robotchess.chess.Alignment;
import com.frederikam.robotchess.chess.TilePosition;

public class King extends ChessPiece {

    King(Alignment alignment, TilePosition position) {
        super(alignment, position);
    }

    @Override
    public boolean canMoveTo(TilePosition newPos) {
        TilePosition diff = position.minus(newPos);

        return diff.x <= 1 && diff.y <= 1 && super.canMoveTo(newPos);
    }
}
