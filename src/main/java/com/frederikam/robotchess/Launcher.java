package com.frederikam.robotchess;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

public class Launcher {

    public static final GpioController gpio = GpioFactory.getInstance();

    public static void main(String[] args) throws Exception {
        switch (args.length != 0 ? args[0] : "default") {
            case "voice-test":
                SpeechService.streamingRecognizeFile("test.wav");
                break;
        }
    }

}
