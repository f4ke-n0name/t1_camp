package com.f4ken0name.github.audit.publishers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public class KafkaAuditPublisher implements AuditPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${audit.kafka.topic}")
    private String topic;

    @Override
    public void publish(String message) {
        kafkaTemplate.send(topic, message)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("Failed to send audit message to Kafka", throwable);
                    } else {
                        log.info("Audit message sent to Kafka topic {}", topic);
                    }
                });
    }
}
