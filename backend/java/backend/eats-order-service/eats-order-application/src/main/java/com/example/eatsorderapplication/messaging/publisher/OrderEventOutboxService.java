package com.example.eatsorderapplication.messaging.publisher;

import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderEventOutboxService extends EventPublisher<OrderEvent> {

    Mono<Void> deleteEvents(List<Long> ids);

}
