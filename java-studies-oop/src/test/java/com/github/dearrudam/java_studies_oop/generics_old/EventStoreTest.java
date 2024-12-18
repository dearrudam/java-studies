package com.github.dearrudam.java_studies_oop.generics_old;

import com.github.dearrudam.java_studies_oop.session_01.EventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EventStoreTest {

    EventStore eventStore;

    @BeforeEach
    void setup() {
        eventStore = new EventStore();
    }

    @Test
    void shouldStoreAndRetrieveEvents() {

        var expectedEventList = List.of(
                new ProcessEventTest().createNewValidEvent(),
                new MessageEventTest().createNewValidEvent()
        );

        expectedEventList.forEach(eventStore::store);

        assertSoftly(softly -> {

            softly.assertThatCode(()->eventStore.store(null))
                    .as("store() should not accept null event")
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("event is required");

            softly.assertThat(eventStore.listAll())
                    .as("listAll() result cannot be null")
                    .isNotNull()
                    .as("listAll() should contains the expected stored events")
                    .containsAll(expectedEventList);

        });

        // You could implement here more conforming tests for all event tests

    }

}