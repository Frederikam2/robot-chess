package com.frederikam.mercury

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import org.json.JSONArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.InetSocketAddress

class Server : WebSocketServer(InetSocketAddress(12345)) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(Server::class.java)
    }

    init {
        isReuseAddr = true
    }

    private var speech: SpeechService? = null

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        log.info("Opened from {}", conn.remoteSocketAddress)
        if (speech != null) {
            conn.close(1000, "Already in use")
            return
        }

        var lang = handshake.getFieldValue("locale")
        var keywords = handshake.getFieldValue("keywords")

        if (lang.isEmpty()) lang = "da-DK"
        if (keywords.isEmpty()) keywords = "[]"

        @Suppress("UNCHECKED_CAST")
        speech = SpeechService(conn, lang, JSONArray(keywords).toList() as MutableList<String>)
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String?, remote: Boolean) {
        log.info("Opened from {}", conn.remoteSocketAddress)
        speech = null
    }

    override fun onMessage(conn: WebSocket?, message: String) {
        log.info("<-- {}", message)
        speech?.setListening(message == "true")
    }

    override fun onStart() {
        log.info("Started")
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        log.error("Error in WS", ex)
    }

}