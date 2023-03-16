package com.kov.messagebus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageBusTest {
    private MessageBus messageBus;

    @BeforeEach
    void setUp() {
        messageBus = new MessageBus();
    }

    @Test
    void testRegisterHandlerAndInvoke() {
        messageBus.registerHandler(TestMessageHandler.class);

        String result = messageBus.invoke(new TestMessage("Hello, world!"));
        assertEquals("Processed: Hello, world!", result);
    }

    @Test
    void testInvokeWithoutHandler() {
        assertThrows(RuntimeException.class, () -> messageBus.invoke(new TestMessage("Hello, world!")));
    }

    @MessageHandler
    static class TestMessageHandler {
        public String invoke(TestMessage message) {
            return "Processed: " + message.getContent();
        }
    }

    static class TestMessage {
        private final String content;

        public TestMessage(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }
}