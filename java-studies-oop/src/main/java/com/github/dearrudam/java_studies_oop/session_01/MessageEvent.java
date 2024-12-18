package com.github.dearrudam.java_studies_oop.session_01;

import java.time.Instant;
import java.util.Optional;

public record MessageEvent(String message, Instant occurredOn) implements Event {

    public MessageEvent(String message) {
        this(message, Instant.now());
    }

    public MessageEvent {
        message = Optional.ofNullable(message)
                .orElseThrow(() -> new IllegalArgumentException("message is required"));
        occurredOn = Optional.ofNullable(occurredOn).orElseGet(Instant::now);
    }

}
