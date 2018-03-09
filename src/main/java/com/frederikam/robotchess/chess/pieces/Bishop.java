package com.frederikam.robotchess.chess.pieces;

import com.frederikam.robotchess.chess.Alignment;
import com.frederikam.robotchess.chess.Chessboard;
import com.frederikam.robotchess.chess.DiagonalDirection;
import com.frederikam.robotchess.chess.TilePosition;

import java.util.List;
import java.util.Optional;

public class Bishop extends ChessPiece {

    Bishop(Chessboard chessboard, Alignment alignment, TilePosition position) {
        super(chessboard, alignment, position);
    }

    @Override
    public boolean canMoveTo(TilePosition newPos) {
        Optional<DiagonalDirection> direction = DiagonalDirection.getDirection(position, newPos);

        if (!direction.isPresent()) return false;
        List<ChessPiece> inTheWay = direction.get().getPiecesInDirection(chessboard, position);
        int moveDistance = position.minus(newPos).abs().x;
        for (ChessPiece piece : inTheWay) {
            // Check if anything is in the way (besides on the target tile)
            int pieceDistance = position.minus(piece.position).abs().x;
            if (moveDistance > pieceDistance) return false;
        }

        return super.canMoveTo(newPos);
    }

    @Override
    public String signatureCharacter() {
        return alignment == Alignment.WHITE ? "♗" : "♝";
    }
}
