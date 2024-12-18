package com.github.dearrudam.java_studies_oop.session_01;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Predicate;

public class ProcessEvent implements Event {

    private final String command;
    private final Instant occurredOn;

    public ProcessEvent(String command) {
        this.command = Optional.ofNullable(command)
                .filter(Predicate.not(String::isBlank))
                .orElseThrow(()->new IllegalArgumentException("valid command is required"));
        this.occurredOn = Instant.now();
    }

    public String command() {
        return command;
    }

    public Instant occurredOn() {
        return occurredOn;
    }

    @Override
    public String toString() {
        return "ProcessEvent{" +
                "command='" + command + '\'' +
                ", occurredOn=" + occurredOn +
                '}';
    }
}
