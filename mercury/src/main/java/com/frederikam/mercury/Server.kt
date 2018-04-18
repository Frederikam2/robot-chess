package com.frederikam.mercury

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.InetSocketAddress

class Server : WebSocketServer(InetSocketAddress(12345)) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(Server::class.java)
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake?) {
        log.info("Opened from {}", conn.remoteSocketAddress)
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String?, remote: Boolean) {
        log.info("Opened from {}", conn.remoteSocketAddress)
    }

    override fun onMessage(conn: WebSocket?, message: String) {
        log.info("<-- {}", message)
    }

    override fun onStart() {
        log.info("Started")
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        log.error("Error in WS", ex)
    }

}