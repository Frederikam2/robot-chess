package com.frederikam.robotchess;

import com.frederikam.robotchess.audio.ChessLocale;
import com.frederikam.robotchess.audio.SpeechService;
import com.frederikam.robotchess.chess.ChessControl;
import com.frederikam.robotchess.chess.Chessboard;
import com.frederikam.robotchess.mech.VoiceButtonHandler;
import com.frederikam.robotchess.util.CliInputManager;
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

    public static void main(String[] args) throws InterruptedException {
        Chessboard chessboard = new Chessboard();
        chessboard.populate();
        log.info("Started game\n" + chessboard.getBoardStateString());

        ChessControl chessControl = new ChessControl(chessboard);
        ChessLocale loc = new  ChessLocale.Danish();
        SpeechService speechService = new SpeechService(chessControl, SpeechService.generateHeaders(loc));
        new CliInputManager(chessControl, speechService, System.in).start();

        if (gpio != null) {
            new VoiceButtonHandler(RaspiPin.GPIO_11, speechService);
        }

        chessControl.getMechanicalControl().reset();
        chessControl.getWorkspace().setMagnetEnabled(false);
        speechService.connectBlocking();

        /*
        chessControl.processCommand("force 1600 800");
        chessControl.processCommand("force 1600 1600");
        chessControl.processCommand("force 2000 1600");
        chessControl.processCommand("force 0 0");*/

        //noinspection ConstantConditions
        /*if (true) {
            // Run motor test
            chessControl.getMechanicalControl().reset();
            chessControl.move(new TilePosition("A1"), new TilePosition("A3"));
            chessControl.move(new TilePosition("B8"), new TilePosition("A1"));
            chessControl.move(new TilePosition("A3"), new TilePosition("B8"));
            chessControl.resetBoard();
            chessControl.getMechanicalControl().reset();
        }*/
    }

}
