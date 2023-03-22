package com.kov.messagebus;

public interface MessageBusInterface {
    <T, R> R dispatch(T message);
}
