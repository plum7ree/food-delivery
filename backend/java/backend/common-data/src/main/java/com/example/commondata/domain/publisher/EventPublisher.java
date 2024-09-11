package com.example.commondata.domain.publisher;

import com.example.commondata.domain.events.DomainEvent;
import com.example.commondata.domain.outbox.Outbox;
import reactor.core.publisher.Flux;

public interface EventPublisher<T extends DomainEvent> {

    Flux<Outbox<T>> publish();

}