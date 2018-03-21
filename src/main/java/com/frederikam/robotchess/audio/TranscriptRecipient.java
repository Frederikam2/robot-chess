package com.frederikam.robotchess.audio;

import com.frederikam.robotchess.chess.ChessControl;
import com.frederikam.robotchess.chess.ChessUtil;
import com.frederikam.robotchess.chess.Chessboard;
import com.frederikam.robotchess.chess.TilePosition;
import com.frederikam.robotchess.chess.pieces.ChessPiece;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.common.util.concurrent.SettableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

class TranscriptRecipient<T> implements ApiStreamObserver<T> {

    private static final Logger log = LoggerFactory.getLogger(TranscriptRecipient.class);

    private final SettableFuture<List<T>> future = SettableFuture.create();
    private final List<T> messages = new java.util.ArrayList<>();
    private final LinkedList<String> transcripts = new LinkedList<>();
    private final ChessControl chessControl;
    private final ChessLocale chessLocale;

    TranscriptRecipient(ChessControl chessControl, ChessLocale chessLocale) {
        this.chessControl = chessControl;
        this.chessLocale = chessLocale;
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

    private void parsePhrase(String s) {
        // TODO: Check for special words like "reset"

        String[] words = s.split(" ");
        LinkedList<Object> targets = new LinkedList<>();
        for (String word : words) {
            Optional<?> opt = parseWord(word);
            targets.add(opt);
        }

        // If we don't have just two targets, the phrase is ambiguous
        if (targets.size() != 2) return;

        Object from = targets.get(0);
        Object to = targets.get(1);
        Chessboard chessboard = chessControl.getChessboard();

        if (from instanceof TilePosition && to instanceof TilePosition) {
            chessControl.move((TilePosition) from, (TilePosition) to);
        } else if (from instanceof Class && to instanceof Class) {
            log.warn("Can't parse going from {} to {}", from, to);
        } else if (from instanceof Class) {
            // Eg "Pawn to A4"

            //noinspection unchecked
            List<ChessPiece> candidates = ChessUtil.getPiecesOfTypeWhichCanMoveTo(chessboard,
                    (Class<? extends ChessPiece>) from,
                    (TilePosition) to);

            if (candidates.size() == 1) {
                //noinspection ConstantConditions
                chessControl.move(candidates.get(0).getPosition(), (TilePosition) to);
            } else {
                log.warn("From {} to {} is ambiguous", from, to);
            }
        } else if (to instanceof Class) {
            // Eg "A1 to rook"
            //noinspection ConstantConditions,unchecked
            List<ChessPiece> toMoveTo = ChessUtil.getTargetsOfTypeWhichWeCanMoveTo(chessboard,
                    (Class<? extends ChessPiece>) to,
                    (TilePosition) from);

            if (toMoveTo.size() == 1) {
                //noinspection ConstantConditions
                chessControl.move((TilePosition) from, toMoveTo.get(0).getPosition());
            } else {
                log.warn("From {} to {} is ambiguous", from, to);
            }
        } else {
            throw new RuntimeException("This shouldn't happen: " + to + " " + from);
        }
    }

    private Optional<?> parseWord(String s) {
        if (TilePosition.isValidTile(s)) {
            return Optional.of(new TilePosition(s));
        }

        return ChessLocale.interpretChessPiece(s);
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

