package com.f4ken0name.github.command.exceptions;

public class CommandQueueOverflowException extends RuntimeException {
    public CommandQueueOverflowException(String message) {
        super(message);
    }
}
