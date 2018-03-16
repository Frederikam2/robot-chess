package com.frederikam.robotchess.audio;

import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
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
    private final static AudioFormat CAPTURE_FORMAT = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
    private final static RecognitionConfig.AudioEncoding UPLOAD_FORMAT = RecognitionConfig.AudioEncoding.LINEAR16;

    private final AudioInputManager inputManager;
    private volatile ResponseApiStreamingObserver<StreamingRecognizeResponse> responseObserver;
    private volatile ApiStreamObserver<StreamingRecognizeRequest> requestObserver;
    private volatile boolean listening = false;
    private SpeechClient speech;
    private volatile TargetDataLine line;
    private ScheduledExecutorService readerService;

    public SpeechService() {
        this.inputManager = new AudioInputManager();

        // Instantiates a client with GOOGLE_APPLICATION_CREDENTIALS
        try {
            speech = SpeechClient.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

    private void startListening() {
        // Open a line
        line = inputManager.getInputLine();

        try {
            line.open(CAPTURE_FORMAT);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

        RecognitionConfig recConfig = RecognitionConfig.newBuilder()
                .setEncoding(UPLOAD_FORMAT)
                .setLanguageCode("en-US") // TODO
                .setSampleRateHertz(SAMPLE_RATE)
                .build();
        StreamingRecognitionConfig config = StreamingRecognitionConfig.newBuilder()
                .setConfig(recConfig)
                .setInterimResults(true)
                .build();

        responseObserver = new ResponseApiStreamingObserver<>();

        BidiStreamingCallable<StreamingRecognizeRequest,StreamingRecognizeResponse> callable =
                speech.streamingRecognizeCallable();

        requestObserver = callable.bidiStreamingCall(responseObserver);

        // The first request must **only** contain the audio configuration:
        requestObserver.onNext(StreamingRecognizeRequest.newBuilder()
                .setStreamingConfig(config)
                .build());

        // Begin actually sending
        readerService = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "audio-reader-thread"));
        readerService.scheduleAtFixedRate(this::sendAudio, 0, 4000, TimeUnit.MILLISECONDS);
    }

    private void onCompleted() {
        log.info("Completed sending audio");
        readerService.shutdown();
        line.close();

        requestObserver.onCompleted();
        List<StreamingRecognizeResponse> responses;
        try {
            responses = responseObserver.future().get();
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

            int avail = line.available();
            log.info("Sending {} bytes", avail);
            byte[] data = new byte[avail];
            line.read(data, 0, avail);
            requestObserver.onNext(StreamingRecognizeRequest.newBuilder()
                    .setAudioContent(ByteString.copyFrom(data))
                    .build());
        } catch (RuntimeException e) {
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
