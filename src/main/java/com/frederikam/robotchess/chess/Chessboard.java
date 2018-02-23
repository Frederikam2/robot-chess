package com.frederikam.robotchess.chess;

import com.frederikam.robotchess.chess.pieces.ChessPiece;

import java.util.LinkedList;
import java.util.Optional;

public class Chessboard {

    private final LinkedList<ChessPiece> pieces;
    private Alignment playerOfTurn = Alignment.WHITE; // White always begins first

    public Chessboard() {
        pieces = new LinkedList<>();

        // Todo: Instantiate chess pieces
    }

    public Optional<ChessPiece> getPieceAt(TilePosition tile) {
        for (ChessPiece piece : pieces) {
            if (piece.getPosition().equals(tile)) return Optional.of(piece);
        }

        return Optional.empty();
    }

    public Alignment getPlayerOfTurn() {
        return playerOfTurn;
    }
}
