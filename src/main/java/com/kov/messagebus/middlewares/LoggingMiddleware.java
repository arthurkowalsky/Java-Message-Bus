package com.kov.messagebus.middlewares;

import com.kov.messagebus.Middleware;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingMiddleware implements Middleware {

    private static final Logger logger = java.util.logging.Logger.getLogger(LoggingMiddleware.class.getName());

    @Override
    public <R, T> R invoke(T message, Next<R> next) {
        Class<?> messageType = message.getClass();

        logger.log(Level.INFO, "Starting handling message of type: {0}", messageType.getName());

        R result = next.invoke();

        logger.log(Level.INFO, "Finished handling message of type: {0}", messageType.getName());
        return result;
    }
}
