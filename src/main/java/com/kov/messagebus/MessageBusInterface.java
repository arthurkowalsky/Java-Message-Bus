package com.kov.messagebus;

public interface MessageBusInterface {
    void registerHandler(Class<?> handlerClass);
    <T, R> R invoke(T message);
}
