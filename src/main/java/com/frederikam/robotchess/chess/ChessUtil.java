package com.frederikam.robotchess.chess;

import com.frederikam.robotchess.chess.pieces.ChessPiece;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // Eg "pawn to A4"
    public static List<ChessPiece> getPiecesOfTypeWhichCanMoveTo(Chessboard chessboard,
                                                                 Class<? extends ChessPiece> type,
                                                                 TilePosition to) {
        return chessboard.getPieces()
                .stream()
                .filter(type::isInstance)
                .filter((p) -> p.canMoveTo(to))
                .collect(Collectors.toList());
    }

    // Eg "A1 to rook"
    public static List<ChessPiece> getTargetsOfTypeWhichWeCanMoveTo(Chessboard chessboard,
                                                               Class<? extends ChessPiece> type,
                                                               TilePosition from) {
        Optional<ChessPiece> pieceAt = chessboard.getPieceAt(from);

        if (!pieceAt.isPresent()) return new LinkedList<>();
        ChessPiece fromPiece = pieceAt.get();

        return chessboard.getPieces()
                .stream()
                .filter(type::isInstance)
                .filter((p) -> fromPiece.canMoveTo(p.getPosition()))
                .collect(Collectors.toList());
    }

}
