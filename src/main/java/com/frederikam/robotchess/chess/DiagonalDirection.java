package com.frederikam.robotchess.chess;

import com.frederikam.robotchess.chess.pieces.ChessPiece;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public enum DiagonalDirection {
    TOP_RIGHT(1,1),
    BOTTOM_RIGHT(1,-1),
    BOTTOM_LEFT(-1,-1),
    TOP_LEFT(-1,1);

    private final int xFactor;
    private final int yFactor;

    DiagonalDirection(int xFactor, int yFactor) {
        this.xFactor = xFactor;
        this.yFactor = yFactor;
    }

    public List<TilePosition> getTilesInDirection(TilePosition origin) {
        LinkedList<TilePosition> list = new LinkedList<>();

        int i = 1;
        while (true) {
            TilePosition pos = new TilePosition(origin.x + i * xFactor, origin.y + i * yFactor);
            if (pos.isOutOfBounds()) break;
            list.add(pos);
            i++;
        }

        return list;
    }

    public List<ChessPiece> getPiecesInDirection(Chessboard chessboard, TilePosition origin) {
        List<ChessPiece> pieces = new LinkedList<>();
        List<TilePosition> tiles = getTilesInDirection(origin);
        for (TilePosition tile : tiles) {
            Optional<ChessPiece> pieceAt = chessboard.getPieceAt(tile);
            pieceAt.ifPresent(pieces::add);
        }
        return pieces;
    }
}
