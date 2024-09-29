package com.example.websocketserver.messaging.config;

import com.example.kafka.avro.model.DriverMatchedEvent;
import com.example.kafka.avro.model.UserNotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class RestaurantApprovalNotificationReceiverClass {
    @Bean
    @Qualifier("generalSink")
    public Sinks.Many<UserNotificationEvent> generalSink() {
        return Sinks.many().unicast().onBackpressureBuffer();
    }

    @Bean
    public Consumer<Flux<UserNotificationEvent>> restaurantApprovalNotificationReceiver(
        @Qualifier("generalSink") Sinks.Many<UserNotificationEvent> sink) {
        return flux -> flux.doOnNext(e -> log.info("restaurantApprovalNotificationReceiver {}", e.toString())).doOnNext(sink::tryEmitNext).subscribe();
    }

    @Bean
    public Consumer<Flux<DriverMatchedEvent>> matchedDriverProcessor() {
        return flux -> Flux.empty();
    }
}
