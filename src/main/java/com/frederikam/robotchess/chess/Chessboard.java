package com.frederikam.robotchess.chess;

import com.frederikam.robotchess.chess.pieces.ChessPiece;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;

public class Chessboard {

    private static final Logger log = LoggerFactory.getLogger(Chessboard.class);

    private final LinkedList<ChessPiece> pieces;
    private Alignment playerOfTurn = Alignment.WHITE; // White always begins first
    private int whiteDead = 0;
    private int blackDead = 0;

    public Chessboard() {
        pieces = new LinkedList<>();

        // Todo: Instantiate chess pieces
        log.info("Started game\n" + getBoardStateString());
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

    void onTurnEnd() {
        playerOfTurn = playerOfTurn == Alignment.WHITE ? Alignment.BLACK : Alignment.WHITE;
    }

    public String getBoardStateString() {
        StringBuilder b = new StringBuilder();
        b.append("┌──┬────────┬──┐\n");
        Consumer<Optional<ChessPiece>> append = (opt) -> {
            b.append(opt.isPresent() ? opt.get().signatureCharacter() : " ");
        };
        for (int y = 7; y > -1; y--) {
            b.append("│");
            append.accept(getPieceAt(new TilePosition(-2, y)));
            append.accept(getPieceAt(new TilePosition(-1, y)));
            b.append("│");
            for (int i = 1; i <= 8; i++) {
                append.accept(getPieceAt(new TilePosition(i, y)));
            }
            b.append("│");
            append.accept(getPieceAt(new TilePosition(8, y)));
            append.accept(getPieceAt(new TilePosition(9, y)));
            b.append("│\n");
        }
        b.append("└──┴────────┴──┘");
        return b.toString();
    }

    public Alignment getPlayerOfTurn() {
        return playerOfTurn;
    }
}
