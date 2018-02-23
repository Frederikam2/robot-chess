package com.frederikam.robotchess.chess;

import com.frederikam.robotchess.chess.pieces.ChessPiece;
import com.frederikam.robotchess.mech.MechanicalControl;
import com.frederikam.robotchess.mech.Workspace;

import java.util.Optional;

/**
 * Class responsible for modifying board state
 */
public class ChessControl {

    private final Chessboard chessboard;
    private final Workspace workspace = new Workspace();
    private final MechanicalControl mechanicalControl = new MechanicalControl(workspace);

    public ChessControl(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public boolean move(TilePosition from, TilePosition to) {
        Optional<ChessPiece> pieceFrom = chessboard.getPieceAt(from);
        Optional<ChessPiece> pieceTo = chessboard.getPieceAt(to);

        if (from.isOutOfBounds()) return false;
        if (!pieceFrom.isPresent()) return false; // Piece must be present
        if(!pieceFrom.get().canMoveTo(to)) return false; // Piece refuses to move there

        pieceTo.ifPresent(chessPiece -> {
            // A piece is here, so we should kill it first
            chessboard.sendToGraveyard(chessPiece);
            mechanicalControl.queueDragAndDrop(to.toStepPosition(), chessPiece.getPosition().toStepPosition());
        });

        // Now finally move the actual piece
        TilePosition startPos = pieceFrom.get().getPosition();
        pieceFrom.get().setPosition(to);
        mechanicalControl.queueDragAndDrop(startPos.toStepPosition(), to.toStepPosition());
        return true;
    }

}
