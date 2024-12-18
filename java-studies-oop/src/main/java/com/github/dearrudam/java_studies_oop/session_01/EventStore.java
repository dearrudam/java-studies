package com.github.dearrudam.java_studies_oop.session_01;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class EventStore {

    private List events = new LinkedList();

    public void store(Event event) {
        this.events.add(Objects.requireNonNull(event,"event is required"));
    }

    public List listAll() {
        // returning a copy of the events list to avoid external modifications
        return new LinkedList(events);
    }

}
