package com.example.restaurantapprovalservice.config;


import com.example.commondata.message.MessageConverter;
import com.example.kafka.avro.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;


@Slf4j
@Configuration
public class Processors {


    public Processors() {

    }


    public Mono<OrderEvent> process(RestaurantEvent event) {
        if (event.getEvent() instanceof RequestApproval requestApproval) {
            return Mono.just(OrderEvent.newBuilder()
                .setCorrelationId(requestApproval.getOrderId())
                .setEvent(OrderApprovedByRestaurant.newBuilder()
                    .setOrderId(requestApproval.getOrderId())
                    .setCreatedAt(requestApproval.getCreatedAt())
                    .build())
                .build());
        } else if (event.getEvent() instanceof UserCancelled userCancelled) {
            // Handle UserCancelled event if needed
            return Mono.empty(); // or some other processing
        }
        return Mono.empty();
    }

    @Bean
    public Function<Flux<Message<RestaurantEvent>>, Flux<Message<OrderEvent>>> processor() {
        return flux -> flux.map(MessageConverter::toRecord)
            .doOnNext(r -> log.info("restaurant service received {}", r.message()))
            .concatMap(r -> process(r.message())
                .doOnSuccess(e -> r.acknowledgement().acknowledge())
            )
            .map(this::toMessage);
    }


    private Message<OrderEvent> toMessage(OrderEvent event) {
        return MessageBuilder.withPayload(event)
            .setHeader(KafkaHeaders.KEY, event.getCorrelationId().toString())
            .build();
    }
}