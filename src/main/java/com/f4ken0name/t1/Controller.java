package com.f4ken0name.t1;

import com.f4ken0name.t1.kafka.WeatherProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class Controller {
    private WeatherProducer weatherProducer;

    public Controller(WeatherProducer weatherProducer) {
        this.weatherProducer = weatherProducer;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generate() {
        try {
            weatherProducer.sendMessage();
            return ResponseEntity.ok("Weather data generated and sent to Kafka");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error generating weather data: " + e.getMessage());
        }
    }
}