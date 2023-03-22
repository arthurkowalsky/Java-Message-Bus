package com.kov.messagebus.handlers;

import com.kov.messagebus.MessageHandler;
import com.kov.messagebus.messages.Query;

public interface QueryHandler<T extends Query, R> extends MessageHandler<T, R> {
}
