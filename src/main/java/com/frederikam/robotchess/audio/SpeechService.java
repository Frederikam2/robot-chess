package com.frederikam.robotchess.audio;

import com.frederikam.robotchess.chess.ChessControl;
import com.frederikam.robotchess.chess.TilePosition;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SpeechService extends WebSocketClient {

    private static final Logger log = LoggerFactory.getLogger(SpeechService.class);
    private volatile boolean listening = false;
    private final ChessLocale locale;
    private final TranscriptRecipient recipient;

    public SpeechService(ChessControl control, ChessLocale locale, HashMap<String, String> headers) {
        super(URI.create("ws://10.144.96.158:12345"), headers);

        this.locale = locale;
        recipient = new TranscriptRecipient(control);
    }

    public static HashMap<String, String> generateHeaders(ChessLocale locale) {
        HashMap<String, String> map = new HashMap<>();
        map.put("locale", locale.voiceLocale());
        List<String> list = getKeywords(locale);
        map.put("keywords", new JSONArray(list).toString());
        return map;
    }

    private static List<String> getKeywords(ChessLocale locale) {
        LinkedList<String> list = new LinkedList<>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                list.add(new TilePosition(x, y).toTileNotation());
            }
        }

        list.addAll(locale.getKeywords());

        return list;
    }

    public void setListening(boolean listening) {
        this.listening = listening;
        send(Boolean.toString(listening));
    }

    public boolean isListening() {
        return listening;
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        log.info("Connected");
    }

    @Override
    public void onMessage(String message) {
        recipient.parsePhrase(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.warn("WS closed. Code: ", code);
    }

    @Override
    public void onError(Exception ex) {
        log.error("Error in WS", ex);
    }
}
