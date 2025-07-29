package com.f4ken0name.github.metrics;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ThreadPoolExecutor;

@RequiredArgsConstructor
public class AndroidBusynessMetric implements MeterBinder {

    private final ThreadPoolExecutor executor;

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("android.busyness", executor.getQueue(), q -> (double) q.size())
                .description("Current android task queue size")
                .register(registry);

        Counter.builder("android.completed")
                .description("Total number of tasks completed by the android")
                .register(registry)
                .increment(executor.getCompletedTaskCount());
    }
}
