package com.f4ken0name.github.command.executors;

import com.f4ken0name.github.command.models.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandExecutor implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(CommandExecutor.class);

    private final Command command;

    public CommandExecutor(Command command) {
        this.command = command;
    }

    @Override
    public void run() {
        log.info("Executing command: {}", command);
    }
}
