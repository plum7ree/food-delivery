package com.example.commondata.domain.outbox;

import com.example.commondata.domain.events.DomainEvent;
import lombok.Builder;

@Builder
public record Outbox<T extends DomainEvent>(Long correlationId,
                                            T event) {
}
