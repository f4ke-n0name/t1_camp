package com.f4ken0name.github.command.exceptions;

public class CommandRejectedException extends RuntimeException {
    public CommandRejectedException(String message) {
        super(message);
    }
}
