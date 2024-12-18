package com.github.dearrudam.java_studies_oop.generics_old;

import com.github.dearrudam.java_studies_oop.session_01.Event;
import com.github.dearrudam.java_studies_oop.session_01.ProcessEvent;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProcessEventTest implements EventTest {

    @Override
    public Event createNewValidEvent() {
        return new ProcessEvent("any command --" + UUID.randomUUID().toString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldErrorGivenInvalidCommand(String command) {
        assertThatThrownBy(() -> new ProcessEvent(command))
                .as("cannot accept to create an event with invalid command as argument")
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("valid command is required");
    }

}
