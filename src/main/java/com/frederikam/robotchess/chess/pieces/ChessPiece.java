package com.frederikam.robotchess.chess.pieces;

import com.frederikam.robotchess.chess.Alignment;
import com.frederikam.robotchess.chess.Chessboard;
import com.frederikam.robotchess.chess.TilePosition;

import java.util.Optional;

public abstract class ChessPiece {

    final Chessboard chessboard;
    final Alignment alignment;
    TilePosition position;

    ChessPiece(Chessboard chessboard, Alignment alignment, TilePosition position) {
        this.chessboard = chessboard;
        this.alignment = alignment;
        this.position = position;
    }

    public boolean canMoveTo(int x, int y) {
        return canMoveTo(new TilePosition(x, y));
    }

    public boolean canMoveTo(TilePosition newPos) {
        if(chessboard.getPlayerOfTurn() != alignment) return false; // The other player can't move this piece

        // Check that we are not moving to the same tile
        if(position.equals(newPos)) return false;

        // Check that we aren't trying to move to the same tile as an ally
        Optional<ChessPiece> atPos = chessboard.getPieceAt(newPos);
        if (atPos.isPresent()) {
            if (atPos.get().getAlignment() == alignment) return false;
        }

        // Check that we aren't moving out of bounds
        return !newPos.isOutOfBounds();
    }

    public abstract String signatureCharacter();

    public TilePosition getPosition() {
        return position;
    }

    public void setPosition(TilePosition position) {
        this.position = position;
    }

    public Alignment getAlignment() {
        return alignment;
    }
}
