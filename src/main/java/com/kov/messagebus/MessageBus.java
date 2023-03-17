package com.kov.messagebus;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MessageBus implements MessageBusInterface {
    private final Map<Class<?>, Object> handlers = new HashMap<>();

    public MessageBus() {
    }

    public MessageBus(String basePackage) {
        Set<Class<?>> handlerClasses = new Reflections(
                new ConfigurationBuilder()
                        .addUrls(ClasspathHelper.forPackage(basePackage))
                        .addScanners(Scanners.TypesAnnotated, Scanners.SubTypes))
                .getTypesAnnotatedWith(MessageHandler.class);

        handlerClasses.forEach(this::registerHandler);
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