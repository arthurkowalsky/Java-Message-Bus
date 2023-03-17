package com.kov.messagebus;

import com.kov.messagebus.messages.PackageScanningTestMessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageBusTest {
    private MessageBus messageBus;

    @BeforeEach
    void setUp() {
        messageBus = new MessageBus();
    }
    @Test
    void testConstructorWithPackageScanning() {
        MessageBus messageBus = new MessageBus(Test.class.getPackage().getName());

        assertInstanceOf(MessageBus.class, messageBus);
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

    @Test
    public void testRegisterHandlerThrowsExceptionOnHandlerCreationFailure() {
        Class<?> handlerClass = BrokenTestMessageHandler.class;

        Exception exception = assertThrows(RuntimeException.class, () -> messageBus.registerHandler(handlerClass));
        assertTrue(exception.getMessage().contains("Failed to register handler"));
    }

    @Test
    public void testInvokeThrowsExceptionWhenHandlerInvocationFails() {
        messageBus.registerHandler(FailingTestMessageHandler.class);

        TestMessage testMessage = new TestMessage("hello world");

        Exception exception = assertThrows(RuntimeException.class, () -> messageBus.invoke(testMessage));
        assertTrue(exception.getMessage().contains("Failed to invoke handler for message"));
    }

    @Test
    void testRegisterHandlerWithoutAnnotation() {
        messageBus.registerHandler(NoAnnotationTestMessageHandler.class);

        assertThrows(RuntimeException.class, () -> messageBus.invoke(new TestMessage("Hello, world!")));
    }

    @Test
    void testRegisterHandlerWithTwoParameters() {
        messageBus.registerHandler(TwoParametersTestMessageHandler.class);

        assertThrows(RuntimeException.class, () -> messageBus.invoke(new TestMessage("Hello, world!")));
    }

    @Test
    void testInvokeHandlerWithDifferentMethodNames() {
        messageBus.registerHandler(DifferentMethodNamesTestMessageHandler.class);

        String result = messageBus.invoke(new TestMessage("Hello, world!"));
        assertEquals("Processed: Hello, world!", result);
    }

    @MessageHandler
    static class BrokenTestMessageHandler {
        public BrokenTestMessageHandler() throws Exception {
            throw new Exception("Failed to create the handler");
        }

        public Void invoke(TestMessage message) {
            // This method will not be called since the constructor fails.
            return null;
        }
    }

    @MessageHandler
    static class FailingTestMessageHandler {
        public Void invoke(TestMessage message) {
            throw new RuntimeException("Failed to invoke the handler");
        }
    }

    @MessageHandler
    static class TestMessageHandler {
        public String invoke(TestMessage message) {
            return "Processed: " + message.getContent();
        }
    }

    static class NoAnnotationTestMessageHandler {
        public String invoke(TestMessage message) {
            return "This should not be called";
        }
    }

    @MessageHandler
    static class TwoParametersTestMessageHandler {
        public String invoke(TestMessage message, String extraParameter) {
            return "This should not be called";
        }
    }

    @MessageHandler
    static class DifferentMethodNamesTestMessageHandler {
        public String invoke(TestMessage message) {
            return "Processed: " + message.getContent();
        }

        public String anotherMethod(TestMessage message) {
            return "This should not be called";
        }
    }

    public static class TestMessage {
        private final String content;

        public TestMessage(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }
}