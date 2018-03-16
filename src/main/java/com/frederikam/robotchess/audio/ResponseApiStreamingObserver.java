package com.frederikam.robotchess.audio;

import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.common.util.concurrent.SettableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class ResponseApiStreamingObserver<T> implements ApiStreamObserver<T> {

    private static final Logger log = LoggerFactory.getLogger(ResponseApiStreamingObserver.class);

    private final SettableFuture<List<T>> future = SettableFuture.create();
    private final List<T> messages = new java.util.ArrayList<>();

    @Override
    public void onNext(T message) {
        log.info("onNext {}", message);
        messages.add(message);
    }

    @Override
    public void onError(Throwable t) {
        log.info("onError", t);
        future.setException(t);
    }

    @Override
    public void onCompleted() {
        log.info("onComplete");
        future.set(messages);
    }

    // Returns the SettableFuture object to get received messages / exceptions.
    public SettableFuture<List<T>> future() {
        return future;
    }
}

