package com.github.dearrudam.java_studies_oop.generics_old;

import com.github.dearrudam.java_studies_oop.session_01.Event;
import com.github.dearrudam.java_studies_oop.session_01.MessageEvent;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MessageEventTest implements EventTest {

    @Override
    public Event createNewValidEvent() {
        return new MessageEvent(UUID.randomUUID().toString());
    }

    @ParameterizedTest
    @NullSource
    void shouldErrorGivenInvalidMessage(String message) {
        assertThatThrownBy(() -> new MessageEvent(message))
                .as("cannot accept to create an event with invalid message as argument")
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("message is required");
    }

}
