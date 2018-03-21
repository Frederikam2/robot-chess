package com.frederikam.robotchess;

import com.frederikam.robotchess.audio.ChessLocale;
import com.frederikam.robotchess.audio.SpeechService;
import com.frederikam.robotchess.chess.ChessControl;
import com.frederikam.robotchess.chess.Chessboard;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.system.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {

    private static final Logger log = LoggerFactory.getLogger(Launcher.class);
    public static final GpioController gpio;

    static {
        gpio = SystemInfo.getOsArch().equals("amd64") ? null : GpioFactory.getInstance();
    }

    public static void main(String[] args) {
        Chessboard chessboard = new Chessboard();
        chessboard.populate();
        log.info("Started game\n" + chessboard.getBoardStateString());

        ChessControl chessControl = new ChessControl(chessboard);
        SpeechService speechService = new SpeechService(chessControl, new ChessLocale.English());
        new CliInputManager(chessControl, speechService, System.in).start();
    }

}
