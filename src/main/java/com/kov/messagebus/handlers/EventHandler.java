package com.kov.messagebus.handlers;

import com.kov.messagebus.MessageHandler;
import com.kov.messagebus.messages.Event;

public interface EventHandler<T extends Event> extends MessageHandler<T, Void> {
}
