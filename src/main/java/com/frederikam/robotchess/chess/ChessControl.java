package com.frederikam.robotchess.chess;

import com.frederikam.robotchess.Constants;
import com.frederikam.robotchess.Launcher;
import com.frederikam.robotchess.chess.pieces.ChessPiece;
import com.frederikam.robotchess.mech.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Class responsible for modifying board state
 */
public class ChessControl {

    private static final Logger log = LoggerFactory.getLogger(ChessControl.class);
    private final Chessboard chessboard;
    private final IWorkspace IWorkspace = Launcher.gpio != null ? new Workspace() : new DummyWorkspace();
    private final MechanicalControl mechanicalControl = new MechanicalControl(IWorkspace);

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
            boolean direct = canMoveDirect(to, chessPiece.getPosition());
            mechanicalControl.queueDragAndDrop(to.toStepPosition(), chessPiece.getPosition().toStepPosition(), direct);
        });

        // Now finally move the actual piece
        TilePosition startPos = pieceFrom.get().getPosition();
        pieceFrom.get().setPosition(to);

        boolean direct = canMoveDirect(startPos, to);
        mechanicalControl.queueDragAndDrop(startPos.toStepPosition(), to.toStepPosition(), direct);
        return true;
    }

    // Can we move directly, or do we need to move indirectly (and thus less elegantly)?
    protected boolean canMoveDirect(TilePosition from, TilePosition to) {
        /*
        // Check if there is anything in a rectangle
        int expected = chessboard.getPieceAt(to).isPresent() ? 2 : 1;
        if (ChessUtil.getPiecesInRectangle(chessboard, from, to).size() == expected) return true;
        */
        // Check if we can move directly by moving a point along the vector,
        //  and seeing if we get too close to any nearby pieces

        int checkCount = (int) Math.floor((from.toStepPosition().minus(to.toStepPosition())
                .magnitude() / Constants.TILE_WIDTH) * 10);

        StepPosition delta = to.toStepPosition().minus(from.toStepPosition());
        for (int i = 0; i <= checkCount; i++) {
            double tweenFactor = ((double) i) / ((double) checkCount);
            StepPosition testPoint = from.toStepPosition().plus(delta.times(tweenFactor));
            Optional<ChessPiece> nearestPiece = chessboard.getPieceAt(testPoint.getNearestTile());

            // Handle if nearest tile has a piece other than on to or from
            if (nearestPiece.isPresent() && !nearestPiece.get().getPosition().equals(from)
                    && !nearestPiece.get().getPosition().equals(to)) {
                double distance = testPoint.minus(testPoint.getNearestTile().toStepPosition()).magnitude();
                log.debug("Near {} with a distance of {} at pos {}", nearestPiece.get().getPosition(), distance, testPoint);
                if (distance < Constants.PIECE_DIAMETER) return false; // Too close!
            }
        }

        return true;
    }

    public Chessboard getChessboard() {
        return chessboard;
    }
}
