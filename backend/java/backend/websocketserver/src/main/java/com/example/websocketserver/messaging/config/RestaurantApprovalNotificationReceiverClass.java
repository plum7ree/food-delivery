package com.example.websocketserver.messaging.config;

import com.example.kafka.avro.model.RestaurantApprovalNotificationEvent;
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
    public Sinks.Many<RestaurantApprovalNotificationEvent> generalSink() {
        return Sinks.many().unicast().onBackpressureBuffer();
    }

    @Bean
    public Consumer<Flux<RestaurantApprovalNotificationEvent>> restaurantApprovalNotificationReceiver(
        @Qualifier("generalSink") Sinks.Many<RestaurantApprovalNotificationEvent> sink) {
        return flux -> flux.doOnNext(e -> log.info("restaurantApprovalNotificationReceiver {}", e.toString())).doOnNext(sink::tryEmitNext).subscribe();
    }
}
