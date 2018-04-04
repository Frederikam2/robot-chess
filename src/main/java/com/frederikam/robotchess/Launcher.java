package com.frederikam.robotchess;

import com.frederikam.robotchess.audio.ChessLocale;
import com.frederikam.robotchess.audio.SpeechService;
import com.frederikam.robotchess.chess.ChessControl;
import com.frederikam.robotchess.chess.Chessboard;
import com.frederikam.robotchess.chess.TilePosition;
import com.frederikam.robotchess.mech.VoiceButtonHandler;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiPin;
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
        SpeechService speechService = new SpeechService(chessControl, new ChessLocale.Danish());
        new CliInputManager(chessControl, speechService, System.in).start();

        if (gpio != null) {
            new VoiceButtonHandler(RaspiPin.GPIO_11, speechService);
        }

        //noinspection ConstantConditions
        if (true) {
            // Run motor test
            chessControl.getMechanicalControl().reset();
            chessControl.move(new TilePosition("A1"), new TilePosition("A3"));
            chessControl.move(new TilePosition("B8"), new TilePosition("A1"));
            chessControl.move(new TilePosition("A3"), new TilePosition("B8"));
            chessControl.resetBoard();
            chessControl.getMechanicalControl().reset();
        }
    }

}
