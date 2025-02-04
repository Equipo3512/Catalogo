package com.Demo.catalogo.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class MetricsActuator {

    private final Timer timer;

    public MetricsActuator(MeterRegistry meterRegistry) {
        this.timer = meterRegistry.timer("catalogo.obtener_todas_las_peliculas");
    }

    public Timer getTimer() {
        return timer;
    }
}
