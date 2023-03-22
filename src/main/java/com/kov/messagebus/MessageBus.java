package com.kov.messagebus;

import com.kov.messagebus.exceptions.NoHandlerFoundException;
import com.kov.messagebus.handlers.CommandHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageBus implements MessageBusInterface {
    private final Map<Class<?>, List<MessageHandler>> handlers;
    private final List<Middleware> middlewares;
    private boolean allowNoHandlers;

    private MessageBus() {
        this.middlewares = new ArrayList<>();
        this.handlers = new HashMap<>();
        this.allowNoHandlers = false;
    }

    @Override
    public <T, R> R dispatch(T message) {
        List<MessageHandler> handlersForMessage = handlers.get(message.getClass());
        if (handlersForMessage == null || handlersForMessage.isEmpty()) {
            if (allowNoHandlers) {
                return null;
            } else {
                throw new NoHandlerFoundException("No handler found for message: " + message.getClass().getName());
            }
        }

        Middleware.Next<R> currentNext = () -> {
            R result = null;
            for (MessageHandler<T,R> handler : handlersForMessage) {
                result = handler.handle(message);
            }
            return result;
        };

        for (int i = middlewares.size() - 1; i >= 0; i--) {
            final Middleware middleware = middlewares.get(i);
            final Middleware.Next<R> next = currentNext;
            currentNext = () -> middleware.invoke(message, next);
        }

        return currentNext.invoke();
    }

    public MessageBus withHandlers(List<? extends MessageHandler> handlers) {
        for (MessageHandler handler : handlers) {
            this.handlers.computeIfAbsent(handler.getMessageType(), k -> new ArrayList<>()).add(handler);
        }
        return this;
    }

    public MessageBus withMiddlewares(List<? extends Middleware> middlewares) {
        this.middlewares.addAll(middlewares);
        return this;
    }

    public MessageBus withAllowNoHandlers(boolean allowNoHandlers) {
        this.allowNoHandlers = allowNoHandlers;
        return this;
    }

    public static MessageBus create() {
        return new MessageBus();
    }
}
