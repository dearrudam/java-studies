package com.github.dearrudam.java_studies_oop.generics_old;

import com.github.dearrudam.java_studies_oop.session_01.Event;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

interface EventTest {

    Event createNewValidEvent();

    @Test
    default void conformingTests() {

        assertSoftly(softly -> {

            Event validEvent = createNewValidEvent();

            assertThat(validEvent)
                    .as("createNewValidEvent() should returns a valid non null Event instance")
                    .isNotNull();

            softly.assertThat(validEvent.occurredOn())
                    .isInstanceOf(Instant.class)
                    .as("event instance should provide a non-null occurred instant time")
                    .isNotNull();

        });

    }

}