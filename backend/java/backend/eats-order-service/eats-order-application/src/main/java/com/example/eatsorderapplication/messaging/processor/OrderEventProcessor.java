package com.example.eatsorderapplication.messaging.processor;

import com.example.commondata.domain.events.DomainEvent;
import com.example.commondata.domain.events.order.OrderEvent;
import reactor.core.publisher.Mono;

public interface OrderEventProcessor<R extends DomainEvent> extends EventProcessor<OrderEvent, R> {

    @Override
    default Mono<R> process(OrderEvent event) {
        if (event instanceof OrderEvent.OrderCreated e) {
            return this.handle(e);
        } else if (event instanceof OrderEvent.OrderRejectedByRestaurant e) {
            return this.handle(e);
        } else if (event instanceof OrderEvent.OrderCompleted e) {
            return this.handle(e);
        } else {
            return Mono.error(new IllegalArgumentException("Unknown event type: " + event));
        }
    }

    Mono<R> handle(OrderEvent.OrderCreated event);

    Mono<R> handle(OrderEvent.OrderApprovedByRestaurant event);

    Mono<R> handle(OrderEvent.OrderRejectedByRestaurant event);

    Mono<R> handle(OrderEvent.OrderCompleted event);


}
