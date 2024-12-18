package com.github.dearrudam.java_studies_oop.session_01;

import java.util.List;

public class StoringAndRetrieveEvents {

    public static void main(String[] args) {

        EventStore eventStore = new EventStore();

        eventStore.store(new ProcessEvent("command A"));
        eventStore.store(new ProcessEvent("command B"));
        eventStore.store(new ProcessEvent("command C"));

        List list = eventStore.listAll();

        System.out.println("All events:" + list.size());
        list.forEach(System.out::println);

    }
}
