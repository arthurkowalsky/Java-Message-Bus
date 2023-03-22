package com.kov.messagebus;

import com.kov.messagebus.exceptions.NoHandlerFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest {

    private MessageBus messageBus;

    static class TestMessage {
        private final String content;

        TestMessage(String content) {
            this.content = content;
        }

        String getContent() {
            return content;
        }
    }

    static class TestMessageHandler implements MessageHandler<TestMessage, String> {
        @Override
        public String handle(TestMessage message) {
            return "Handler: " + message.getContent();
        }
    }

    static class TestMiddleware implements Middleware {
        private final String name;
        private final StringBuilder output;

        TestMiddleware(String name, StringBuilder output) {
            this.name = name;
            this.output = output;
        }

        @Override
        public <R, T> R invoke(T message, Next<R> next) {
            output.append(name).append(" before -> ");
            R result = next.invoke();
            output.append(name).append(" after -> ");
            return result;
        }
    }

    static class Query {
        public Query(String content) {
            this.content = content;
        }
        public String content;
    }

    static class QueryHandler implements MessageHandler<Query, String> {
        @Override
        public String handle(Query message) {
            return message.content + " QueryHandler";
        }

    }

    static class QueryMiddleware implements Middleware {

        private final String name;

        public QueryMiddleware(String name) {
            this.name = name;
        }

        @Override
        public <R, T> R invoke(T message, Next<R> next) {
            if (message instanceof Query) {
                Query query = (Query) message;
                query.content = query.content + " " + name;
            }
            return next.invoke();
        }
    }

    @BeforeEach
    void setUp() {
        messageBus = MessageBus.create();
    }

    @Test
    void testRegisterAndDispatchHandler() {
        messageBus.withHandlers(List.of(new TestMessageHandler()));

        String result = messageBus.dispatch(new TestMessage("John"));
        assertEquals("Handler: John", result);
    }

    @Test
    void testDispatchMessageWithoutHandlerThrowsException() {
        assertThrows(RuntimeException.class, () -> messageBus.dispatch(new TestMessage("NoHandlerMessage")));
    }

    @Test
    void testAllowNoHandlersOption() {
        messageBus.withAllowNoHandlers(true);
        assertNull(messageBus.dispatch(new TestMessage("NoHandlerMessage")));
    }

    @Test
    void testMiddlewareOrderAndInvocation() {
        StringBuilder middlewareOutput = new StringBuilder();

        TestMiddleware middleware1 = new TestMiddleware("Middleware1", middlewareOutput);
        TestMiddleware middleware2 = new TestMiddleware("Middleware2", middlewareOutput);

        messageBus.withMiddlewares(List.of(middleware1, middleware2))
                .withHandlers(List.of(new TestMessageHandler()));

        String result = messageBus.dispatch(new TestMessage("John"));
        assertEquals("Handler: John", result);
        assertEquals("Middleware1 before -> Middleware2 before -> Middleware2 after -> Middleware1 after -> ", middlewareOutput.toString());
    }

    @Test
    void testMiddlewaresModifyingMessage() {
        QueryMiddleware queryMiddleware1 = new QueryMiddleware("QueryMiddleware1");
        QueryMiddleware queryMiddleware2 = new QueryMiddleware("QueryMiddleware2");

        messageBus.withMiddlewares(List.of(queryMiddleware1, queryMiddleware2)).withHandlers(List.of(new QueryHandler()));

        String result = messageBus.dispatch(new Query("Query"));
        assertEquals("Query QueryMiddleware1 QueryMiddleware2 QueryHandler", result);
    }


    @Test
    void testMiddlewareThrowingException() {
        TestMiddleware middleware1 = new TestMiddleware("Middleware1", new StringBuilder()) {
            @Override
            public <R, T> R invoke(T message, Next<R> next) {
                throw new RuntimeException("Middleware1 error");
            }
        };

        messageBus.withMiddlewares(List.of(middleware1))
                .withHandlers(List.of(new TestMessageHandler()));

        assertThrows(RuntimeException.class, () -> messageBus.dispatch(new TestMessage("John")), "Middleware1 error");
    }

    @Test
    void testNoHandlerFoundException() {
        NoHandlerFoundException exception = assertThrows(NoHandlerFoundException.class, () -> messageBus.dispatch(new TestMessage("NoHandlerMessage")));
        assertEquals("No handler found for message: com.kov.messagebus.MessageBusTest$TestMessage", exception.getMessage());
    }

    @Test
    void testMultipleHandlersForSingleMessage() {
        messageBus.withHandlers(List.of(new TestMessageHandler(), new TestMessageHandlerExtra()));

        String result = messageBus.dispatch(new TestMessage("John"));
        assertEquals("Handler: John Extra", result);
    }

    static class TestMessageHandlerExtra implements MessageHandler<TestMessage, String> {
        @Override
        public String handle(TestMessage message) {
            return "Handler: " + message.getContent() + " Extra";
        }
    }

    @Test
    void testComplexMessageData() {
        messageBus.withHandlers(List.of(new ComplexMessageHandler()));

        ComplexMessage complexMessage = new ComplexMessage("John", 30, List.of("Java", "Python"));
        String result = messageBus.dispatch(complexMessage);
        assertEquals("Handler: John is 30 years old and knows Java, Python.", result);
    }

    static class ComplexMessage {
        private final String name;
        private final int age;
        private final List<String> skills;

        ComplexMessage(String name, int age, List<String> skills) {
            this.name = name;
            this.age = age;
            this.skills = skills;
        }

        String getName() {
            return name;
        }

        int getAge() {
            return age;
        }

        List<String> getSkills() {
            return skills;
        }
    }

    static class ComplexMessageHandler implements MessageHandler<ComplexMessage, String> {
        @Override
        public String handle(ComplexMessage message) {
            return "Handler: " + message.getName() + " is " + message.getAge() + " years old and knows " + String.join(", ", message.getSkills()) + ".";
        }
    }

    @Test
    void testNullMessageThrowsException() {
        messageBus.withHandlers(List.of(new TestMessageHandler()));
        assertThrows(NullPointerException.class, () -> messageBus.dispatch(null));
    }

    @Test
    void testInvalidMessageDataThrowsException() {
        messageBus.withHandlers(List.of(new InvalidMessageHandler()));
        assertThrows(IllegalArgumentException.class, () -> messageBus.dispatch(new TestMessage("Invalid")));
    }

    static class InvalidMessageHandler implements MessageHandler<TestMessage, String> {
        @Override
        public String handle(TestMessage message) {
            if ("Invalid".equals(message.getContent())) {
                throw new IllegalArgumentException("Invalid message content");
            }
            return "Handler: " + message.getContent();
        }
    }

}
