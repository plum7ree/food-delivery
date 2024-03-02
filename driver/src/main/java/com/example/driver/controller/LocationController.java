package com.example.driver.controller;

import com.example.driver.stream.config.KafkaStreamsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class LocationController {

    @Autowired
    private KafkaTemplate<Long, Object> kafkaTemplate;

    @GetMapping("/mono/{value}")
    public Mono<String> monoExample(@PathVariable String value) {
        kafkaTemplate.
        return Mono.just("Mono with value: " + value);
    }

    @GetMapping("/flux")
    public Flux<String> fluxExample() {
        return Flux.just("Value 1", "Value 2", "Value 3");
    }
}