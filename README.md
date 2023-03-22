![Java CI](https://github.com/arthurkowalsky/Java-Message-Bus/actions/workflows/java.yml/badge.svg)
[![codecov](https://codecov.io/gh/arthurkowalsky/Java-Message-Bus/branch/main/graph/badge.svg)](https://codecov.io/gh/arthurkowalsky/Java-Message-Bus)

# Message Bus Library

A simple and lightweight Java message bus library for handling communication between application components using messages and handlers.

## Features

- Easy-to-use message bus interface
- Supports any Java class as a message without requiring to implement interfaces
- Middleware support for message processing
- Can be easily integrated into any Java project


## Integration with Spring Boot

### 1. Create a configuration class
Create a new configuration class to configure the message buses:

```java
import com.kov.messagebus.MessageBus;
import com.kov.messagebus.MessageBusInterface;
import com.kov.messagebus.handlers.CommandHandler;
import com.kov.messagebus.handlers.EventHandler;
import com.kov.messagebus.handlers.QueryHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.ObjectProvider;

@Configuration
public class MessageBusConfiguration {

    @Bean
    public MessageBusInterface commandBus(ObjectProvider<CommandHandler> handlers) {
        return MessageBus.create().withHandlers(handlers.stream().toList());
    }

    @Bean
    public MessageBusInterface queryBus(ObjectProvider<QueryHandler> handlers) {
        return MessageBus.create().withHandlers(handlers.stream().toList());
    }

    @Bean
    public MessageBusInterface eventBus(ObjectProvider<EventHandler> handlers) {
        return MessageBus.create().withAllowNoHandlers(true)
                .withHandlers(handlers.stream().toList());
    }

}
```

### 2. Use the message bus in your application
Now you can use the message bus in your application by injecting the MessageBusInterface and invoking messages:

```java
package com.example;

import com.kov.messagebus.MessageBusInterface;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

    private final MessageBusInterface commandBus;

    public ExampleController(MessageBusInterface commandBus) {
        this.commandBus = commandBus;
    }

    @GetMapping("/send-sms")
    public String sendSms() {
        commandBus.dispatch(new SmsNotificationCommand());
        return "SMS sent";
    }
}
```

## Usage

### 1. Add the dependency

Add the `message-bus` dependency to your project's `pom.xml`:
```xml
<dependencies>
    <dependency>
        <groupId>com.kov</groupId>
        <artifactId>message-bus</artifactId>
        <version>2.0.0</version>
    </dependency>
</dependencies>
```

### 2. Create message classes

Create a message class for each type of message you want to handle, such as commands, events, and queries:
```java
import com.kov.messagebus.messages.Command;
import com.kov.messagebus.messages.Event;
import com.kov.messagebus.messages.Query;

public class SmsNotificationCommand implements Command{
// Add any properties and methods specific to SmsNotificationCommand.
}

public class SmsWasSentEvent implements Event{
// Add any properties and methods specific to SmsWasSentEvent.
}

public class GetNotificationsListsQuery implements Query{
// Add any properties and methods specific to GetSmsListsQuery.
}
```

### 3. Create handler classes

Create a handler class for each message type that implements the appropriate `MessageHandler` interface:

```java
import com.kov.messagebus.MessageBusInterface;
import com.kov.messagebus.handlers.CommandHandler;
import com.kov.messagebus.handlers.QueryHandler;

public class SmsNotificationCommandHandler implements CommandHandler<SmsNotificationCommand> {
    MessageBusInterface eventBus;

    public SmsNotificationCommandHandler(MessageBusInterface eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Void handle(SmsNotificationCommand message) {
        // ... do some work - like sending an SMS message!
        this.eventBus.dispatch(new SmsWasSentEvent());
    }
}

public class GetNotificationsListsQueryHandler implements QueryHandler<GetNotificationsListsQuery, String[]> {
    @Override
    public String[] handle(GetNotificationsListsQuery query) {
        // ... fetch notifications from the database
        return ["Notification1", "Notification 2"];
    }
}
```
### 4. Initialize the message bus

Create a new `MessageBus` instance and register the handlers:

```java
MessageBusInterface bus = MessageBus.create();
bus.withHandlers(List.of(new SmsNotificationCommandHandler()));
```

### 5. Invoke messages

Invoke messages using the `dispatch` method on the message bus:

```java
bus.dispatch(new SmsNotificationCommand());
```

## Middleware

You can also create custom middleware classes that implement the `Middleware` interface and add them to the message bus:

```java
import com.kov.messagebus.Middleware;
import com.kov.messagebus.Next;

public class LoggingMiddleware implements Middleware {
    @Override
    public <R, T> R invoke(T message, Next<R> next) {
        System.out.println("Middleware: " + message.getClass().getSimpleName());
        return next.invoke();
    }
}
```

Register the middleware with the message bus:

```java
bus.withMiddlewares(List.of(new LoggingMiddleware()));
```

## License
This project is licensed under the [MIT License](https://chat.openai.com/chat/LICENSE.md).