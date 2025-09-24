package com.f4ken0name.github.audit.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "audit")
public class AuditProperties {
    public enum Mode {
        CONSOLE,
        KAFKA
    }
    private Mode mode = Mode.CONSOLE;
    private String kafkaTopic = "android-audit-log";
}
