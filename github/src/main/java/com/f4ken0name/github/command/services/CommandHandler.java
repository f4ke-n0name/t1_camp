package com.f4ken0name.github.command.services;

import com.f4ken0name.github.command.exceptions.CommandValidationException;
import com.f4ken0name.github.command.executors.CommandExecutor;
import com.f4ken0name.github.command.models.Command;
import com.f4ken0name.github.command.utils.Priority;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

public class CommandHandler implements CommandService {
    private final Validator validator;
    private final ThreadPoolExecutor executor;

    public CommandHandler(Validator validator, ThreadPoolExecutor executor) {
        this.validator = validator;
        this.executor = executor;
    }

    public void handle(Command command) {
        validate(command);

        if (command.getPriority() == Priority.CRITICAL) {
            new CommandExecutor(command).run();
        } else {
            executor.execute(new CommandExecutor(command));
        }
    }

    private void validate(Command command) {
        Set<ConstraintViolation<Command>> violations = validator.validate(command);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("Validation failed");
            throw new CommandValidationException(message);
        }
    }
}
