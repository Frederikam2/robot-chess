package com.frederikam.robotchess.util;

import com.frederikam.robotchess.audio.SpeechService;
import com.frederikam.robotchess.chess.ChessControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Scanner;

public class CliInputManager extends Thread {

    private static final Logger log = LoggerFactory.getLogger(CliInputManager.class);
    private final ChessControl chessControl;
    private final SpeechService speechService;
    private final InputStream is;

    public CliInputManager(ChessControl chessControl, SpeechService speechService, InputStream stream) {
        this.chessControl = chessControl;
        this.speechService = speechService;
        this.is = stream;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(is);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().toUpperCase();

            // Toggle listening for commands
            if (line.equals("L")) {
                speechService.setListening(!speechService.isListening());
                log.info(speechService.isListening() ? "Started listening" : "Stopped listening");
                continue;
            }

            chessControl.processCommand(line);
        }
    }
}
