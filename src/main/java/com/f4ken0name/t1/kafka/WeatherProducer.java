package com.f4ken0name.t1.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.List;



@Component
public class WeatherProducer {
    private static final Logger logger = LoggerFactory.getLogger(WeatherProducer.class);
    private final String TOPIC = "weather";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Random random = new Random();

    private static class TemperatureRange {
        final int min;
        final int max;
        TemperatureRange(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }

    private final Map<String, TemperatureRange> weatherConditions = Map.ofEntries(
            Map.entry("sunny", new TemperatureRange(-20, 40)),
            Map.entry("partly_cloudy", new TemperatureRange(-10, 35)),
            Map.entry("cloudy", new TemperatureRange(5, 23)),
            Map.entry("rainy", new TemperatureRange(0, 17)),
            Map.entry("thunderstorm", new TemperatureRange(5, 10)),
            Map.entry("snowy", new TemperatureRange(-20, -3)),
            Map.entry("sleet", new TemperatureRange(-5, 2)),
            Map.entry("freezing_rain", new TemperatureRange(-10, 0)),
            Map.entry("icy", new TemperatureRange(-20, -2)),
            Map.entry("foggy", new TemperatureRange(-10, 10)),
            Map.entry("windy", new TemperatureRange(-15, 22)),
            Map.entry("stormy", new TemperatureRange(5, 16)),
            Map.entry("overcast", new TemperatureRange(0, 20))
    );

    public WeatherProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage() {
        String condition = getRandomWeatherCondition();
        int temperature = generateTemperatureForCondition(condition);

        String message = String.format("{\"temperature\": %d, \"condition\": \"%s\"}",
                temperature, condition);

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Sent message: {} with offset: {}",
                        message, result.getRecordMetadata().offset());
            } else {
                logger.error("Unable to send message: {}", message, ex);
            }
        });
    }

    private String getRandomWeatherCondition() {
        List<String> conditions = List.copyOf(weatherConditions.keySet());
        return conditions.get(random.nextInt(conditions.size()));
    }

    private int generateTemperatureForCondition(String condition) {
        TemperatureRange range = weatherConditions.get(condition);
        return random.nextInt(range.max - range.min + 1) + range.min;
    }

    @Scheduled(fixedRate = 2000)
    public void generateMessagesPeriodically() {
        sendMessage();
    }
}