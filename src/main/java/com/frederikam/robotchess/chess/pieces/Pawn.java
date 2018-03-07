package com.frederikam.robotchess.chess.pieces;

import com.frederikam.robotchess.chess.Alignment;
import com.frederikam.robotchess.chess.ChessUtil;
import com.frederikam.robotchess.chess.Chessboard;
import com.frederikam.robotchess.chess.TilePosition;

import java.util.Optional;

public class Pawn extends ChessPiece {

    public Pawn(Chessboard chessboard, Alignment alignment, TilePosition position) {
        super(chessboard, alignment, position);
    }

    @Override
    public boolean canMoveTo(TilePosition newPos) {
        int facing = alignment == Alignment.WHITE ? -1 : 1;
        TilePosition delta = position.minus(newPos);
        int deltaX = delta.x;
        int forward = delta.y / facing; // Delta y adjusted for alignment

        Optional<ChessPiece> pieceAt = chessboard.getPieceAt(newPos);

        switch (deltaX) {
            case 0:
                // We can't move if a piece is in front of us
                if (ChessUtil.getPiecesInRectangle(chessboard, position, newPos).size() != 1) return false;

                if (forward == 2) {
                    // We must not have moved from the Pawn's home line
                    int home = alignment == Alignment.WHITE ? 1 : 6;
                    if (position.y != home) return false;
                } else if (forward != 1) return false; // Must not move too far
                break;
            case -1:
            case 1:
                // Make sure we don't move too far
                if (forward != 1) return false;

                // Make sure we only move if killing an enemy
                if (!pieceAt.isPresent() || pieceAt.get().getAlignment() == alignment) return false;
                break;
            default:
                // Moving too far along x axis
                return false;
        }

        return super.canMoveTo(newPos);
    }

    @Override
    public String signatureCharacter() {
        return alignment == Alignment.WHITE ? "♙" : "♟";
    }
}
