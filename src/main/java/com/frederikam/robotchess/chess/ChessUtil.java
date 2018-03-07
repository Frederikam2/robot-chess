package com.frederikam.robotchess.chess;

import com.frederikam.robotchess.chess.pieces.ChessPiece;

import java.util.LinkedList;
import java.util.List;

public class ChessUtil {

    public static List<ChessPiece> getPiecesInRectangle(Chessboard chessboard, TilePosition from, TilePosition to) {
        int lowX  = Math.min(from.x, to.x);
        int lowY  = Math.min(from.y, to.y);
        int highX = Math.max(from.x, to.x);
        int highY = Math.max(from.y, to.y);

        LinkedList<ChessPiece> list = new LinkedList<>();

        for(ChessPiece piece : chessboard.getPieces()) {
            TilePosition pos = piece.getPosition();
            if (lowX <= pos.x
                    && highX >= pos.x
                    && lowY <= pos.y
                    && highY >= pos.y) {
                list.add(piece);
            }
        }

        return list;
    }

}
