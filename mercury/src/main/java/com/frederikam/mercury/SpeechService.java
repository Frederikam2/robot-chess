package com.frederikam.mercury;

import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SpeechService implements LineListener {

    private static final Logger log = LoggerFactory.getLogger(SpeechService.class);
        private static final int SAMPLE_RATE = 16000;
    private final static AudioFormat CAPTURE_FORMAT = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
    private final static RecognitionConfig.AudioEncoding UPLOAD_FORMAT = RecognitionConfig.AudioEncoding.LINEAR16;

    private final AudioInputManager inputManager;
    private volatile TranscriptRecipient responseObserver;
    private volatile ApiStreamObserver<StreamingRecognizeRequest> requestObserver;
    private volatile boolean listening = false;
    private SpeechClient speech;
    private volatile TargetDataLine line;
    private ScheduledExecutorService readerService;
    private volatile AudioInputStream audioInputStream = null;
    private final WebSocket socket;
    private final StreamingRecognitionConfig streamingConfig;

    public SpeechService(WebSocket socket, String language, List<String> keywords) {
        this.socket = socket;
        this.inputManager = new AudioInputManager();

        // Instantiates a client with GOOGLE_APPLICATION_CREDENTIALS
        try {
            speech = SpeechClient.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SpeechContext speechContext = SpeechContext.newBuilder().addAllPhrases(keywords).build();

        RecognitionConfig recConfig = RecognitionConfig.newBuilder()
                .setEncoding(UPLOAD_FORMAT)
                .setLanguageCode(language)
                .setSampleRateHertz(SAMPLE_RATE)
                .addSpeechContexts(speechContext)
                .build();
        streamingConfig = StreamingRecognitionConfig.newBuilder()
                .setConfig(recConfig)
                .setInterimResults(true)
                .build();
    }

    void setListening(boolean listening) {
        if (!this.listening && listening) {
            // We are starting
            startListening();
        } else if (this.listening && !listening) {
            // We are stopping
            onCompleted();
        }
        this.listening = listening;
    }

    private void startListening() {
        // Open a line
        line = inputManager.getInputLine();

        try {
            line.open(CAPTURE_FORMAT);
            line.start();
            audioInputStream = new AudioInputStream(line);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

        responseObserver = new TranscriptRecipient(socket::send);

        BidiStreamingCallable<StreamingRecognizeRequest,StreamingRecognizeResponse> callable =
                speech.streamingRecognizeCallable();

        requestObserver = callable.bidiStreamingCall(responseObserver);

        // The first request must **only** contain the audio configuration:
        requestObserver.onNext(StreamingRecognizeRequest.newBuilder()
                .setStreamingConfig(streamingConfig)
                .build());

        // Begin actually sending
        readerService = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "audio-reader-thread"));
        readerService.scheduleAtFixedRate(this::sendAudio, 500, 500, TimeUnit.MILLISECONDS);
    }

    private void onCompleted() {
        log.info("Completed sending audio");
        readerService.shutdown();
        line.close();
        line.stop();
        try {
            audioInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        line = null;

        requestObserver.onCompleted();
        List<StreamingRecognizeResponse> responses;
        try {
            responses = responseObserver.getFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        for (StreamingRecognizeResponse response : responses) {
            for (StreamingRecognitionResult result : response.getResultsList()) {
                result.getAlternativesList().forEach((alt) -> log.info("Transcript: " + alt.getTranscript()));
            }
        }
    }

    private void sendAudio() {
        try {
            int avail = audioInputStream.available();
            byte[] data = new byte[avail];
            //noinspection ResultOfMethodCallIgnored
            audioInputStream.read(data, 0, avail);

            requestObserver.onNext(StreamingRecognizeRequest.newBuilder()
                    .setAudioContent(ByteString.copyFrom(data))
                    .build());
        } catch (RuntimeException | IOException e) {
            log.error("Exception while sending audio", e);
        }
    }

    @Override
    public void update(LineEvent event) {
        log.info("Line status " + event);

        switch (event.getType().toString()) {
            case "Close":
                setListening(false);
                log.info("Audio line closed");
                break;
        }
    }
}
