package com.frederikam.robotchess;

import com.frederikam.robotchess.chess.ChessControl;
import com.frederikam.robotchess.chess.Chessboard;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.system.SystemInfo;

public class Launcher {

    public static final GpioController gpio;

    static {
        gpio = SystemInfo.getOsArch().equals("amd64") ? null : GpioFactory.getInstance();
    }

    public static void main(String[] args) throws Exception {
        Chessboard chessboard = new Chessboard();
        ChessControl chessControl = new ChessControl(chessboard);
        new CliInputManager(chessControl, System.in).start();
        /*
        switch (args.length != 0 ? args[0] : "default") {
            case "voice-test":
                SpeechService.streamingRecognizeFile("test.wav");
                break;
        }*/
    }

}
