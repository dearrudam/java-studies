<!--
## Make the life of your developer client's easier with smart object builders
-->

We use to listen "Make the life of your client easier". But, what about the developers? They are the ones who will be working with the code you write. It is important to make their life easier too.

Despite the target customer of your solution, you'll be creating code that will be maintained by other developers and, sometimes, it may include you as well. Choosing good approaches and technics will make their life, or your life, easier probably. Be an effective developer is not only about writing code that works, but also about writing code that is easy to read, easy to understand, and easy to maintain.

Talk about good practices and technics is a long conversation and, I'm not here to tell you what you must or not to do. I believe that the best approach depends on the context.

In this content, we'll discuss how to make the life of developers easier by using good strategies to build complex objects.

### The scenario: The complex object

A good way to learn things is by examples. So, here is our challenge:

We need to create a `Notification` object that has the following mandatory attributes:

- `title`: the title of the notification;
- `message`: the content of the notification;
- `recipient`: the person who will receive the notification;

and the optional attributes:

- `highPriority`: a flag to indicate if the notification is high priority. Default is `false`;
- `type`: the enum type of the notification. Supported values: `GENERAL`, `INFO`, `WARNING`, `ERROR`. Default is `Type.GENERAL`;
- `attachment`: a string with the path to the attachment file. Default is `null`;

Also, let's define more requirements:

- `title`: it's required and cannot be null;
- `message`: it's required and cannot be null;
- `recipient`: it's required and cannot be null;
- `highPriority`: it's optional and cannot be null;
- `type`: it's optional and cannot be null;

Here's an initial definition for our `Notification` class:

```java
public class Notification {
    
    private String title; // mandatory
    private String message; // mandatory
    private String recipient; // mandatory
    private boolean isHighPriority; // optional
    private Type type; // optional
    private String attachment; // optional

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }
    
    // omitted getters
}


```

Some developers would argue: "We can use the default constructor and setters to set the optional attributes". Let's try to follow this argument: 

```java
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.Objects.requireNonNull;

public class Notification {

    private String title; // mandatory
    private String message; // mandatory
    private String recipient; // mandatory
    private boolean highPriority; // optional
    private Type type = Type.GENERAL; // optional
    private String attachment; // optional

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }

    public void setTitle(String title) {
        this.title = requireNonNull(title, "title is required");
    }

    public String getTitle() {
        return title;
    }

    public void setMessage(String message) {
        this.message = requireNonNull(message, "message is required");
    }

    public String getMessage() {
        return message;
    }

    public void setRecipient(String recipient) {
        this.recipient = requireNonNull(recipient, "recipient is required");
    }

    public String getRecipient() {
        return recipient;
    }

    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Optional<Type> getType() {
        return ofNullable(type);
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
    
    public  Optional<String> getAttachment() {
        return attachment;
    }

    // omitted hash and equals methods 
}
```

Analyzing the code above, we can see that the developer must call the setters to set the attributes. The code to create a `Notification` object would be like this:

```java
public class NotificationProgram {
    
    public static void main(String[] args) {
        Notification notification = new Notification();
        notification.setTitle("New message");
        notification.setMessage("Hello, world!");
        notification.setRecipient("johndoe@system.com");
        notification.setHighPriority(true);
        notification.setType(Notification.Type.INFO);
        notification.setAttachment("/path/to/attachment.txt");
    }
}
```

Let's analyze the code above, we can highlight some drawbacks and issues with this approach:

1. **`Notification` objects can be instantiated with invalid state**: it violates the constraints of our challenge.

2. **`Notification` objects are not thread-safe**: the setters can be called by multiple threads at the same time, causing race conditions issues in multithreaded applications.

3. **The developer must call the setters to set the mandatory and optional attributes**: it's verbose and **error-prone** because the developer can forget to set an attribute, keeping the object in an invalid state;

3. **It's not clear**: the developer must read the documentation to know which attributes are mandatory and which are optional;

Also, we can highlight some good things on this approach: **developers are able to call the setters of the optional attributes as they need**.

Let's try to address the issues above.

1. **`Notification` objects can be instantiated with invalid state**: it violates the constraints of our challenge.

Okay, you would say: "It's not a big deal! We can use the constructor to set the mandatory attributes and the setters to set the optional attributes". Let's try to follow this argument:

```java
import static java.util.Optional.ofNullable;
import static java.util.Objects.requireNonNull;

public class Notification {

    private String title; // mandatory
    private String message; // mandatory
    private String recipient; // mandatory
    private boolean highPriority; // optional
    private Type type = Type.GENERAL; // optional
    private String attachment; // optional

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }

    public Notification(String title, String message, String recipient) {
        setTitle(title);
        setMessage(message);
        setRecipient(recipient);
    }
    
    public void setTitle(String title) {
        this.title = requireNonNull(title, "title is required");
    }

    public String getTitle() {
        return title;
    }

    public void setMessage(String message) {
        this.message = requireNonNull(message, "message is required");
    }

    public String getMessage() {
        return message;
    }

    public void setRecipient(String recipient) {
        this.recipient = requireNonNull(recipient, "recipient is required");
    }

    public String getRecipient() {
        return recipient;
    }

    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Optional<Type> getType() {
        return ofNullable(type);
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
    
    public  Optional<String> getAttachment() {
        return attachment;
    }

    // omitted hash and equals methods 
}
```

Let's update the `NotificationProgram` that creates a `Notification` object:

```java
public class NotificationProgram {

    public static void main(String[] args) {
        Notification notification = new Notification("New message", "Hello, world!", "johndoe@system.com");
        notification.setHighPriority(true);
        notification.setType(Notification.Type.INFO);
        notification.setAttachment("/path/to/attachment.txt");
    }
}
```

It looks like we solved the first issue, right? Well, let's analyze the code again:

With the changes the `Notification` objects will be instantiated with non-null references for the mandatory attributes. But, we still have some issues: **the `Notification` constructor with all mandatory attributes is error-prone!** Now, developers can set the attributes in the wrong order. For example, the developer can set the `recipient` before the `title`, which invalidate the object state making the fields holds the wrong value. Such issue only can be caught by debugging process or output derived from these invalid `Notification` objects.

Maybe two or three arguments with the same type is not a big deal, but in the perspective of the developers whose will use our class it's not something easier to deal with, and what if we have more attributes? The constructor will become even more complex and error-prone.

Let's continue trying to address the second issues:

2. **`Notification` objects are not thread-safe**: the setters can be called by multiple threads at the same time, causing race conditions issues in multithreaded applications.

The easiest way to make the `Notification` objects thread-safe is to make the getters and setters `synchronized` by using `Locks` from the `java.util.concurrent.locks` package. It would work, but it has some drawbacks to deal with:

- **It's not scalable and error-prone**: if it's needed to add more attributes, developers must have to make sure that any write and read state will be `synchronized`, which can lead to performance issues and undesired threads deadlock;
- **It's verbose**: the developer must write a lot of code to make the object thread-safe.
- **It's not clear**: the developer must read the documentation to know which attributes are thread-safe and which are not.
- **It's not efficient**: synchronizing access to read and write data can cause performance issues in multithreaded applications.

All of these concerns are needed just because a class that instantiate mutable objects. If your scenario requires mutable objects then it makes sense to put effort to deal with all of these concerns. Otherwise, if your scenario allows you to use immutable objects, then you can avoid all of these concerns.

That's the reason why I think that you must get clarity about the requirements of your solution before start coding. It will help you to choose the best approach to solve the problem. Let's come back to our `Notification` class and try to make it immutable.

Immutable objects are thread-safe by nature because they cannot be modified after creation. It's a good practice to make your objects immutable whenever possible.

We could use specific constructors to make the `Notification` object immutable. See below:

```java
import static java.util.Optional.ofNullable;
import static java.util.Objects.requireNonNull;

public class Notification {

    private final String title; // mandatory
    private final String message; // mandatory
    private final String recipient; // mandatory
    private final boolean highPriority; // optional
    private final Type type; // optional
    private final String attachment; // optional

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }
    
    public Notification(String title, 
                        String message, 
                        String recipient, 
                        boolean highPriority, 
                        Type type, 
                        String attachment) {
        this.title = requireNonNull(title, "title is required");
        this.message = requireNonNull(message, "message is required");
        this.recipient = requireNonNull(recipient, "recipient is required");
        this.highPriority = highPriority;
        this.type = ofNullable(type).orElse(Type.GENERAL);
        this.attachment = attachment;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public boolean isHighPriority() {
        return highPriority;
    }

    public Optional<Type> getType() {
        return ofNullable(type);
    }

    public  Optional<String> getAttachment() {
        return attachment;
    }

    // omitted hash and equals methods 
}
```

Now, let's update the `NotificationProgram` that creates a `Notification` object:

```java
public class NotificationProgram {

    public static void main(String[] args) {
        Notification notification = new Notification(
                "New message", 
                "Hello, world!", 
                "johndoe@system.com", 
                true, 
                Notification.Type.INFO, 
                "/path/to/attachment.txt");
    }
}
```

Now, the `Notification` objects are immutable and thread-safe. The developer can instantiate the object with all mandatory and optional attributes in a single line of code. The object will be created in a valid state, and the developer cannot change its state after creation. 

Since Java 16, we can use the `record` keyword to create immutable objects. If you're using Java 16 or above, I highly recommend you to use Java Records to create immutable objects. Let's see how we can refactor the `Notification` class to become a Java Record:

```java
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public record Notification(
        String title,
        String message,
        String recipient,
        boolean highPriority,
        Type type,
        String attachment) {

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }
    
    public Notification {
        requireNonNull(title, "title is required");
        requireNonNull(message, "message is required");
        requireNonNull(recipient, "recipient is required");
        type = ofNullable(type).orElse(Type.GENERAL);
    }
}
```

Less code, more readability, and more maintainability. That's the power of Java Records.

Coming back to our challenge, something is still not right: if `highPriority`, and `type`, and `attachment` are optional attributes, why it's required to provide each value in the constructor?

We can solve this issue by using a traditional approach that it's called **Telescoping constructors**.

### The traditional approach: Telescoping constructors

A common approach to object creation is to provide multiple constructors with different numbers of parameters. Each constructor calls another constructor with the required parameters and sets the optional parameters to default values. It's called **telescoping constructors**. You can use this approach on any java class, including Java Records.

Let's try to follow this approach. Let's see them.

```java
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public record Notification(
        String title,
        String message,
        String recipient,
        boolean highPriority,
        Type type,
        String attachment) {

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR
    }
    
    public Notification (String title, String message, String recipient) {
        this(title, message, recipient, false, Type.GENERAL, null);
    }
    
    public Notification {
        requireNonNull(title, "title is required");
        requireNonNull(message, "message is required");
        requireNonNull(recipient, "recipient is required");
        type = ofNullable(type).orElse(Type.GENERAL);
    }
}
```
Now developers will be able to create `Notification` objects with only the mandatory attributes. The optional attributes will be set to default values. The `Notification` objects are immutable and thread-safe.

Let's update the `NotificationProgram` that creates a `Notification` object:

```java
public class NotificationProgram {

    public static void main(String[] args) {
        var notificationWithDefaultOptionalValues =
                new Notification(
                        "New message", 
                        "Hello, world!", 
                        "johndoe@system.com");
        
        var notificationWithCustomOptionalValues =
                new Notification(
                        "Another message",
                        "Oh no! Something wrong happened",
                        "johndoe@system.com",
                        true,
                        Notification.Type.ERROR,
                        "/path/to/attachment.txt");
    }
}
```

Great! Let's review the issues we had and how we solved them:

1. **`Notification` objects only can be instantiated with valid state**: the constructors ensure that the object will be created in a valid state;

2. **`Notification` objects are thread-safe**: the immutability ensure to us this capability.

Well, the third one about the verbosity and error-prone of the constructors is still there yet. The fourth issue item, about the documentation, can help developers to know which constructor should be used with its argument ordering, but we can do more to make it easier.

### Favor Static Factories Methods over Class Constructors

To help developers to know which constructor should be used, we can use static method factories to create objects. Static method factories are static methods that return an instance of a given class or its subtype. They can have names that describe the object being returned, making it easier for developers to know which constructor should be used.

Some advantages of using static method factories are:

- **Static method factories have names that describe the object being returned**: the developer can know which constructor should be used by reading the method name;
- **Static method factories don't need to create a new object on even invocation**: it can return the same object if the object is immutable saving memory and CPU resources;
- **Static method factories can return any object of subtype from the own return type**: it can return a subtype of the class, making it easier to create objects with different configurations;

Before to put our finger in the code, let's think about how to apply the static method factories on our challenge. According to the static method factory concept we can create a static method factory for each combination of attributes. It even will make the developer's life easier, but how do we should implement these static method factories?

In fact, for our challenge, if we concentrate to provide static method factories for all possible combinations, it would be resulting in a big class with many static method factories. It's about 16 variations of static method factories! Maybe it would be not a good idea to have many static factories methods in a class. Maybe it makes the class harder to maintain and understand. Let's change our point of view: instead of cover statically all possible combinations, we can provide static method factories with the attributes that would be composing possible combinations. Let's see them.

```java
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public record Notification(
        String title,
        String message,
        String recipient,
        boolean highPriority,
        Type type,
        String attachment) {

    public static enum Type {
        GENERAL, INFO, WARNING, ERROR;
    }

    public static Notification createNotification(Type type, String title, String message, String recipient) {
        return new Notification(title, message, recipient, false, type, null);
    }

    public static Notification createHighPriorityNotification(Type type, String title, String message, String recipient) {
        return new Notification(title, message, recipient, true, type, null);
    }

    public static Notification createNotificationWithAttachment(Type type, String title, String message, String recipient, String attachment) {
        return new Notification(title, message, recipient, false, type, attachment);
    }

    public static Notification createHighPriorityNotificationWithAttachment(Type type, String title, String message, String recipient, String attachment) {
        return new Notification(title, message, recipient, true, type, attachment);
    }

    public Notification {
        requireNonNull(title, "title is required");
        requireNonNull(message, "message is required");
        requireNonNull(recipient, "recipient is required");
        type = ofNullable(type).orElse(Type.GENERAL);
    }
    
}
```
Now, developers can create `Notification` objects using static method factories. The static method factories have names that describe the object being returned, making it easier for developers to know which constructor should be used. The `Notification` objects are immutable and thread-safe.

Let's update the `NotificationProgram` that creates a `Notification` object:

```java

public class NotificationProgram {

    public static void main(String[] args) {
        var generalNotificationWithoutAttachment = Notification
                .createNotification(
                        Notification.Type.GENERAL,
                        "General Notification",
                        "This is a general notification",
                        "johndoe@system.com");

        var highPrioryInfoNotification = Notification
                .createHighPriorityNotification(
                        Notification.Type.INFO,
                        "High Priority Info Notification",
                        "This is a high priority info notification",
                        "johndoe@system.com");
    }
}
```

Great! We're improving our code step by step. Maybe it's even good shape for some cases already, but I'm sure that we can do better!

In our implementation, each static method factory is requiring four arguments. It's not a big deal, but what if we have more attributes? The static method factories will become even more complex and error-prone. Let's try to address this issue. 

### Many parameters? Use the Builder pattern

### Restricts the order of method calls in the Builder pattern

### Conclusion

### Key Takeaways

### Final Thoughts

### Next steps

Congratulations on reaching the end of this content! I hope you found it informative and helpful.

The learned concepts in this content are essential for understanding the motivation behind using generics in Java. Generics are a powerful feature that allows you to write more flexible, type-safe code by providing compile-time type checking. 

Did you like this content? If so, please share it with your friends and colleagues. Also, don't forget to follow me on social media to stay up to date with the latest content and updates.

See you in the next content!
