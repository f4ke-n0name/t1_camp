package com.f4ken0name.t1.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class WeatherConsumer {
    private static final Logger logger = LoggerFactory.getLogger(WeatherConsumer.class);

    @KafkaListener(topics = "weather", groupId = "${kafka_group_id}")
    public void consumeWeatherData(
            String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        logger.info("Received message: {} from partition: {}", message, partition);
    }
}