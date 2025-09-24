package com.f4ken0name.github.command.configs;


import com.f4ken0name.github.command.properties.CommandProperties;
import com.f4ken0name.github.command.services.CommandHandler;
import com.f4ken0name.github.metrics.AndroidBusynessMetric;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Validator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
@EnableConfigurationProperties(CommandProperties.class)
public class CommandConfiguration {

    @Bean
    public ThreadPoolExecutor commandExecutor(CommandProperties properties) {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(properties.getQueueCapacity());

        return new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                60L,
                TimeUnit.SECONDS,
                queue,
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        throw new RejectedExecutionException("Command queue overflow");
                    }
                }
        );
    }

    @Bean
    public CommandHandler commandHandler(Validator validator, ThreadPoolExecutor executor) {
        return new CommandHandler(validator, executor);
    }

    @Bean
    public AndroidBusynessMetric androidBusynessMetric(ThreadPoolExecutor executor, MeterRegistry registry) {
        AndroidBusynessMetric metric = new AndroidBusynessMetric(executor);
        metric.bindTo(registry);
        return metric;
    }
}
