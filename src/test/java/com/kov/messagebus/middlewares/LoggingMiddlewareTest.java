package com.kov.messagebus.middlewares;

import com.kov.messagebus.Middleware;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoggingMiddlewareTest {

    @Test
    void testInvoke() {
        LoggingMiddleware middleware = new LoggingMiddleware();

        // Mock the next middleware
        Middleware.Next<Object> next = () -> "Hello, World!";

        // Invoke the middleware with a message and the mocked next middleware
        String result = middleware.invoke("test message", next).toString();

        // Check if the result is correct
        Assertions.assertEquals("Hello, World!", result);
    }
}
