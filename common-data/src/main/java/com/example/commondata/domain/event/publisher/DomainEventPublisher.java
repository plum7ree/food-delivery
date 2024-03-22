package com.example.commondata.domain.event.publisher;


import com.example.commondata.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {

    void publish(T domainEvent);
}
