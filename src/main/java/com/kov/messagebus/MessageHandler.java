package com.kov.messagebus;

import java.lang.reflect.ParameterizedType;

public interface MessageHandler<T, R> {
    R handle(T message);

    default Class<T> getMessageType() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }
}
