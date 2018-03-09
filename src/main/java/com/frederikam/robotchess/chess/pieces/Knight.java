package com.frederikam.robotchess.chess.pieces;

import com.frederikam.robotchess.chess.Alignment;
import com.frederikam.robotchess.chess.Chessboard;
import com.frederikam.robotchess.chess.TilePosition;

public class Knight extends ChessPiece {

    public Knight(Chessboard chessboard, Alignment alignment, TilePosition position) {
        super(chessboard, alignment, position);
    }

    @Override
    public boolean canMoveTo(TilePosition newPos) {
        return super.canMoveTo(newPos);
    }

    @Override
    public String signatureCharacter() {
        return alignment == Alignment.WHITE ? "♘" : "♞";
    }
}