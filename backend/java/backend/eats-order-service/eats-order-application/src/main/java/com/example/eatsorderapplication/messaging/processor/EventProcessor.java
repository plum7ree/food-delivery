package com.example.eatsorderapplication.messaging.processor;

import reactor.core.publisher.Mono;

public interface EventProcessor<T, R> {

    Mono<R> process(T event);

}
