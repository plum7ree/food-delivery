package com.example.eatsorderapplication.messaging.processor;

import com.example.commondata.domain.events.DomainEvent;
import com.example.kafka.avro.model.*;
import reactor.core.publisher.Mono;

public interface OrderEventProcessor<R> extends EventProcessor<OrderEvent, R> {

    @Override
    default Mono<R> process(OrderEvent event) {
        if (event.getEvent() instanceof OrderCreated e) {
            return this.handle(e);
        } else if (event.getEvent() instanceof OrderApprovedByRestaurant e) {
            return this.handle(e);
        } else if (event.getEvent() instanceof OrderRejectedByRestaurant e) {
            return this.handle(e);
        } else if (event.getEvent() instanceof OrderCompleted e) {
            return this.handle(e);
        } else {
            return Mono.error(new IllegalArgumentException("Unknown event type: " + event));
        }
    }

    Mono<R> handle(OrderCreated event);

    Mono<R> handle(OrderApprovedByRestaurant event);

    Mono<R> handle(OrderRejectedByRestaurant event);

    Mono<R> handle(OrderCompleted event);


}
