# Generics 

Since Java 5, Java has introduced Generics but, what does it bring to the table? Do we really need it? Do we really need to use it? What are the benefits of using it?

## Do we really need to work with Generics?

Let's bring the conversation to a hypothetical and practical scenario: let's suppose that we need to create an event store application. The initial application requirements are:

- Each event should be composed with description and its occurred instant time;
- The application should allow users to store and retrieve events for future analytics or processing;

Great! Here it's a good point to use TDD to help us to design our codes:

Lets getting started by `Event` unit tests:

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EventTest {

    @Test
    @DisplayName("should create valid event")
    void shouldCreateValidEvent() {

        assertSoftly(softly -> {

            softly.assertThatThrownBy(() -> new Event(null))
                    .as("should throw illegal argument exception for nullable descriptions")
                    .isInstanceOf(IllegalArgumentException.class);


            softly.assertThatThrownBy(() -> new Event(""))
                    .as("should throw illegal argument exception for empty descriptions")
                    .isInstanceOf(IllegalArgumentException.class);


            softly.assertThatThrownBy(() -> new Event(" "))
                    .as("should throw illegal argument exception for blank descriptions")
                    .isInstanceOf(IllegalArgumentException.class);

            String expectedDescription = "any description";

            var event = new Event(expectedDescription);

            softly.assertThat(event.description())
                    .isEqualTo(expectedDescription);

            softly.assertThat(event.occurredOn())
                    .isInstanceOf(Instant.class)
                    .isNotNull();
        });

    }

    // Implement here other scenarios as needed

}

```

And here is a possible `Event` implementation:

```java
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class Event {

    private final String description;
    private final Instant occurredOn;

    public Event(String description) {
        this.description = Optional.ofNullable(description)
                .filter(Objects::nonNull)
                .filter(Predicate.not(String::isBlank))
                .orElseThrow(() -> new IllegalArgumentException("description is required"));
        this.occurredOn = Instant.now();
    }

    public String description() {
        return description;
    }

    public Instant occurredOn() {
        return occurredOn;
    }
}

```

By now, it looks good enough. Lets implements the `EventStore` tests:

```java

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EventStoreTest {

    EventStore eventStore;

    @BeforeEach
    void setup() {
        eventStore = new EventStore();
    }

    @Test
    void shouldStoreAndRetrieveEvents() {

        // creating events for the process A
        Event first_process_a_event = new Event("first process A event");
        Event second_process_a_event = new Event("second process A event");
        Event third_process_a_event = new Event("third process A event");

        // storing the events
        eventStore.store(first_process_a_event);
        eventStore.store(second_process_b_event);
        eventStore.store(third_process_c_event);

        assertSoftly(softly -> {

            softly.assertThat(eventStore.listAll())
                    .as("listAll() result cannot be null")
                    .isNotNull()
                    .as("listAll() should contains the expected stored events")
                    .containsExactly(
                            first_process_a_event,
                            second_process_a_event,
                            third_process_a_event);
        });

    }

}
```
And then lets getting starting to implement our `EventStore` class:

First, let's implement the `EventStore` class without the query capabilities:

```java
import java.util.ArrayList;
import java.util.List;

public class EventStore {

    private List events = new ArrayList();

    public void store(Event event) {
        this.events.add(event);
    }

    public List listAll() {
        return events;
    }
}
```
For some developers this solution could look good. Some developers can argument with these assertions:

- The project compilation is working fine and the packaging process is working as expected and...
- All the tests have passed successfully!

Okay, lets assume these assertions and keep it like it is.

And then, suddenly, new requirement requests come and we should be okay with that, after all, it's normal to happen that thing. Software keeps to evolve and, software is something that intents to be flexible, don't you?

- Now, the `Event` should be distinguished by two new kind of events: `ProcessEvent` and `MessageEvent` and the `EventStore` should store then too;
- `ProcessEvent` is composed by the command line and its occurred instant time attributes;
- `MessageEvent` is composed by the message and its occurred instant time attributes;
- The application should allow users to get the list the of `MessageEvent` and `ProcessEvent`;

In this point, in a normal situation, probably we'll need more clarification about these new requirements. Some of them would be like:

- Is event a kind of concept that represents each event or such one represents a generic event as well? 
- Would the application handle with other kind of events beyond than `ProcessEvent` and `MessageEvent` ?
- And so on... 

But, lets assume the following answers for these questions:

- `Event` is just a kind of concept that represents each event;
- No longer the events needs to have a description attribute;
- The application will handle only with `ProcessEvent` and `MessageEvent` events;

With that, lets start to implement the new requirements.

First, lets refactor the `EventTest` to become a kind of specification that will be used as a required unit test base for next event types, like `ProcessEventTest` and `MessageEventTest` classes:

```java
package com.github.dearrudam.java_studies_oop.generics_old;

import com.github.dearrudam.java_studies_oop.session_01.Event;
import org.junit.jupiter.api.DisplayName;
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

            // You could implement here more conforming tests for all event tests

        });

    }
}

```
And, lets refactor the `Event` class:

```java
package com.github.dearrudam.java_studies_oop.generics_old;

import java.time.Instant;

public interface Event {

    public Instant occurredOn();

}

```
Now, lets implement the `ProcessEventTest`

```java
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
```
And then, lets implements a valid `ProcessEvent` class:

```java
package com.github.dearrudam.java_studies_oop.generics_old;

import com.github.dearrudam.java_studies_oop.session_01.Event;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Predicate;

public class ProcessEvent implements Event {

    private final String command;
    private final Instant occurredOn;

    public ProcessEvent(String command) {
        this.command = Optional.ofNullable(command)
                .filter(Predicate.not(String::isBlank))
                .orElseThrow(() -> new IllegalArgumentException("valid command is required"));
        this.occurredOn = Instant.now();
    }

    public String command() {
        return command;
    }

    public Instant occurredOn() {
        return occurredOn;
    }

    /**
     * for convenience
     */
    @Override
    public String toString() {
        return "ProcessEvent{" +
                "command='" + command + '\'' +
                ", occurredOn=" + occurredOn +
                '}';
    }
}
```

Thanks to the architectural decision to create the `EventTest` as the conforming tests for all events we're able to create new event types and their tests easily:

```java
package com.github.dearrudam.java_studies_oop.generics_old;

import com.github.dearrudam.java_studies_oop.session_01.Event;
import com.github.dearrudam.java_studies_oop.session_01.MessageEvent;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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
```
For the `MessageEvent` class, lets implement it as a Java Record:

```java
package com.github.dearrudam.java_studies_oop.generics_old;

import com.github.dearrudam.java_studies_oop.session_01.Event;

import java.time.Instant;
import java.util.Optional;

public record MessageEvent(String message, Instant occurredOn) implements Event {

    static MessageEvent(String message) {
        this(message, Instant.now());
    }

    public MessageEvent {
        message = Optional.ofNullable(message)
                .orElseThrow(() -> new IllegalArgumentException("message is required"));
        occurredOn = Optional.ofNullable(occurredOn).orElseGet(Instant::now);
    }

    // Java Records provides a convenient toString() method by default
}
```
Now, lets refactor the `EventStoreTest` to fits with the new changes:

```java 
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

            softly.assertThat(eventStore.listAll())
                    .as("listAll() result cannot be null")
                    .isNotNull()
                    .as("listAll() should contains the expected stored events")
                    .containsAll(expectedEventList);
        });

        // You could implement here more conforming tests for all event tests

    }

}
```

That's it! All tests have passed again! And we didn't need to use generics yet. Well, should we use generics? 


Let's suppose there is a class a like below:

```java

    

```


have a need to store some `ProcessEvent` and `MessageEvent` in the `EventStore` and then I need to get the list of `ProcessEvent` and `MessageEvent` separately. How can we do that?


```java
    public List<ProcessEvent> listAllProcessEvents() {
        List<ProcessEvent> processEvents = new ArrayList<>();
        for (Event event : events) {
            if (event instanceof ProcessEvent) {
                processEvents.add((ProcessEvent) event);
            }
        }
        return processEvents;
    }

```java

```


Now, let's implement the `EventStore` class with the query capabilities!

First, we can figure out that we need to iterate over the stored events and filter the events that match the given text sequence. Let's implement that loop in the `findByDescriptionContains` method:

```java

    public List findByDescriptionContains(String text) {
        List result = new ArrayList();
        for (Object event : events) {
            // Great! lets continue here ...
        }
        return result;
    }
    
```

Now, once we're able to iterate over the event through the list so it's needed to check if the event's description contains the given text sequence, but where is the `Event.getDescription()` method?

```java
    public List findByDescriptionContains(String text) {
        List result = new ArrayList();
        for (Object event : events) {
            // Oh no! it's not possible to do that because the `Event` class doesn't expose the description attribute.
        }
        return result;
    }
```

Why it's not possible to do that? And how can we solve this problem?

Before Java 5, we would have to use the `instanceof` operator to check if the object is an instance of the `Event` class and then cast it to the `Event` class to access the `description` attribute. Take a look below:

```java
    public List findByDescriptionContains(String text) {
        List result = new ArrayList();
        for (Object event : events) {
            //1. First, we need to check if the object is an instance of the `Event` class;
            if(event instanceof Event) {
                //2. Then, we need to cast the object to the `Event` class;
                Event e = (Event) event;
                //3. Finally, we can access the `description` attribute.
                if(e.description().contains(text)) {
                    result.add(e);
                }
            }
        }
        return result;
    }
```




Of course, many of us have already used the `instanceof` operator to check the type of a given object and cast it to the desired type. 




The question that we need to do to ourselves is: is it scalable? Is it maintainable? Is it a good practice?



In terms of scalability and maintainability, it's not a good practice to use the `instanceof` operator because it breaks the Open/Closed Principle and the Liskov Substitution Principle.

But, it's not a good practice to use the `instanceof` operator because it breaks the Open/Closed Principle and the Liskov Substitution Principle. 


but it's not a good practice to use the `instanceof` operator because it breaks the Open/Closed Principle and the Liskov Substitution Principle.


By the way, it's not a good practice to use the `instanceof` operator because it breaks the Open/Closed Principle and the Liskov Substitution Principle.

Why it breaks the Open/Closed Principle and the Liskov Substitution Principle?

The Open/Closed Principle states that a class should be open for extension but closed for modification. The `instanceof` operator breaks this principle because it requires the class to be modified whenever a new subclass is added.

The Liskov Substitution Principle states that objects of a superclass should be replaceable with objects of its subclasses without affecting the correctness of the program. The `instanceof` operator breaks this principle because it requires the class to check the type of the object before casting it to the subclass.


1. First, we need to check if the object is an instance of the `Event` class;
2. Then, we need to cast the object to the `Event` class;
3. Finally, we can access the `description` attribute.


But, it's not a good practice to use the `instanceof` operator because it breaks the Open/Closed Principle and the Liskov Substitution Principle.

Oh, 


it's not possible to do that because the `Event` class doesn't expose the description attribute.


```java

    public List findByDescriptionContains(String text) {
        List result = new ArrayList();
        for (Object event : events) {
            if (event instanceof Event) {
                Event e = (Event) event;
                if (e.description().contains(text)) {
                    result.add(e);
                }
            }
        }
        return result;
    }
    
```


An Event Store without query capabilities doesn't make sense, don't you? Let's