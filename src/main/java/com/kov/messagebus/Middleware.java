package com.kov.messagebus;

public interface Middleware {
    <R, T> R invoke(T message, Middleware.Next<R> next);

    interface Next<T> {
        T invoke();
    }
}
