package com.f4ken0name.github.command.services;

import com.f4ken0name.github.command.models.Command;

public interface CommandService {
    void handle(Command command);
}
