![Java CI](https://github.com/arthurkowalsky/Java-Message-Bus/actions/workflows/java.yml/badge.svg)
[![codecov](https://codecov.io/gh/arthurkowalsky/Java-Message-Bus/branch/main/graph/badge.svg)](https://codecov.io/gh/arthurkowalsky/Java-Message-Bus)

# Message Bus Library

A simple and lightweight Java message bus library for handling communication between application components using messages and handlers.

## Features

- Easy-to-use message bus interface
- Automatic handler registration using annotations
- Supports any Java class as a message without requiring to implement interfaces
- Can be easily integrated into any Java project, including Spring Boot applications

## Usage

### 1. Add the dependency

Add the `message-bus` dependency to your project's `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>com.kov</groupId>
        <artifactId>message-bus</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```


### 2. Create message classes
   Create a message class for each type of message you want to handle:
```java
public class SmsNotification {
// Add any properties and methods specific to SmsNotification.
}
```

### 3. Create handler classes
Create a handler class for each message type and annotate it with @MessageHandler:

```java
import com.kov.messagebus.MessageHandler;

@MessageHandler
public class SmsNotificationHandler {
    public Void invoke(SmsNotification message) {
        // ... do some work - like sending an SMS message!
        return null;
    }
}
```

### 4. Initialize the message bus
Create a new MessageBus instance and provide the package name where your handlers are located:

```java
MessageBusInterface bus = new MessageBus("com.kov.handlers");
```

### 5. Invoke messages
Invoke messages using the invoke method on the message bus:
```java
bus.invoke(new SmsNotification());
```


## Integration with Spring Boot

### 1. Create a configuration class
Create a new configuration class to configure the message bus:

```java
import com.kov.messagebus.MessageBus;
import com.kov.messagebus.MessageBusInterface;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageBusConfiguration {

    @Bean
    public MessageBusInterface messageBus() {
        String basePackage = getBasePackage();
        return new MessageBus(getClass().getPackage().getName());
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

    private final MessageBusInterface messageBus;

    public ExampleController(MessageBusInterface messageBus) {
        this.messageBus = messageBus;
    }

    @GetMapping("/send-sms")
    public String sendSms() {
        messageBus.invoke(new SmsNotification());
        return "SMS sent";
    }
}
```


## License
This project is licensed under the [MIT License](https://chat.openai.com/chat/LICENSE.md).