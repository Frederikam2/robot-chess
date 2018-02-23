package com.frederikam.robotchess;

import com.frederikam.robotchess.chess.Chessboard;
import com.pi4j.io.gpio.GpioController;

public class Launcher {

    public static final GpioController gpio;

    static {
        gpio = null;
        //gpio = GpioFactory.getInstance();
    }

    public static void main(String[] args) throws Exception {
        new Chessboard();

        switch (args.length != 0 ? args[0] : "default") {
            case "voice-test":
                SpeechService.streamingRecognizeFile("test.wav");
                break;
        }
    }

}
