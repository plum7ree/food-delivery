package com.example.commondata.domain.event;

public interface DomainEvent<T> {
    void fire();
}
