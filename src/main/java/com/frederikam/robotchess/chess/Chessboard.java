package com.frederikam.robotchess.chess;

import com.frederikam.robotchess.chess.pieces.ChessPiece;

import java.util.LinkedList;
import java.util.Optional;

public class Chessboard {

    private final LinkedList<ChessPiece> pieces;
    private Alignment playerOfTurn = Alignment.WHITE; // White always begins first
    private int whiteDead = 0;
    private int blackDead = 0;

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

    public void sendToGraveyard(ChessPiece piece) {
        switch (piece.getAlignment()) {
            case WHITE:
                piece.setPosition(new TilePosition(
                        -2 + whiteDead%2,
                        whiteDead/2
                ));
                whiteDead++;
                break;
            case BLACK:
                piece.setPosition(new TilePosition(
                        9 - blackDead%2,
                        7 - blackDead/2
                ));
                blackDead++;
                break;
        }
    }

    public Alignment getPlayerOfTurn() {
        return playerOfTurn;
    }
}
