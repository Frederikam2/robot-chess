package com.frederikam.robotchess.chess.pieces;

import com.frederikam.robotchess.chess.Alignment;
import com.frederikam.robotchess.chess.ChessUtil;
import com.frederikam.robotchess.chess.Chessboard;
import com.frederikam.robotchess.chess.TilePosition;

import java.util.List;

public class Rook extends ChessPiece {

    public Rook(Chessboard chessboard, Alignment alignment, TilePosition position) {
        super(chessboard, alignment, position);
    }

    @Override
    public boolean canMoveTo(TilePosition newPos) {
        if (position.x != newPos.x && position.y != newPos.y) return false;

        // Check collision
        List<ChessPiece> inTheWay = ChessUtil.getPiecesInRectangle(chessboard, position, newPos);
        int expected = chessboard.getPieceAt(newPos).isPresent() ? 2 : 1;
        if (inTheWay.size() != expected) return false;

        return super.canMoveTo(newPos);
    }

    @Override
    public String signatureCharacter() {
        return alignment == Alignment.WHITE ? "♖" : "♜";
    }
}
