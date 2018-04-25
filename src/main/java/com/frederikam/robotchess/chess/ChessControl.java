package com.frederikam.robotchess.chess;

import com.frederikam.robotchess.Launcher;
import com.frederikam.robotchess.chess.pieces.ChessPiece;
import com.frederikam.robotchess.mech.*;
import com.frederikam.robotchess.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Class responsible for modifying board state
 */
public class ChessControl {

    private static final Logger log = LoggerFactory.getLogger(ChessControl.class);
    private final Chessboard chessboard;
    private final IWorkspace workspace = Launcher.gpio != null ? new Workspace() : new DummyWorkspace();
    private final MechanicalControl mechanicalControl = new MechanicalControl(workspace);

    public ChessControl(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public boolean move(TilePosition from, TilePosition to, boolean force) {
        if (from.equals(to)) return false;

        Optional<ChessPiece> pieceFrom = chessboard.getPieceAt(from);
        Optional<ChessPiece> pieceTo = chessboard.getPieceAt(to);

        if (from.isOutOfBounds() && !force) {
            log.warn("Can't move out of bounds");
            return false;
        }
        if (!pieceFrom.isPresent()) {
            log.warn("Can't move from {} when no piece is present", from);
            return false;
        }
        if(!pieceFrom.get().canMoveTo(to) && !force) {
            log.warn("{} can't move from {} to {}", pieceFrom.get(), from, to);
            return false;
        }

        pieceTo.ifPresent(chessPiece -> {
            // A piece is here, so we should kill it first
            chessboard.sendToGraveyard(chessPiece);
            boolean direct = canMoveDirect(to, chessPiece.getPosition());
            mechanicalControl.queueDragAndDrop(
                    to.toStepPosition(),
                    chessPiece.getPosition().toStepPosition(),
                    direct);
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

    // From stdin
    public void processCommand(String command) {
        if (command.startsWith("RESET")) {
            log.info("resetting");
            mechanicalControl.reset();
            return;
        } else if (command.startsWith("FORCE")) {
            String[] split = command.split(" ");
            StepPosition stepPos = new StepPosition(Double.parseDouble(split[1]), Double.parseDouble(split[2]));
            mechanicalControl.submit(() -> workspace.moveToSync(stepPos));
            return;
        } else if (command.startsWith("GOTO")) {
            String[] split = command.split(" ");

            TilePosition position;
            if (split.length == 2) {
                position = new TilePosition(split[1]);
            } else {
                position = new TilePosition(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            }

            mechanicalControl.submit(() -> workspace.moveToSync(position.toStepPosition()));
            return;
        } else if (command.startsWith("MAGNET")) {
            String[] split = command.split(" ");
            workspace.setMagnetEnabled(Boolean.parseBoolean(split[1]));
            return;
        }

        if (command.length() != 4) return;

        TilePosition from;
        TilePosition to;
        try {
            from = new TilePosition(command.substring(0, 2));
            to = new TilePosition(command.substring(2, 4));
        } catch (IllegalArgumentException e) {
            return;
        }
        Optional<ChessPiece> piece = getChessboard().getPieceAt(from);

        boolean valid = move(from, to, false);
        if (valid) {
            //noinspection ConstantConditions
            log.info("Moved {} from {} to {}",
                    piece.get().getClass().getSimpleName(),
                    command.substring(0, 2),
                    command.substring(2, 4));
            getChessboard().onTurnEnd();
        } else {
            log.info("Invalid move");
        }
    }

    public void resetBoard() {
        log.info("Reset board requested");
        chessboard.getPieces().forEach((p) -> move(p.getPosition(), p.getStartPosition(), true));
        chessboard.onReset();
    }

    public MechanicalControl getMechanicalControl() {
        return mechanicalControl;
    }

    public Chessboard getChessboard() {
        return chessboard;
    }

    public IWorkspace getWorkspace() {
        return workspace;
    }
}
