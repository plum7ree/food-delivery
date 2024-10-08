package com.example.websocketserver.messaging.config;

import com.example.commondata.message.MessageConverter;
import com.example.commondata.message.Record;
import com.example.kafka.avro.model.DriverMatchedEvent;
import com.example.kafka.avro.model.UserNotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
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
    @Qualifier("matchedDriverEventSink")
    public Sinks.Many<DriverMatchedEvent> matchedDriverEventSink() {
        return Sinks.many().unicast().onBackpressureBuffer();
    }

    @Bean
    public Consumer<Flux<Message<UserNotificationEvent>>> restaurantApprovalNotificationReceiver(
        @Qualifier("generalSink") Sinks.Many<UserNotificationEvent> sink) {
        return flux -> flux
            .map(MessageConverter::toRecord)
            .doOnNext(Record::acknowledgement) // todo before ack() save a message and check status if needed
            .doOnNext(m -> log.info("restaurantApprovalNotificationReceiver {}", m.message().toString()))
            .doOnNext(m -> sink.tryEmitNext(m.message()))
            .subscribe();
    }

    @Bean
    public Consumer<Flux<Message<DriverMatchedEvent>>> matchedDriverProcessor(
        @Qualifier("matchedDriverEventSink") Sinks.Many<DriverMatchedEvent> sink) {
        return flux -> flux
            .map(MessageConverter::toRecord)
            .doOnNext(Record::acknowledgement) // todo before ack() save a message and check status if needed
            .doOnNext(m -> log.info("matchedDriverProcessor {}", m.message().toString()))
            .doOnNext(m -> sink.tryEmitNext(m.message()))
            .subscribe();

    }
}
