<!--
## The Challenges of Using 'Object' as a Catch-All Type in Java
-->
During a mentoring session with a mentee developer where we got started to talk about Java Generics, we realized that some concepts need to be mastered before than talk about Java Generics. Suddenly, a question came up: "Why it is not a good practice to use `Object` as a catch-all type in Java?" 

IMHO this question is very interesting and that's the reason why I'm covering this subject in this content.

Okay, let's get started!

As a Java developer, you should know that `java.lang.Object` is the root of the class hierarchy. Every class inherits from `Object`, including arrays. This means that all objects are, by default, instances of `Object`.

!["Object is the root of the class hierarchy"](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/dd6ucncgbjdtgdis3sfm.png)

Well, if every class inherits from `Object` then why is it not a good practice to use `Object` as a catch-all type in Java? Let's check it out!

### Using Object to Hold Any Type of Object

When declaring variables, you can use `Object` as the type to hold any object when the specific type is unknown.

```java
    Object infoA = "Maximillian"; // Works fine, because String is an Object!
    Object infoB = 45; // Works fine, because Integer is an Object!
```

Such declarations can be part of a valid Java program, as shown below:

```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = "Maximillian"; // Works fine, because String is an Object!
        System.out.println(infoA);
    }
}
```

The output of this program will be:

```shell
$ java ObjectAsCatchAllTypeProgram.java
Maximillian
```

This happens because `String` is a subclass of `Object`, and the compiler does not complain about it. Additionally, you can change the value of `infoA` to an object of any type, and the program will still work fine:

```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = 3.14; // Works fine, because Double is an Object!
        System.out.println(infoA);
    }
}
```

The output will be:

```shell
$ java ObjectAsCatchAllTypeProgram.java
3.14
```

As we can see, the program works regardless of the type of object assigned to the `infoA` variable.

But what are the benefits of using `Object` as a type? Some developers might say, "It makes the code more flexible and reusable." But is it true?

### Limitations of Using Object: Lack of type safety

Yeah, there is no "silver bullet" in the software development area. Every decision has its pros and cons. Let's explore the limitations of using `Object` as a catch-all type.

Regardless of the type of object, you can only interact with it through its **interface**. In this context the "interface" word means the exposed methods declared on its class.

A **class** is a blueprint for an object. When you define a class, you're specifying its type, the structure and behavior of the objects created from it. The class defines the attributes (data) and methods (behavior) that the objects will have. These methods allow objects to interact with one application.

The `Object` class includes some common methods, such as `toString()`, `equals()`, and `hashCode()`. These methods are useful in many cases, but they are not sufficient when you want to interact with specific methods of a particular class.

Let’s suppose we are holding a `String` object in a variable declared as `Object`, and we want to get the length of the assigned value. Here's what happens:

```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = "Maximillian"; // Works fine, because String is an Object, but...
        System.out.println(infoA.length());
    }
}
```

Trying to run the code above results in:

```shell
$ java ObjectAsCatchAllTypeProgram.java
ObjectAsCatchAllTypeProgram.java:5: error: cannot find symbol
        System.out.println(infoA.length());
                                ^
  symbol:   method length()
  location: variable infoA of type Object
1 error
error: compilation failed
```

When you declare a variable as `Object`, you lose type safety. Type safety is a feature of Java that prevents you from assigning an object of one type to a variable of application type. This feature helps catch errors at compile time, making your code more reliable. In the example above, the compiler doesn't know that `infoA` is a `String` object, so it doesn't allow you to call the `length()` method on it by raising a **compilation error**.

### Limitations of Using Object: The need for explicit casting

To interact with an object as its specific type, you need to cast it to that type. Casting is the process of converting an object from one type to application. In Java, you can cast an object to a subclass or superclass type. Let’s cast `infoA` to `String` before calling the `length()` method to fix the **compilation failed** showed before:

```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = "Maximillian"; // Works fine, because String is an Object, but...
        String name = (String) infoA;
        System.out.println(name.length());
    }
}
```

The output will now be:

```shell
$ java ObjectAsCatchAllTypeProgram.java
11
```

At this point, you might think, "It's not a big deal; I can just cast the object to its type before interacting with it." But can we be sure?

### Limitations of Using Object: Susceptibility to runtime errors

Let’s take a look at a different scenario:

```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = 11; // Works fine, because Integer is an Object, but...
        String name = (String) infoA;  // It's not a String, it's an Integer!!!
        System.out.println(name.length());
    }
}
```

Running this results in:

```shell
$ java ObjectAsCatchAllTypeProgram.java
Exception in thread "main" java.lang.ClassCastException: class java.lang.Integer cannot be cast to class java.lang.String (java.lang.Integer and java.lang.String are in module java.base of loader 'bootstrap')
        at ObjectAsCatchAllTypeProgram.main(ObjectAsCatchAllTypeProgram.java:4)
```

A **ClassCastException** is thrown because we're trying to cast an `Integer` object to a `String` object. `Integer` is not a subclass of `String`, so the cast fails. This is a **runtime error**.

Let’s explore application case:

```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = null; // Oops, a null value?!?!
        String name = (String) infoA;
        System.out.println(name.length());
    }
}
```

This will result in:

```shell
$ java ObjectAsCatchAllTypeProgram.java
Exception in thread "main" java.lang.NullPointerException: Cannot invoke "String.length()" because "<local2>" is null
        at ObjectAsCatchAllTypeProgram.main(ObjectAsCatchAllTypeProgram.java:5)
```

A **NullPointerException** occurs because we're trying to call a method on a null reference.

You might think, "I can easily fix this using a try-catch block or the `instanceof` operator." Let’s try to handle it with both approaches:

Using a `try-catch` block:

```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = null; // Oops, a null value?!?!
        try {
            String name = (String) infoA;
            System.out.println(name.length());
        } catch (NullPointerException ex) {
            System.out.println("infoA cannot be cast because it's null");
        } catch (ClassCastException ex) {
            System.out.println("infoA cannot be cast to String");
        }
    }
}
```

Output:

```shell
$ java ObjectAsCatchAllTypeProgram.java
infoA cannot be cast because it's null
```

Using the `instanceof` operator:

```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = null; // Oops, a null value?!?!
        if (infoA instanceof String) {
            String name = (String) infoA;
            System.out.println(name.length());
        } else {
            System.out.println("infoA cannot be cast to String");
        }
    }
}
```

Output:

```shell
$ java ObjectAsCatchAllTypeProgram.java
infoA cannot be cast to String
```

While these strategies can help, they are not practical to use every time you need to interact with an object.

Using `instanceof` is a good practice when you need to check the type of an object before casting it. However, it can make your code more complex and harder to read. The `try-catch` block is useful for handling exceptions, but it can also make your code more verbose and harder to maintain.

If you're using Java 14 or above, we can use the Pattern Matching for Instanceof ([JEP305](https://openjdk.java.net/jeps/305)) feature. Such built-in language enhancement helps us to write better and more readable code.

```java
class ObjectAsCatchAllTypeProgram {
    public static void main(String[] args) {
        Object infoA = null; 
        if (infoA instanceof String name) { // more concise, isn't it? :-)
            System.out.println(name.length());
        } else {
            System.out.println("infoA cannot be cast to String");
        }
    }
}
```
Here's the output:

```shell
$ java ObjectAsCatchAllTypeProgram.java
infoA cannot be cast to String
```

### Conclusion

We learned that being declaring variables as `java.lang.Object` is generally considered bad practice unless there is a specific, compelling reason. Using `Object` sacrifices type safety, readability, and maintainability. In most cases, it’s better to use a more specific type to take full advantage of Java's strong typing system.

During our exploration, we discovered that **compilation errors** and **runtime errors** can occur when using `Object` as a catch-all type. Let’s recap the differences between these two types of errors:

- **Compilation errors** happen during the compilation phase when the code cannot be converted into bytecode. These errors prevent the program from running.

- **Runtime errors** occur after successful compilation and can cause the program to behave unpredictably or crash. Runtime errors are typically more problematic because they can affect production environments.

While both types of errors indicate issues, **runtime errors** are usually more severe because they can affect users and cause unexpected behavior, while **compilation errors** are easier to resolve during development.

A good practice is to implement a good error handling strategy to deal with runtime errors, capturing the exceptions and logging them properly to help you debug and fix the issues.

### Key Takeaways

Through this content, we explored the challenges of using `Object` as a catch-all type. We learned that using `Object` can lead to:

- **Lack of type safety:** The compiler doesn't know the specific type of an object declared as `Object`, so it can't catch type-related errors at compile time. This can lead to runtime errors when interacting with the object.


- **The need for explicit casting:** To interact with an object as its specific type, you need to cast it to that type. This can make your code more complex and harder to read.


- **Susceptibility to runtime errors:** Using `Object` as a catch-all type can lead to runtime errors, such as `ClassCastException` and `NullPointerException`. These errors can be difficult to track, debug, and fix, especially in large codebases.


- **The need for additional error handling:** To prevent runtime errors, you might need to use `try-catch` blocks or the `instanceof` operator. While these strategies can help, they can make your code more verbose and harder to maintain. I recommend using a good error handler strategy to deal with runtime errors, capturing the exceptions and logging them properly to help you debug and fix the issues.

### Final Thoughts

Not always using `Object` as catch-all type is the best solution. In some cases, using `Object` can be more appropriate. For example, when you have no control over the type of object that will be manipulated. However, it is important to understand the limitations and challenges associated with using `Object` and know when it is appropriate to use it. 

When questions about what's right or wrong in the software development area comes to any discussion, we use to see developers answering like that: "It depends on the context that you're handling with." And they are right! 

But, once you know the context, "it depends" is not so valid answer. The problem context should guide us to make a good decision!

So, favor using more specific types whenever possible to take full advantage of Java's strong typing system. This will help you write more readable, maintainable, and reliable code. 

What do you think about using `Object` as a catch-all type in Java? Do you have any experiences or best practices to share? Feel free to leave your thoughts in the comments below!

### Next steps

Congratulations on reaching the end of this content! I hope you found it informative and helpful.

The learned concepts in this content are essential for understanding the motivation behind using generics in Java. Generics are a powerful feature that allows you to write more flexible, type-safe code by providing compile-time type checking. 

Did you like this content? If so, please share it with your friends and colleagues. Also, don't forget to follow me on social media to stay up to date with the latest content and updates.

See you in the next content, where we will explore the basics of generics in Java!
