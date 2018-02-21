package com.frederikam.robotchess.chess.pieces;

import com.frederikam.robotchess.chess.TilePosition;

public class King extends ChessPiece {

    @Override
    public boolean canMoveTo(TilePosition newPos) {
        TilePosition diff = position.minus(newPos);

        return diff.x <= 1 && diff.y <= 1 && super.canMoveTo(newPos);
    }
}
