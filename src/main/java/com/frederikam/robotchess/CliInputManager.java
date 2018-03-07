package com.frederikam.robotchess;

import com.frederikam.robotchess.chess.ChessControl;
import com.frederikam.robotchess.chess.TilePosition;
import com.frederikam.robotchess.chess.pieces.ChessPiece;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;

public class CliInputManager extends Thread {

    private static final Logger log = LoggerFactory.getLogger(CliInputManager.class);
    private final ChessControl chessControl;
    private final InputStream is;

    CliInputManager(ChessControl chessControl, InputStream stream) {
        this.chessControl = chessControl;
        this.is = stream;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(is);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().toUpperCase();
            if (line.length() != 4) continue;

            TilePosition from = new TilePosition(line.substring(0, 2));
            TilePosition to   = new TilePosition(line.substring(2, 4));
            Optional<ChessPiece> piece = chessControl.getChessboard().getPieceAt(from);

            boolean valid = chessControl.move(from, to);
            if (valid) {
                //noinspection ConstantConditions
                log.info("Moved {} from {} to {}",
                        piece.get().getClass().getSimpleName(),
                        line.substring(0, 2),
                        line.substring(2, 4));
                log.info("\n" + chessControl.getChessboard().getBoardStateString());
                chessControl.getChessboard().onTurnEnd();
            } else {
                log.info("Invalid move");
            }
        }
    }
}
