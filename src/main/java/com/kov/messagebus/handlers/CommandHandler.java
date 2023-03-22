package com.kov.messagebus.handlers;

import com.kov.messagebus.MessageHandler;
import com.kov.messagebus.messages.Command;

public interface CommandHandler<T extends Command> extends MessageHandler<T, Void> {
}
