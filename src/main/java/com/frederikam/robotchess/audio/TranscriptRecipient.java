package com.frederikam.robotchess.audio;

import com.frederikam.robotchess.chess.ChessControl;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.common.util.concurrent.SettableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

class TranscriptRecipient<T> implements ApiStreamObserver<T> {

    private static final Logger log = LoggerFactory.getLogger(TranscriptRecipient.class);

    private final SettableFuture<List<T>> future = SettableFuture.create();
    private final List<T> messages = new java.util.ArrayList<>();
    private final LinkedList<String> transcripts = new LinkedList<>();
    private final ChessControl chessControl;

    TranscriptRecipient(ChessControl chessControl) {
        this.chessControl = chessControl;
    }

    @Override
    public void onNext(T m) {
        messages.add(m);
        StreamingRecognizeResponse message = (StreamingRecognizeResponse) m;

        for (StreamingRecognitionResult res : message.getResultsList()) {
            for (SpeechRecognitionAlternative alt : res.getAlternativesList()) {
                String transcript = alt.getTranscript();
                if (transcripts.contains(transcript)) continue; // Already attempted
                log.info("Voice: " + transcript);
                transcripts.add(transcript);
                try {
                    chessControl.processCommand(transcript);
                } catch (RuntimeException e) {
                    log.error("Error processing command", e);
                }
            }
        }
    }

    @Override
    public void onError(Throwable t) {
        log.info("Speech API error", t);
        future.setException(t);
    }

    @Override
    public void onCompleted() {
        future.set(messages);
    }

    // Returns the SettableFuture object to get received messages / exceptions.
    public SettableFuture<List<T>> future() {
        return future;
    }
}

