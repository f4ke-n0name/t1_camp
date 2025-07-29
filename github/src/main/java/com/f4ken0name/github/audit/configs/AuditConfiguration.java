package com.f4ken0name.github.audit.configs;

import com.f4ken0name.github.audit.properties.AuditProperties;
import com.f4ken0name.github.audit.publishers.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableConfigurationProperties(AuditProperties.class)
public class AuditConfiguration {

    @Bean
    @ConditionalOnProperty(name = "audit.mode", havingValue = "kafka")
    public AuditPublisher kafkaAuditPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                              AuditProperties properties) {
        return new KafkaAuditPublisher(kafkaTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "audit.mode", havingValue = "console", matchIfMissing = true)
    public AuditPublisher consoleAuditPublisher() {
        return new ConsoleAuditPublisher();
    }
}
