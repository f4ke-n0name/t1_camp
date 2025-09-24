package com.f4ken0name.github.audit.publishers;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleAuditPublisher implements AuditPublisher {
    @Override
    public void publish(String message) {
        log.info("AUDIT: {}", message);
    }
}