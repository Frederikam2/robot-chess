package com.frederikam.robotchess.audio;

import com.frederikam.robotchess.chess.ChessControl;
import com.frederikam.robotchess.chess.TilePosition;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SpeechService implements LineListener {

    private static final Logger log = LoggerFactory.getLogger(SpeechService.class);
    private volatile boolean listening = false;
    private final ChessLocale locale;

    public void setListening(boolean listening) {
        if (!this.listening && listening) {
            // We are starting
            startListening();
        } else if (this.listening && !listening) {
            // We are stopping
            onCompleted();
        }
        this.listening = listening;
    }

    public boolean isListening() {
        return listening;
    }

    private List<String> getKeywords() {
        LinkedList<String> list = new LinkedList<>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                list.add(new TilePosition(x, y).toTileNotation());
            }
        }

        list.addAll(locale.getKeywords());

        return list;
    }
}
