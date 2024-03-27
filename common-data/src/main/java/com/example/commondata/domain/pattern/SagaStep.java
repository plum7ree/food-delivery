package com.example.commondata.domain.pattern;


import com.example.commondata.domain.event.DomainEvent;

public interface SagaStep<T, SuccessEvent extends DomainEvent, FailEvent extends DomainEvent> {
    SuccessEvent process(T data);

    FailEvent rollback(T data);
}
