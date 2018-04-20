package com.frederikam.robotchess.chess;

import com.frederikam.robotchess.chess.pieces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
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
    }

    public void populate() {
        for (int i = 0; i <= 7; i++) {
            pieces.add(new Pawn(this, Alignment.WHITE, new TilePosition(i, 1)));
            pieces.add(new Pawn(this, Alignment.BLACK, new TilePosition(i, 6)));
        }

        pieces.add(new Rook(this, Alignment.WHITE, new TilePosition(0, 0)));
        pieces.add(new Knight(this, Alignment.WHITE, new TilePosition(1, 0)));
        pieces.add(new Bishop(this, Alignment.WHITE, new TilePosition(2, 0)));
        pieces.add(new Queen(this, Alignment.WHITE, new TilePosition(3, 0)));
        pieces.add(new King(this, Alignment.WHITE, new TilePosition(4, 0)));
        pieces.add(new Bishop(this, Alignment.WHITE, new TilePosition(5, 0)));
        pieces.add(new Knight(this, Alignment.WHITE, new TilePosition(6, 0)));
        pieces.add(new Rook(this, Alignment.WHITE, new TilePosition(7, 0)));

        pieces.add(new Rook(this, Alignment.BLACK, new TilePosition(0, 7)));
        pieces.add(new Knight(this, Alignment.BLACK, new TilePosition(1, 7)));
        pieces.add(new Bishop(this, Alignment.BLACK, new TilePosition(2, 7)));
        pieces.add(new Queen(this, Alignment.BLACK, new TilePosition(3, 7)));
        pieces.add(new King(this, Alignment.BLACK, new TilePosition(4, 7)));
        pieces.add(new Bishop(this, Alignment.BLACK, new TilePosition(5, 7)));
        pieces.add(new Knight(this, Alignment.BLACK, new TilePosition(6, 7)));
        pieces.add(new Rook(this, Alignment.BLACK, new TilePosition(7, 7)));
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

    public void onTurnEnd() {
        playerOfTurn = playerOfTurn == Alignment.WHITE ? Alignment.BLACK : Alignment.WHITE;
        log.info("\n" + getBoardStateString());
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
            for (int i = 0; i <= 7; i++) {
                append.accept(getPieceAt(new TilePosition(i, y)));
            }
            b.append("│");
            append.accept(getPieceAt(new TilePosition(8, y)));
            append.accept(getPieceAt(new TilePosition(9, y)));
            b.append("│\n");
        }
        b.append("└──┴────────┴──┘\n");
        b.append("Current turn: " + playerOfTurn);
        return b.toString();
    }

    public void put(ChessPiece chessPiece) {
        if(getPieceAt(chessPiece.getPosition()).isPresent())
            throw new IllegalArgumentException("There is already a piece at this position");

        pieces.add(chessPiece);
    }

    public Alignment getPlayerOfTurn() {
        return playerOfTurn;
    }

    public List<ChessPiece> getPieces() {
        return pieces;
    }
}
