package com.frederikam.robotchess;

public class Launcher {

    public static void main(String[] args) throws Exception {
        switch (args.length != 0 ? args[0] : "default") {
            case "voice-test":
                SpeechService.streamingRecognizeFile("test.wav");
                break;
        }
    }

}
