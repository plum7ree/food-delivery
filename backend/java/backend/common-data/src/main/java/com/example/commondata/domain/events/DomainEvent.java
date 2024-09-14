package com.example.commondata.domain.events;

import java.time.Instant;

public interface DomainEvent {

    Instant createdAt();

}
