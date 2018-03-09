package com.frederikam.robotchess.chess.pieces;

import com.frederikam.robotchess.chess.Alignment;
import com.frederikam.robotchess.chess.Chessboard;
import com.frederikam.robotchess.chess.TilePosition;

public class Queen extends ChessPiece {

    private final Rook dummyRook;
    private final Bishop dummyBishop;

    public Queen(Chessboard chessboard, Alignment alignment, TilePosition position) {
        super(chessboard, alignment, position);
        dummyRook = new Rook(chessboard, alignment, position);
        dummyBishop = new Bishop(chessboard, alignment, position);
    }

    @Override
    public boolean canMoveTo(TilePosition newPos) {
        dummyRook.setPosition(position);
        dummyBishop.setPosition(position);

        return dummyRook.canMoveTo(newPos) || dummyBishop.canMoveTo(newPos);
    }

    @Override
    public String signatureCharacter() {
        return alignment == Alignment.WHITE ? "♕" : "♛";
    }
}
