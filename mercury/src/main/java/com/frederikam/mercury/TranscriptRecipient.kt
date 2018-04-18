package com.frederikam.mercury

import com.google.api.gax.rpc.ApiStreamObserver
import com.google.cloud.speech.v1.StreamingRecognizeResponse
import com.google.common.util.concurrent.SettableFuture
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer

class TranscriptRecipient(private val callback: Consumer<String>) : ApiStreamObserver<StreamingRecognizeResponse> {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(TranscriptRecipient::class.java)
    }

    val future = SettableFuture.create<List<StreamingRecognizeResponse>>()!!
    private val messages = mutableListOf<StreamingRecognizeResponse>()
    private val transcripts = mutableListOf<String>()

    override fun onNext(message: StreamingRecognizeResponse) {
        messages.add(message)

        for (res in message.resultsList) {
            for (alt in res.alternativesList) {
                val transcript = alt.transcript
                if (transcripts.contains(transcript)) continue // Already attempted
                log.info("Voice: $transcript")
                transcripts.add(transcript)
                callback.accept(transcript)
            }
        }
    }

    override fun onError(t: Throwable) {
        log.info("Speech API error", t)
        future.setException(t)
    }

    override fun onCompleted() {
        future.set(messages)
    }
}