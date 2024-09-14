package com.example.eatsorderapplication.application.component;

import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalEventPublisher;
import reactor.core.publisher.Mono;

@Component
public class AfterCommitEventPublisher {
    private final ApplicationEventPublisher publisher;
    private TransactionalEventPublisher transactionalEventPublisher;

    public AfterCommitEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostConstruct
    public void init() {
        transactionalEventPublisher = new TransactionalEventPublisher(publisher);
    }

    public <T> Mono<T> publishEvent(T event) {
        return transactionalEventPublisher.publishEvent(event)
            .thenReturn(event);
    }
}