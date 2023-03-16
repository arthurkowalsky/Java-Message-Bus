package com.kov.messagebus;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MessageBus implements MessageBusInterface {
    private final Map<Class<?>, Object> handlers;

    public MessageBus() {
        handlers = new HashMap<>();
    }

    @Override
    public void registerHandler(Class<?> handlerClass) {
        if (handlerClass.isAnnotationPresent(MessageHandler.class)) {
            try {
                for (Method method : handlerClass.getDeclaredMethods()) {
                    if ("invoke".equals(method.getName())) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (parameterTypes.length == 1) {
                            handlers.put(parameterTypes[0], handlerClass.getDeclaredConstructor().newInstance());
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to register handler: " + handlerClass.getName(), e);
            }
        }
    }

    @Override
    public <T, R> R invoke(T message) {
        Object handler = handlers.get(message.getClass());
        if (handler != null) {
            try {
                for (Method method : handler.getClass().getDeclaredMethods()) {
                    if ("invoke".equals(method.getName())) {
                        return (R) method.invoke(handler, message);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke handler for message: " + message.getClass().getName(), e);
            }
        }
        throw new RuntimeException("No handler registered for message: " + message.getClass().getName());
    }
}