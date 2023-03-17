package com.kov.messagebus.messages;

import com.kov.messagebus.MessageBusTest;
import com.kov.messagebus.MessageHandler;

@MessageHandler
public class PackageScanningTestMessageHandler {
    public String invoke(MessageBusTest.TestMessage message) {
        return "Processed: " + message.getContent();
    }

}
